package com.wells.datashare.client;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.wells.datashare.DataShareMod;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Windows named-pipe server: CreateNamedPipe → wait for external reader → write NDJSON lines.
 * External software only connects and reads; it must not create the pipe.
 */
public final class NamedPipeServer {
    private final String pipePath;
    private final AtomicReference<String> latestLine = new AtomicReference<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public NamedPipeServer(String pipePath) {
        this.pipePath = pipePath;
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name", "");
        return os.toLowerCase().contains("win");
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        worker = new Thread(this::loop, "data-share-mod");
        worker.setDaemon(true);
        worker.start();
    }

    public void stop() {
        running.set(false);
        if (worker != null) {
            worker.interrupt();
        }
    }

    public void publish(String jsonLine) {
        latestLine.set(jsonLine);
    }

    private void loop() {
        while (running.get()) {
            WinNT.HANDLE pipe = Kernel32.INSTANCE.CreateNamedPipe(
                    pipePath,
                    WinBase.PIPE_ACCESS_OUTBOUND,
                    WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,
                    1,
                    256 * 1024,
                    16 * 1024,
                    0,
                    null
            );

            if (WinBase.INVALID_HANDLE_VALUE.equals(pipe) || pipe == null) {
                int err = Kernel32.INSTANCE.GetLastError();
                DataShareMod.LOGGER.warn(
                        "[{}] CreateNamedPipe failed, error={} — retry in 2s",
                        DataShareMod.MOD_ID, err);
                sleep(2000);
                continue;
            }

            DataShareMod.LOGGER.info(
                    "[{}] Waiting for external reader on {}",
                    DataShareMod.MOD_ID, pipePath);

            boolean connected = Kernel32.INSTANCE.ConnectNamedPipe(pipe, null);
            if (!connected) {
                int err = Kernel32.INSTANCE.GetLastError();
                if (err != 535) {
                    DataShareMod.LOGGER.warn(
                            "[{}] ConnectNamedPipe failed, error={}",
                            DataShareMod.MOD_ID, err);
                    Kernel32.INSTANCE.CloseHandle(pipe);
                    sleep(500);
                    continue;
                }
            }

            DataShareMod.LOGGER.info("[{}] External reader connected", DataShareMod.MOD_ID);
            writeWhileConnected(pipe);
            Kernel32.INSTANCE.FlushFileBuffers(pipe);
            Kernel32.INSTANCE.DisconnectNamedPipe(pipe);
            Kernel32.INSTANCE.CloseHandle(pipe);
            DataShareMod.LOGGER.info("[{}] Reader disconnected; recreating pipe", DataShareMod.MOD_ID);
        }
    }

    private void writeWhileConnected(WinNT.HANDLE pipe) {
        String lastSent = null;
        while (running.get()) {
            String line = latestLine.get();
            if (line == null || line.equals(lastSent)) {
                sleep(20);
                continue;
            }

            // Each snapshot is one complete pretty JSON object, followed by a blank line delimiter.
            byte[] payload = (line + "\n\n").getBytes(StandardCharsets.UTF_8);
            IntByReference written = new IntByReference();
            boolean ok = Kernel32.INSTANCE.WriteFile(pipe, payload, payload.length, written, null);
            if (!ok || written.getValue() <= 0) {
                int err = Kernel32.INSTANCE.GetLastError();
                DataShareMod.LOGGER.info(
                        "[{}] WriteFile ended (error={}), waiting for next reader",
                        DataShareMod.MOD_ID, err);
                return;
            }
            lastSent = line;
        }
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
