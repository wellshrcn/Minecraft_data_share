package com.wells.datashare.client;

import com.wells.datashare.DataShareMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        MinecraftForge.EVENT_BUS.register(new ClientBootstrap());
        DataShareMod.LOGGER.info(
                "[{}] Pipe server started at {}",
                DataShareMod.MOD_ID,
                DataShareMod.PIPE_PATH);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || pipeServer == null) {
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
