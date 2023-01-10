package at.petrak.collectorslog.handler;

import at.petrak.collectorslog.CollectorsLog;
import at.petrak.collectorslog.client.gui.screens.GuiCollectorsLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class AddButtonHandler {
    @Nullable
    private static AbstractWidget recipeBookButton;
    @Nullable
    private static AbstractWidget collectorsLogButton;

    public static void onScreenInit$Post(Screen screen, Minecraft minecraft, int width, int height, List<Renderable> renderables, Consumer<AbstractWidget> addWidget) {
        recipeBookButton = findRecipeBookButton(renderables);
        if (recipeBookButton == null) {
            CollectorsLog.LOGGER.warn("Couldn't find the recipe book button");
        } else {
            collectorsLogButton = new ImageButton(recipeBookButton.getX() + recipeBookButton.getWidth() + 8, recipeBookButton.getY(), 20, 18, 208, 0, GuiCollectorsLog.TEXTURE_LOC, button -> {
                Minecraft.getInstance().setScreen(new GuiCollectorsLog(screen, minecraft.player, minecraft.player.connection.enabledFeatures(), minecraft.options.operatorItemsTab().get()));
            });
            addWidget.accept(collectorsLogButton);
        }
    }

    private static AbstractWidget findRecipeBookButton(List<Renderable> renderables) {
        for (Renderable r : renderables) {
            if (r instanceof ImageButton ib) {
                return ib;
            }
        }
        return null;
    }

    public static void mouseClicked(Screen screen, double mouseX, double mouseY, int buttonId) {
        if (collectorsLogButton != null && recipeBookButton != null) {
            collectorsLogButton.setPosition(recipeBookButton.getX() + recipeBookButton.getWidth() + 8, recipeBookButton.getY());
        }
    }
}
