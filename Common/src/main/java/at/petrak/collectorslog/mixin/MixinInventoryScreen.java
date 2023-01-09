package at.petrak.collectorslog.mixin;

import at.petrak.collectorslog.api.CollectorsLogAPI;
import at.petrak.collectorslog.gui.GuiCollectorsLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(InventoryScreen.class)
abstract class MixinInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> {
    @Unique
    private AbstractWidget collectorslog$recipeBookButton;
    @Unique
    private AbstractWidget collectorslog$collectorsLogButton;

    public MixinInventoryScreen(InventoryMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void cl$init(CallbackInfo ci) {
        InventoryScreen self = (InventoryScreen) (Object) this;

        List<Renderable> renderables = ((AccessorScreen) self).cl$getRenderables();
        this.collectorslog$recipeBookButton = null;
        for (Renderable r : renderables) {
            if (r instanceof ImageButton ib) {
                this.collectorslog$recipeBookButton = ib;
                break;
            }
        }

        if (this.collectorslog$recipeBookButton == null) {
            CollectorsLogAPI.LOGGER.warn("Couldn't find the recipe book button");
        } else {
            this.collectorslog$collectorsLogButton = ((AccessorScreen) self).cl$addRenderableWidget(new ImageButton(this.collectorslog$recipeBookButton.getX() + this.collectorslog$recipeBookButton.getWidth() + 8, this.collectorslog$recipeBookButton.getY(), 20, 18, 208, 0, GuiCollectorsLog.TEXTURE_LOC, button -> {
                Minecraft.getInstance().setScreen(new GuiCollectorsLog(self, this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()));
            }));
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    public void mouseClicked(double $$0, double $$1, int $$2, CallbackInfoReturnable<Boolean> callback) {
        if (this.collectorslog$collectorsLogButton != null && this.collectorslog$recipeBookButton != null) {
            if (callback.getReturnValueZ()) {
                this.collectorslog$collectorsLogButton.setPosition(this.collectorslog$recipeBookButton.getX() + this.collectorslog$recipeBookButton.getWidth() + 8, this.collectorslog$recipeBookButton.getY());
            }
        }
    }
}
