package at.petrak.collectorslog.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public enum FilterMode {
    COLLECTED("collected"),
    UNCOLLECTED("uncollected"),
    ALL("all");

    public final String key;

    FilterMode(String key) {
        this.key = key;
    }

    public boolean allow(ItemStack item, Set<Item> collected) {
        return switch (this) {
            case COLLECTED -> collected.contains(item.getItem());
            case UNCOLLECTED -> !collected.contains(item.getItem());
            case ALL -> true;
        };
    }
}
