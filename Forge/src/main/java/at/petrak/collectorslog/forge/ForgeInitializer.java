package at.petrak.collectorslog.forge;

import at.petrak.collectorslog.CollectorsLogConfig;
import at.petrak.collectorslog.api.CollectorsLogAPI;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(CollectorsLogAPI.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeInitializer {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CollectorsLogConfig.INSTANCE.spec);
    }
}
