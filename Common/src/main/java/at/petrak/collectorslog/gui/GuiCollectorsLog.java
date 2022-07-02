package at.petrak.collectorslog.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static at.petrak.collectorslog.api.CollectorsLogAPI.modLoc;

public class GuiCollectorsLog extends Screen implements StatsUpdateListener {
    public static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
    public static final ResourceLocation TEXTURE_LOC = modLoc("textures/gui/collectorslog.png");
    private static final int BOOK_TEX_WIDTH = 192;
    private static final int BOOK_TEX_HEIGHT = 128;

    public static final int BOOK_WIDTH = 336;
    public static final int BOOK_HEIGHT = 224;

    @Nullable
    private final Screen previous;
    private boolean isLoading = true;

    private Set<Item> collectedItems = new HashSet();
    private NonNullList<ItemStack> allItemsInRegistryOrder = NonNullList.create();
    private ArrayList<ItemStack> displayedItems = new ArrayList<>();

    private PictureCycleButton<SortMode> sortModeButton = null;
    private PictureCycleButton<Boolean> reverseSortButton = null;
    private PictureCycleButton<FilterMode> filterModeButton = null;

    public GuiCollectorsLog(@Nullable Screen previous) {
        super(Component.translatable("gui.collectorslog"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        this.isLoading = true;
        this.minecraft.getConnection()
            .send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
    }

    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.isLoading = false;

            this.initWidgets();

            this.collectedItems.clear();
            this.allItemsInRegistryOrder.clear();
            Registry.ITEM.iterator().forEachRemaining(item -> {
                item.fillItemCategory(CreativeModeTab.TAB_SEARCH, this.allItemsInRegistryOrder);
                var foundCount = this.minecraft.player.getStats().getValue(Stats.ITEM_PICKED_UP, item);
                if (foundCount > 0) {
                    this.collectedItems.add(item);
                }
            });
        }
    }

    private void initWidgets() {
        var rightMargin = (width - BOOK_WIDTH) / 2 + BOOK_WIDTH / 2 - 24;
        var buttonHeight = (height - BOOK_HEIGHT) / 2 + BOOK_HEIGHT / 8;

        this.sortModeButton = new PCBInternal<SortMode>(rightMargin, buttonHeight, List.of(SortMode.values()), 0, 128) {
            @Override
            public Component getTooltipFromValue() {
                return Component.translatable("gui.collectorslog.sortmode." + this.getValue().key);
            }
        };
        this.reverseSortButton = new PCBInternal<Boolean>(rightMargin - 22, buttonHeight, List.of(false, true), 16,
            128) {
            @Override
            public Component getTooltipFromValue() {
                return Component.translatable("gui.collectorslog.sortreverse." + this.getValue());
            }
        };
        this.filterModeButton = new PCBInternal<FilterMode>(rightMargin - 44, buttonHeight,
            List.of(FilterMode.values()), 32,
            128) {
            @Override
            public Component getTooltipFromValue() {
                return Component.translatable("gui.collectorslog.filtermode." + this.getValue().key);
            }
        };
        this.addRenderableWidget(this.sortModeButton);
        this.addRenderableWidget(this.reverseSortButton);
        this.addRenderableWidget(this.filterModeButton);
    }

    private void sortItems() {
        this.displayedItems.clear();

        var sortMode = this.sortModeButton.getValue();
        boolean reverseSort = this.reverseSortButton.getValue();
        var filterMode = this.filterModeButton.getValue();

        for (var stack : this.allItemsInRegistryOrder) {
            if (filterMode.allow(stack, this.collectedItems)) {
                this.displayedItems.add(stack);
            }
        }
        sortMode.sort(this.displayedItems);
        if (reverseSort) {
            Lists.reverse(this.displayedItems);
        }
    }

    @Override
    public void render(PoseStack ps, int mx, int my, float partialTicks) {
        this.renderBackground(ps);
        ps.pushPose();
        ps.translate((width - BOOK_WIDTH) / 2.0, (height - BOOK_HEIGHT) / 2.0, 1);
        this.renderBg(ps, mx, my, partialTicks);

        var titleLocX = BOOK_WIDTH / 4; // the *center* of the *left half* of the book
        var titleLocY = BOOK_HEIGHT / 16;
        if (this.isLoading) {
            drawCenteredString(ps, this.font, PENDING_TEXT, titleLocX, titleLocY, -1);
            String loadingSymbol = LOADING_SYMBOLS[(int) (Util.getMillis() / 150L % (long) LOADING_SYMBOLS.length)];
            drawCenteredString(ps, this.font, loadingSymbol, titleLocX, titleLocY + 9 * 2, 0xff_dddddd);
        } else {
            drawCenteredString(ps, this.font, Component.translatable(
                "gui.collectorslog.progress",
                this.collectedItems.size(), this.allItemsInRegistryOrder.size(),
                String.format("%.0f", 100.0 * this.collectedItems.size() / this.allItemsInRegistryOrder.size())
            ), titleLocX, titleLocY, -1);
        }

        ps.popPose();

        super.render(ps, mx, my, partialTicks);

        var kid = this.getChildAt(mx, my);
        kid.ifPresent(kiddo -> {
            if (kiddo instanceof TooltipAccessor tt) {
                var tooltip = tt.getTooltip();
                this.renderTooltip(ps, tooltip, mx, my);
            }
        });
    }

    private void renderBg(PoseStack ps, int mx, int my, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOC);
        ps.pushPose();
        blit(ps,
            0, 0, BOOK_WIDTH, BOOK_HEIGHT, // x, y, w, h
            0, 0, BOOK_TEX_WIDTH, BOOK_TEX_HEIGHT, // u, v, uw, vh
            256, 256); // texture size
        ps.popPose();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.previous);
    }

    private abstract class PCBInternal<T> extends PictureCycleButton<T> {
        public PCBInternal(int x, int y, List<T> cycle, int ux, int vy) {
            super(x, y, cycle, TEXTURE_LOC, ux, vy, 16);
        }

        @Override
        public void onChange() {
            GuiCollectorsLog.this.sortItems();
        }
    }
}
