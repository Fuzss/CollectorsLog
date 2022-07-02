package at.petrak.collectorslog.gui;

import net.minecraft.world.item.Item;

import java.util.Set;

public enum FilterMode {
    COLLECTED("collected"),
    UNCOLLECTED("uncollected"),
    ALL("all");

    public final String key;

    FilterMode(String key) {
        this.key = key;
    }

    public boolean allow(Item item, Set<Item> collected) {
        return switch (this) {
            case COLLECTED -> collected.contains(item);
            case UNCOLLECTED -> !collected.contains(item);
            case ALL -> true;
        };
    }
}
