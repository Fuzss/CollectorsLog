package at.petrak.collectorslog.forge;

import at.petrak.collectorslog.api.CollectorsLogAPI;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(CollectorsLogAPI.MOD_ID)
public class ForgeInitializer {
    public static ForgeConfig CONFIG;

    public ForgeInitializer() {
        var config = new ForgeConfigSpec.Builder().configure(ForgeConfig::new);
        CONFIG = config.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, config.getRight());
    }
}
