package at.petrak.collectorslog.forge;

import at.petrak.collectorslog.CollectorsLogConfig;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ForgeConfig implements CollectorsLogConfig {
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> denylist;

    @Override
    public boolean isItemAllowed(Item item) {
        return !denylist.get().contains(Registry.ITEM.getKey(item).toString());
    }

    public ForgeConfig(ForgeConfigSpec.Builder builder) {
        denylist = builder.comment("Items that won't show up in the collector's log")
            .defineList("denylist", CollectorsLogConfig.DEFAULT_DENYLIST, t -> true);
    }
}
