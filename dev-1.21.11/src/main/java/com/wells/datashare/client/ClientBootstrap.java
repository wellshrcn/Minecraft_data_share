package com.wells.datashare.client;

import com.wells.datashare.DataShareMod;
import net.minecraftforge.event.TickEvent;

/**
 * Physical-client bootstrap. Kept in a separate class so client-only
 * references stay off the dedicated-server classpath.
 */
public final class ClientBootstrap {
    private static NamedPipeServer pipeServer;
    private static int tickCounter;

    private ClientBootstrap() {
    }

    public static void init() {
        if (!NamedPipeServer.isWindows()) {
            DataShareMod.LOGGER.error(
                    "[{}] Named pipes require Windows. Mod will idle without exporting.",
                    DataShareMod.MOD_ID);
            return;
        }

        pipeServer = new NamedPipeServer(DataShareMod.PIPE_PATH);
        pipeServer.start();
        TickEvent.ClientTickEvent.Post.BUS.addListener(ClientBootstrap::onClientTick);
        DataShareMod.LOGGER.info(
                "[{}] Pipe server started at {}",
                DataShareMod.MOD_ID,
                DataShareMod.PIPE_PATH);
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        if (pipeServer == null) {
            return;
        }

        tickCounter++;
        if (tickCounter % 2 != 0) {
            return;
        }

        String json = PlayerDataCollector.collectJsonLine();
        if (json != null) {
            pipeServer.publish(json);
        }
    }
}
