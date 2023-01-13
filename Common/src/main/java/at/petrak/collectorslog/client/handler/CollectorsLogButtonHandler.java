package at.petrak.collectorslog.client.handler;

import at.petrak.collectorslog.CollectorsLog;
import at.petrak.collectorslog.client.gui.screens.CollectorsLogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CollectorsLogButtonHandler {
    @Nullable
    private static AbstractWidget recipeBookButton;
    @Nullable
    private static AbstractWidget collectorsLogButton;

    public static void onScreenInit$Post$1(Screen screen, Minecraft minecraft, int width, int height, List<Renderable> renderables, Consumer<AbstractWidget> addWidget) {
        recipeBookButton = findRecipeBookButton(renderables);
        if (recipeBookButton == null) {
            CollectorsLog.LOGGER.warn("Couldn't find the recipe book button");
        } else {
            collectorsLogButton = new ImageButton(recipeBookButton.getX() + recipeBookButton.getWidth() + 8, recipeBookButton.getY(), 20, 18, 208, 0, CollectorsLogScreen.TEXTURE_LOC, button -> {
                minecraft.setScreen(new CollectorsLogScreen(screen, minecraft.player, minecraft.player.connection.enabledFeatures(), minecraft.options.operatorItemsTab().get()));
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

    public static void onMouseClicked$Post(Screen screen, double mouseX, double mouseY, int buttonId) {
        if (collectorsLogButton != null && recipeBookButton != null) {
            collectorsLogButton.setPosition(recipeBookButton.getX() + recipeBookButton.getWidth() + 8, recipeBookButton.getY());
        }
    }

    public static void onScreenInit$Post$2(Screen screen, Minecraft minecraft, int width, int height, List<Renderable> renderables, Consumer<AbstractWidget> addWidget) {
        int buttonX = width / 2 + 4 + 98 + 4;
        int buttonY = height / 4 + 48 + -16;
        String[] vanillaButtons = {"gui.stats", "menu.returnToGame", "menu.reportBugs", "menu.shareToLan"};
        for (String translationKey : vanillaButtons) {
            final Optional<Button> menuButton = getButton(renderables, translationKey);
            if (menuButton.isPresent()) {
                final Button otherButton = menuButton.get();
                buttonX = otherButton.getX() + otherButton.getWidth() + 4;
                buttonY = otherButton.getY();
                break;
            }
        }
        addWidget.accept(new ImageButton(buttonX, buttonY, 20, 20, 80, 198, 20, CollectorsLogScreen.NEW_INDEX_LOCATION, 512, 256, ($$0x) -> {
            minecraft.setScreen(new CollectorsLogScreen(screen, minecraft.player, minecraft.player.connection.enabledFeatures(), minecraft.options.operatorItemsTab().get()));
        }));
    }

    private static Optional<Button> getButton(List<Renderable> renderables, String translationKey) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof Button button && matchesTranslationKey(button, translationKey)) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }

    private static boolean matchesTranslationKey(Button button, String translationKey) {
        return button.getMessage().getContents() instanceof TranslatableContents contents && contents.getKey().equals(translationKey);
    }
}
