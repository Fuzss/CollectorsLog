package at.petrak.collectorslog.fabric;

import net.fabricmc.api.ClientModInitializer;

public class FabricInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricConfig.setup();
    }
}
