package at.petrak.collectorslog.gui;

import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;

public enum SortMode {
    ALPHABETICAL("alphabetical"),
    CREATIVE_MODE("creative_mode");

    public final String key;

    SortMode(String key) {
        this.key = key;
    }

    /**
     * @param items Items in registry order.
     */
    public void sort(List<ItemStack> items) {
        switch (this) {
            case ALPHABETICAL -> {
                items.sort(Comparator.comparing(a -> a.getDisplayName().getString()));
            }
            case CREATIVE_MODE -> {
                // leave 'em as is
            }
        }
    }
}
