package at.petrak.collectorslog.mixin;

import at.petrak.collectorslog.api.CollectorsLogAPI;
import at.petrak.collectorslog.gui.GuiCollectorsLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {
    @Inject(method = "init", at = @At("RETURN"))
    public void cl$init(CallbackInfo ci) {
        var self = (InventoryScreen) (Object) this;

        var renderables = ((AccessorScreen) self).cl$getRenderables();
        ImageButton recipeBookButton = null;
        for (var r : renderables) {
            if (r instanceof ImageButton ib) {
                recipeBookButton = ib;
                break;
            }
        }

        if (recipeBookButton == null) {
            CollectorsLogAPI.LOGGER.warn("Couldn't find the recipe book button");
        } else {
            ((AccessorScreen) self).cl$addRenderableWidget(
                new ImageButton(recipeBookButton.x + recipeBookButton.getWidth() + 2,
                    recipeBookButton.y, 20, 18, 192, 0, GuiCollectorsLog.TEXTURE_LOC, button -> {
                    Minecraft.getInstance().setScreen(new GuiCollectorsLog(self));
                }));
        }
    }
}
