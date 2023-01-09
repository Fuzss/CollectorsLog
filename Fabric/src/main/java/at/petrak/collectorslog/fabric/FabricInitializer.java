package at.petrak.collectorslog.fabric;

import at.petrak.collectorslog.CollectorsLogConfig;
import at.petrak.collectorslog.api.CollectorsLogAPI;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public class FabricInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ForgeConfigRegistry.INSTANCE.register(CollectorsLogAPI.MOD_ID, ModConfig.Type.CLIENT, CollectorsLogConfig.INSTANCE.spec);
    }
}
