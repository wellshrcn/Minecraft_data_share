package com.wells.datashare;

import com.mojang.logging.LogUtils;
import com.wells.datashare.client.ClientBootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

/**
 * Client-only Data Share Mod.
 * Follows Forge sides guidance: https://docs.minecraftforge.net/en/1.20.1/concepts/sides/
 */
@Mod(DataShareMod.MOD_ID)
public final class DataShareMod {
    public static final String MOD_ID = "data_share";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Windows named pipe path created by this mod (external apps connect as readers). */
    public static final String PIPE_PATH = "\\\\.\\pipe\\data_share";

    public DataShareMod(FMLJavaModLoadingContext context) {
        context.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (remoteVersion, isFromServer) -> true
                )
        );

        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            throw new UnsupportedOperationException(
                    MOD_ID + " is client-only and must not be installed on a dedicated server.");
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientBootstrap.init();
        }
    }
}
