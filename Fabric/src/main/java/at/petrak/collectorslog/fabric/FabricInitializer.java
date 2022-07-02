package at.petrak.collectorslog.fabric;

import at.petrak.collectorslog.api.CollectorsLogAPI;
import net.fabricmc.api.ClientModInitializer;

public class FabricInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CollectorsLogAPI.LOGGER.info("Yep we are live");
    }
}
