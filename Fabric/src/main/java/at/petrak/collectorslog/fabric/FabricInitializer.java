package at.petrak.collectorslog.fabric;

import at.petrak.collectorslog.CollectorsLog;
import at.petrak.collectorslog.config.CollectorsLogConfig;
import at.petrak.collectorslog.handler.AddButtonHandler;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.fml.config.ModConfig;

public class FabricInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ForgeConfigRegistry.INSTANCE.register(CollectorsLog.MOD_ID, ModConfig.Type.CLIENT, CollectorsLogConfig.INSTANCE.getSpec());
        registerHandlers();
    }

    private static void registerHandlers() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen) {
                AddButtonHandler.onScreenInit$Post(screen, client, scaledWidth, scaledHeight, Screens.getButtons(screen).stream().map(w -> (Renderable) w).toList(), Screens.getButtons(screen)::add);
                ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                    AddButtonHandler.mouseClicked(screen, mouseX, mouseY, button);
                });
            }
        });
    }
}
