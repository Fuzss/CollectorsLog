package at.petrak.collectorslog.client.gui.screens;

import at.petrak.collectorslog.CollectorsLog;
import at.petrak.collectorslog.config.CollectorsLogConfig;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class CollectorsLogScreen extends Screen implements StatsUpdateListener {
    public static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
    public static final ResourceLocation TEXTURE_LOC = CollectorsLog.id("textures/gui/collectorslog.png");
    public static final ResourceLocation NEW_INDEX_LOCATION = CollectorsLog.id("textures/gui/index.png");
    private static final int BOOK_TEX_WIDTH = 192;
    private static final int BOOK_TEX_HEIGHT = 128;

    public static final int BOOK_WIDTH = 336;
    public static final int BOOK_HEIGHT = 224;

    @Nullable
    private final Screen previous;
    private boolean isLoading = true;

    private final Set<Item> collectedItems = new HashSet<>();
    private final Set<Item> allAllowedItems = new HashSet<>();
    private List<ItemStack> displayedItems = new ArrayList<>();

    private EditBox searchBox = null;
    private PictureCycleButton<SortMode> sortModeButton = null;
    private PictureCycleButton<Boolean> reverseSortButton = null;
    private PictureCycleButton<FilterMode> filterModeButton = null;
    private ImageButton turnPageBackButton = null;
    private ImageButton turnPageForwardButton = null;
    private ImageButton turnPageLandingButton = null;

    // This increments by 1 for every *two* pages spread across.
    private int pageSpread = 0;

    public CollectorsLogScreen(@Nullable Screen previous, Player player, FeatureFlagSet featureFlagSet, boolean displayOperatorCreativeTab) {
        super(Component.translatable("gui.collectorslog"));
        this.previous = previous;
        CreativeModeTabs.tryRebuildTabContents(featureFlagSet, player.canUseGameMasterBlocks() && displayOperatorCreativeTab);
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

            this.collectedItems.clear();
            this.allAllowedItems.clear();
            NonNullList<ItemStack> fillee = NonNullList.create();
            CreativeModeTab creativeModeTab = CreativeModeTabs.searchTab();
            fillee.addAll(creativeModeTab.getSearchTabDisplayItems());
            fillee.iterator().forEachRemaining(item -> {
                if (CollectorsLogConfig.INSTANCE.isItemAllowed(item.getItem())) {
                    this.allAllowedItems.add(item.getItem());
                    int foundCount = this.minecraft.player.getStats().getValue(Stats.ITEM_PICKED_UP, item.getItem());
                    foundCount += this.minecraft.player.getStats().getValue(Stats.ITEM_CRAFTED, item.getItem());
                    if (foundCount > 0) {
                        this.collectedItems.add(item.getItem());
                    }
                }
            });

            this.initWidgets();
        }
    }

    private void initWidgets() {
        int bookCornerX = (this.width - BOOK_WIDTH) / 2;
        int bookCornerY = (this.height - BOOK_HEIGHT) / 2;


        int rightMargin = bookCornerX + BOOK_WIDTH / 2 - 28;
        int buttonHeight = bookCornerY + 4 + 20;

        this.searchBox = new EditBox(this.font, bookCornerX + 8, buttonHeight + 1,
            rightMargin - 44 - bookCornerX - 11, 18, Component.translatable("gui.collectorslog.search"));
        this.searchBox.setSuggestion(Component.translatable("gui.collectorslog.search").getString());
        this.searchBox.setResponder(s -> {
            this.searchBox.setSuggestion(s.isEmpty()
                ? Component.translatable("gui.collectorslog.search").getString()
                : "");
            this.sortItems();
        });

        this.sortModeButton = new PCBInternal<SortMode>(rightMargin - 44, buttonHeight, List.of(SortMode.values()), 0,
            160) {
            @Override
            public Component getTooltipFromValue() {
                return Component.translatable("gui.collectorslog.sortmode." + this.getValue().key);
            }
        };
        this.reverseSortButton = new PCBInternal<Boolean>(rightMargin - 22, buttonHeight, List.of(false, true), 16,
            160) {
            @Override
            public Component getTooltipFromValue() {
                return Component.translatable("gui.collectorslog.sortreverse." + this.getValue());
            }
        };
        this.filterModeButton = new PCBInternal<FilterMode>(rightMargin, buttonHeight,
            List.of(FilterMode.values()), 32,
            160) {
            @Override
            public Component getTooltipFromValue() {
                return Component.translatable("gui.collectorslog.filtermode." + this.getValue().key);
            }
        };

        int pageButtonY = bookCornerY + BOOK_HEIGHT - 14;
        this.turnPageBackButton = this.makeTurnPageButton(bookCornerX + 8, pageButtonY, 48, 160, 0);
        this.turnPageLandingButton = this.makeTurnPageButton(bookCornerX + 8 + 2 + 18, pageButtonY, 84, 160, 1);
        this.turnPageForwardButton = this.makeTurnPageButton(this.width / 2 + BOOK_WIDTH / 2 - 8 - 18,
            pageButtonY, 66, 160, 2);

        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(this.sortModeButton);
        this.addRenderableWidget(this.reverseSortButton);
        this.addRenderableWidget(this.filterModeButton);
        this.addRenderableWidget(this.turnPageBackButton);
        this.addRenderableWidget(this.turnPageLandingButton);
        this.addRenderableWidget(this.turnPageForwardButton);

        this.sortItems();
    }

    private void sortItems() {
        this.displayedItems.clear();

        SortMode sortMode = this.sortModeButton.getValue();
        boolean reverseSort = this.reverseSortButton.getValue();
        FilterMode filterMode = this.filterModeButton.getValue();

        Stream<Item> searchSpace;
        String search = this.searchBox.getValue();
        if (search.isEmpty()) {
            searchSpace = BuiltInRegistries.ITEM.stream();
        } else {
            SearchTree<ItemStack> st = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
            searchSpace = st.search(search.toLowerCase(Locale.ROOT)).stream().map(ItemStack::getItem);
        }

        searchSpace.forEachOrdered(item -> {
            if (this.allAllowedItems.contains(item) && filterMode.allow(item, this.collectedItems)) {
                this.displayedItems.add(new ItemStack(item));
            }
        });
        sortMode.sort(this.displayedItems);
        if (reverseSort) {
            this.displayedItems = Lists.reverse(this.displayedItems);
        }

        this.changePageSpread(0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0.0) {
            this.changePageSpread(-1);
            return true;
        } else if (delta < 0.0) {
            this.changePageSpread(1);
            return true;
        } else {
            return false;
        }
    }

    private void changePageSpread(int delta) {
        this.pageSpread += delta;
        this.pageSpread = Mth.clamp(this.pageSpread, 0, maxUsefulPage(this.displayedItems.size()));

        for (AbstractWidget widget : List.of(this.searchBox, this.sortModeButton, this.reverseSortButton, this.filterModeButton)) {
            widget.visible = (this.pageSpread == 0);
        }
        for (ImageButton widget : List.of(this.turnPageBackButton, this.turnPageLandingButton)) {
            widget.visible = (this.pageSpread != 0);
        }
        this.turnPageForwardButton.visible = this.pageSpread < maxUsefulPage(this.displayedItems.size());
    }

    @Override
    public void render(PoseStack ps, int mx, int my, float partialTicks) {
        this.renderBackground(ps);
        ps.pushPose();
        ps.translate((this.width - BOOK_WIDTH) / 2.0, (this.height - BOOK_HEIGHT) / 2.0, 1);
        this.renderBg(ps, mx, my, partialTicks);

        int titleLocX = BOOK_WIDTH / 4; // the *center* of the *left half* of the book
        int titleLocY = 8;
        if (this.isLoading) {
            drawCenteredString(ps, this.font, PENDING_TEXT, titleLocX, titleLocY, -1);
            String loadingSymbol = LOADING_SYMBOLS[(int) (Util.getMillis() / 150L % (long) LOADING_SYMBOLS.length)];
            drawCenteredString(ps, this.font, loadingSymbol, titleLocX, titleLocY + 9 * 2, 0xff_dddddd);
        } else if (this.pageSpread == 0) {
            drawCenteredString(ps, this.font, Component.translatable(
                "gui.collectorslog.progress",
                this.collectedItems.size(), this.allAllowedItems.size(),
                String.format("%.0f", 100.0 * this.collectedItems.size() / this.allAllowedItems.size())
            ), titleLocX, titleLocY, -1);
        }

        this.renderItemList(ps, false);
        this.renderItemList(ps, true);

        ps.pushPose();
        ps.translate(BOOK_WIDTH / 2.0, BOOK_HEIGHT - 12, 0);
        MutableComponent pageNumber = Component.literal(String.valueOf(this.pageSpread * 2 + 1));
        int width = this.font.width(pageNumber);
        drawString(ps, this.font, pageNumber, -width - 10, 0, 0xffffffff);
        pageNumber = Component.literal(
            String.format("%d/%d", this.pageSpread * 2 + 2, maxUsefulPage(this.displayedItems.size()) * 2 + 2));
        drawString(ps, this.font, pageNumber, 10, 0, 0xffffffff);
        ps.popPose();

        ps.popPose();
        ps.translate(0, 0, 1);
        super.render(ps, mx, my, partialTicks);

    }

    private void renderBg(PoseStack ps, int mx, int my, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOC);
        ps.pushPose();
        blit(ps,
            -16, -16, BOOK_WIDTH + 32, BOOK_HEIGHT + 32, // x, y, w, h
            0, 0, BOOK_TEX_WIDTH + 16, BOOK_TEX_HEIGHT + 17, // u, v, uw, vh
            256, 256); // texture size
        ps.popPose();
    }

    private void renderItemList(PoseStack ps, boolean rhs) {
        boolean veryFirstPage = !rhs && this.pageSpread == 0;

        int page = this.pageSpread * 2 + (rhs ? 1 : 0);
        int startIdx = getItemStartIdxOnPage(page);
        int endIdx = startIdx + (veryFirstPage ? ITEM_COUNT_FIRST_PAGE : ITEM_COUNT_OTHER_PAGE);

        if (startIdx >= this.displayedItems.size()) {
            return;
        }
        List<ItemStack> slice = this.displayedItems.subList(startIdx, Math.min(endIdx, this.displayedItems.size()));

        int dy = 17;

        ps.pushPose();
        ps.translate(10, 4, 0);
        if (rhs) {
            ps.translate(BOOK_WIDTH / 2.0, 0, 0);
        }
        if (!rhs && this.pageSpread == 0) {
            ps.translate(0, dy * 3, 0);
        }

        for (ItemStack stack : slice) {
            boolean hasIt = this.collectedItems.contains(stack.getItem());
            RenderHelper.renderItemStackInGui(ps, stack, 0, 0);

            ps.pushPose();
            ps.translate(20, 12, 0);
            Component toWrite = stack.getHoverName();
            int width = this.minecraft.font.width(toWrite);
            int okWidth = BOOK_WIDTH / 2 - 24 - 12;
            if (width > okWidth) {
                ps.scale((float) okWidth / width, (float) okWidth / width, 1);
            }
            ps.translate(0, -8, 0);
            drawString(ps, this.minecraft.font, toWrite, 0, 0, hasIt ? 0xff_88ff88 : 0xff_ff6666);
            ps.popPose();

            ps.translate(0, dy, 0);
        }

        ps.popPose();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.previous);
    }

    private static final int ITEM_COUNT_FIRST_PAGE = 9;
    private static final int ITEM_COUNT_OTHER_PAGE = 12;

    // 0 = first page, 1 = 2nd page, 2 = third page (on the second spread) ...
    private static int getItemStartIdxOnPage(int page) {
        if (page == 0) {
            return 0;
        } else {
            return ITEM_COUNT_FIRST_PAGE + (page - 1) * ITEM_COUNT_OTHER_PAGE;
        }
    }

    private static int maxUsefulPage(int itemCount) {
        if (itemCount < ITEM_COUNT_FIRST_PAGE) {
            return 0;
        } else {
            return ((itemCount - ITEM_COUNT_FIRST_PAGE - 1) / ITEM_COUNT_OTHER_PAGE + 1) / 2;
        }
    }

    private abstract class PCBInternal<T> extends PictureCycleButton<T> {
        public PCBInternal(int x, int y, List<T> cycle, int ux, int vy) {
            super(x, y, cycle, TEXTURE_LOC, ux, vy, 16);
        }

        @Override
        public void onChange() {
            CollectorsLogScreen.this.sortItems();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
    }

    private ImageButton makeTurnPageButton(int x, int y, int vx, int uy, int which) {
        Button.OnPress onpress = (self) -> {
            if (which == 0) {
                this.changePageSpread(-1);
            } else if (which == 1) {
                this.changePageSpread(-this.pageSpread);
            } else {
                this.changePageSpread(1);
            }
        };
        return new ImageButton(x, y, 18, 10, vx, uy, 10, TEXTURE_LOC, onpress);
    }
}
