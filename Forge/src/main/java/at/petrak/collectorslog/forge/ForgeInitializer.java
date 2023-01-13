package at.petrak.collectorslog.forge;

import at.petrak.collectorslog.CollectorsLog;
import at.petrak.collectorslog.config.CollectorsLogConfig;
import at.petrak.collectorslog.client.handler.CollectorsLogButtonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(CollectorsLog.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeInitializer {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CollectorsLogConfig.INSTANCE.getSpec());
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.Init.Post evt) -> {
            Screen screen = evt.getScreen();
            if (evt.getScreen() instanceof InventoryScreen) {
                CollectorsLogButtonHandler.onScreenInit$Post$1(screen, Minecraft.getInstance(), screen.width, screen.height, screen.renderables, evt::addListener);
            }
            if (evt.getScreen() instanceof PauseScreen) {
                CollectorsLogButtonHandler.onScreenInit$Post$2(screen, Minecraft.getInstance(), screen.width, screen.height, screen.renderables, evt::addListener);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.MouseButtonPressed.Post evt) -> {
            if (evt.getScreen() instanceof InventoryScreen && evt.wasHandled()) {
                CollectorsLogButtonHandler.onMouseClicked$Post(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            }
        });
    }
}
