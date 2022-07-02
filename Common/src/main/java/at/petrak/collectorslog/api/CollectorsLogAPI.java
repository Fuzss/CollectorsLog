package at.petrak.collectorslog.api;


import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CollectorsLogAPI {
    String MOD_ID = "collectorslog";
    Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    static ResourceLocation modLoc(String s) {
        return new ResourceLocation(MOD_ID, s);
    }
}
