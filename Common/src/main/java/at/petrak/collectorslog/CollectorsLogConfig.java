package at.petrak.collectorslog;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class CollectorsLogConfig {
    private static final List<String> DEFAULT_DENYLIST = List.of(
            "minecraft:bedrock",
            "minecraft:budding_amethyst",
            "minecraft:chorus_plant",
            "minecraft:end_portal_frame",
            "minecraft:farmland",
            "minecraft:frogspawn",
            "minecraft:infested_stone",
            "minecraft:infested_cobblestone",
            "minecraft:infested_stone_bricks",
            "minecraft:infested_cracked_stone_bricks",
            "minecraft:infested_mossy_stone_bricks",
            "minecraft:infested_chiseled_stone_bricks",
            "minecraft:infested_deepslate",
            "minecraft:reinforced_deepslate",
            "minecraft:spawner",
            "minecraft:barrier",
            "minecraft:bundle",
            "minecraft:command_block",
            "minecraft:chain_command_block",
            "minecraft:repeating_command_block",
            "minecraft:jigsaw",
            "minecraft:light",
            "minecraft:command_block_minecart",
            "minecraft:petrified_oak_slab",
            "minecraft:player_head",
            "minecraft:structure_block",
            "minecraft:structure_void",

            "minecraft:allay_spawn_egg",
            "minecraft:axolotl_spawn_egg",
            "minecraft:bat_spawn_egg",
            "minecraft:bee_spawn_egg",
            "minecraft:blaze_spawn_egg",
            "minecraft:cave_spider_spawn_egg",
            "minecraft:cat_spawn_egg",
            "minecraft:chicken_spawn_egg",
            "minecraft:cod_spawn_egg",
            "minecraft:cow_spawn_egg",
            "minecraft:creeper_spawn_egg",
            "minecraft:dolphin_spawn_egg",
            "minecraft:donkey_spawn_egg",
            "minecraft:drowned_spawn_egg",
            "minecraft:elder_guardian_spawn_egg",
            "minecraft:enderman_spawn_egg",
            "minecraft:endermite_spawn_egg",
            "minecraft:evoker_spawn_egg",
            "minecraft:fox_spawn_egg",
            "minecraft:frog_spawn_egg",
            "minecraft:ghast_spawn_egg",
            "minecraft:glow_squid_spawn_egg",
            "minecraft:guardian_spawn_egg",
            "minecraft:hoglin_spawn_egg",
            "minecraft:horse_spawn_egg",
            "minecraft:husk_spawn_egg",
            "minecraft:llama_spawn_egg",
            "minecraft:magma_cube_spawn_egg",
            "minecraft:mule_spawn_egg",
            "minecraft:ocelot_spawn_egg",
            "minecraft:panda_spawn_egg",
            "minecraft:parrot_spawn_egg",
            "minecraft:phantom_spawn_egg",
            "minecraft:pig_spawn_egg",
            "minecraft:piglin_spawn_egg",
            "minecraft:piglin_brute_spawn_egg",
            "minecraft:pillager_spawn_egg",
            "minecraft:polar_bear_spawn_egg",
            "minecraft:pufferfish_spawn_egg",
            "minecraft:rabbit_spawn_egg",
            "minecraft:ravager_spawn_egg",
            "minecraft:salmon_spawn_egg",
            "minecraft:sheep_spawn_egg",
            "minecraft:shulker_spawn_egg",
            "minecraft:silverfish_spawn_egg",
            "minecraft:skeleton_spawn_egg",
            "minecraft:skeleton_horse_spawn_egg",
            "minecraft:slime_spawn_egg",
            "minecraft:spider_spawn_egg",
            "minecraft:squid_spawn_egg",
            "minecraft:stray_spawn_egg",
            "minecraft:strider_spawn_egg",
            "minecraft:tadpole_spawn_egg",
            "minecraft:trader_llama_spawn_egg",
            "minecraft:tropical_fish_spawn_egg",
            "minecraft:turtle_spawn_egg",
            "minecraft:vex_spawn_egg",
            "minecraft:vindicator_spawn_egg",
            "minecraft:wandering_trader_spawn_egg",
            "minecraft:warden_spawn_egg",
            "minecraft:witch_spawn_egg",
            "minecraft:wither_skeleton_spawn_egg",
            "minecraft:wolf_spawn_egg",
            "minecraft:zombie_spawn_egg",
            "minecraft:zombie_horse_spawn_egg",
            "minecraft:zombie_villager_spawn_egg",
            "minecraft:zombified_piglin_spawn_egg"
    );
    public static final CollectorsLogConfig INSTANCE = new CollectorsLogConfig(new ForgeConfigSpec.Builder());

    public final ForgeConfigSpec.ConfigValue<List<? extends String>> denylist;
    public final ForgeConfigSpec spec;

    private CollectorsLogConfig(ForgeConfigSpec.Builder builder) {
        this.denylist = builder.comment("Items that won't show up in the collector's log")
                .defineList("denylist", CollectorsLogConfig.DEFAULT_DENYLIST, t -> true);
        this.spec = builder.build();
    }

    public boolean isItemAllowed(Item item) {
        return !this.denylist.get().contains(BuiltInRegistries.ITEM.getKey(item).toString());
    }
}
