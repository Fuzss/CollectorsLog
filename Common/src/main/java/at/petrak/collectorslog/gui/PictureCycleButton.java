package at.petrak.collectorslog.gui;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class PictureCycleButton<T> extends AbstractButton implements TooltipAccessor {
    private List<T> cycle;
    private int index = 0;

    private final ResourceLocation texture;
    private final int ux;
    private final int vy;
    private final int dvy;

    public PictureCycleButton(int x, int y, List<T> cycle, ResourceLocation texture, int ux, int vy, int dvy) {
        super(x, y, 20, 20, Component.empty());
        Preconditions.checkArgument(!cycle.isEmpty(), "Cannot give 0 elements");
        this.cycle = cycle;

        this.texture = texture;
        this.ux = ux;
        this.vy = vy;
        this.dvy = dvy;
    }

    public abstract Component getTooltipFromValue();

    public abstract void onChange();

    public T getValue() {
        return this.cycle.get(this.index);
    }

    @Override
    public List<FormattedCharSequence> getTooltip() {
        return List.of(this.getTooltipFromValue().getVisualOrderText());
    }

    @Override
    public void onPress() {
        if (Screen.hasShiftDown()) {
            this.index--;
        } else {
            this.index++;
        }
        this.index = Mth.positiveModulo(this.index, this.cycle.size());

        this.onChange();
    }

    @Override
    public void renderButton(PoseStack ps, int mouseX, int mouseY, float partialTick) {
        super.renderButton(ps, mouseX, mouseY, partialTick);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        this.blit(ps, this.x + 2, this.y + 2, this.ux, this.vy + this.dvy * this.index, 16, 16);
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }
}
