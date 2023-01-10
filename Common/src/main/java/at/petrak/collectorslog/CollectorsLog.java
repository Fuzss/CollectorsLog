package at.petrak.collectorslog;


import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectorsLog {
    public static final String MOD_ID = "collectorslog";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String s) {
        return new ResourceLocation(MOD_ID, s);
    }
}
