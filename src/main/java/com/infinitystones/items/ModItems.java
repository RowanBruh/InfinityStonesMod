package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.blocks.traps.TrapBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);

    // Creative tabs
    public static final ItemGroup INFINITY_STONES_TAB = InfinityStonesMod.INFINITY_STONES_TAB;
    public static final ItemGroup ROWAN_INDUSTRIES_TAB = InfinityStonesMod.ROWAN_INDUSTRIES_TAB;
    public static final ItemGroup BONCS_ITEMS_TAB = InfinityStonesMod.BONCS_ITEMS_TAB;

    // Infinity Stones
    public static final RegistryObject<Item> SPACE_STONE = ITEMS.register("space_stone",
            () -> new InfinityStone(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> MIND_STONE = ITEMS.register("mind_stone",
            () -> new InfinityStone(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> REALITY_STONE = ITEMS.register("reality_stone",
            () -> new InfinityStone(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> POWER_STONE = ITEMS.register("power_stone",
            () -> new InfinityStone(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> TIME_STONE = ITEMS.register("time_stone",
            () -> new InfinityStone(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> SOUL_STONE = ITEMS.register("soul_stone",
            () -> new InfinityStone(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));

    // Gauntlets
    public static final RegistryObject<Item> INFINITY_GAUNTLET = ITEMS.register("infinity_gauntlet",
            () -> new InfinityGauntlet(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> ADVANCED_INFINITY_GAUNTLET = ITEMS.register("advanced_infinity_gauntlet",
            () -> new AdvancedInfinityGauntlet(new Item.Properties().group(INFINITY_STONES_TAB).maxStackSize(1)));

    // Nano Tech
    public static final RegistryObject<Item> NANO_TECH_CORE = ITEMS.register("nano_tech_core",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> NANO_TECH_HELMET = ITEMS.register("nano_tech_helmet",
            () -> new NanoTechArmorItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> NANO_TECH_CHESTPLATE = ITEMS.register("nano_tech_chestplate",
            () -> new NanoTechArmorItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> NANO_TECH_LEGGINGS = ITEMS.register("nano_tech_leggings",
            () -> new NanoTechArmorItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> NANO_TECH_BOOTS = ITEMS.register("nano_tech_boots",
            () -> new NanoTechArmorItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> NANO_TECH_SWORD = ITEMS.register("nano_tech_sword",
            () -> new NanoTechSword(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));

    // Infected Infinity Stones
    public static final RegistryObject<Item> INFECTED_SPACE_STONE = ITEMS.register("infected_space_stone",
            () -> new InfectedInfinityStone(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> INFECTED_MIND_STONE = ITEMS.register("infected_mind_stone",
            () -> new InfectedInfinityStone(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> INFECTED_REALITY_STONE = ITEMS.register("infected_reality_stone",
            () -> new InfectedInfinityStone(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> INFECTED_POWER_STONE = ITEMS.register("infected_power_stone",
            () -> new InfectedInfinityStone(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> INFECTED_TIME_STONE = ITEMS.register("infected_time_stone",
            () -> new InfectedInfinityStone(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> INFECTED_SOUL_STONE = ITEMS.register("infected_soul_stone",
            () -> new InfectedInfinityStone(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> INFECTED_INFINITY_GAUNTLET = ITEMS.register("infected_infinity_gauntlet",
            () -> new InfectedInfinityGauntlet(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));

    // Box Items
    public static final RegistryObject<Item> COMMON_BOX = ITEMS.register("common_box",
            () -> new BoxItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> RARE_BOX = ITEMS.register("rare_box",
            () -> new BoxItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> EPIC_BOX = ITEMS.register("epic_box",
            () -> new BoxItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> LEGENDARY_BOX = ITEMS.register("legendary_box",
            () -> new BoxItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> COSMIC_BOX = ITEMS.register("cosmic_box",
            () -> new BoxItem(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));

    // Bonc's Items
    public static final RegistryObject<Item> TELEPORT_PEARL = ITEMS.register("teleport_pearl",
            () -> new TeleportPearl(new Item.Properties().group(BONCS_ITEMS_TAB).maxStackSize(16)));
    
    public static final RegistryObject<Item> ULTRA_GRAPPLING_HOOK = ITEMS.register("ultra_grappling_hook",
            () -> new UltraGrapplingHookItem(new Item.Properties().group(BONCS_ITEMS_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> LIGHTNING_ROD = ITEMS.register("lightning_rod",
            () -> new LightningRodItem(new Item.Properties().group(BONCS_ITEMS_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> GRAVITY_HAMMER = ITEMS.register("gravity_hammer",
            () -> new GravityHammerItem(new Item.Properties().group(BONCS_ITEMS_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> TNT_STICK = ITEMS.register("tnt_stick",
            () -> new TNTStickItem(new Item.Properties().group(BONCS_ITEMS_TAB).maxStackSize(16)));

    // Items from SkiddziePlays "Insane Ideas Mojang Rejected!" video
    public static final RegistryObject<Item> COPPER_GOLEM = ITEMS.register("copper_golem",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> TUFF_GOLEM = ITEMS.register("tuff_golem",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> MOOBLOOM_SPAWN_EGG = ITEMS.register("moobloom_spawn_egg",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> ICEOLOGER_STAFF = ITEMS.register("iceologer_staff",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> WILDFIRE_STAFF = ITEMS.register("wildfire_staff",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));
    
    public static final RegistryObject<Item> OSTRICH_EGG = ITEMS.register("ostrich_egg",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> RASCAL_BAG = ITEMS.register("rascal_bag",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> FLETCHING_TABLE_TOOL = ITEMS.register("fletching_table_tool",
            () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB).maxStackSize(1)));

    // Trap Blocks as Items
    // Register trap blocks as items in the Rowan Industries tab
    public static final RegistryObject<Item> FAKE_DIAMOND_ORE_ITEM = ITEMS.register("fake_diamond_ore",
            () -> new BlockItem(TrapBlocks.FAKE_DIAMOND_ORE.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> TRAP_CHEST_ITEM = ITEMS.register("trap_chest",
            () -> new BlockItem(TrapBlocks.TRAP_CHEST.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> LANDMINE_ITEM = ITEMS.register("landmine",
            () -> new BlockItem(TrapBlocks.LANDMINE.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> SPIKE_TRAP_ITEM = ITEMS.register("spike_trap",
            () -> new BlockItem(TrapBlocks.SPIKE_TRAP.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> FALLING_ANVIL_TRAP_ITEM = ITEMS.register("falling_anvil_trap",
            () -> new BlockItem(TrapBlocks.FALLING_ANVIL_TRAP.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> PITFALL_TRAP_ITEM = ITEMS.register("pitfall_trap",
            () -> new BlockItem(TrapBlocks.PITFALL_TRAP.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> ARROW_TRAP_ITEM = ITEMS.register("arrow_trap",
            () -> new BlockItem(TrapBlocks.ARROW_TRAP.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> LAVA_TRAP_ITEM = ITEMS.register("lava_trap",
            () -> new BlockItem(TrapBlocks.LAVA_TRAP.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
    
    public static final RegistryObject<Item> TNT_TRAP_ITEM = ITEMS.register("tnt_trap",
            () -> new BlockItem(TrapBlocks.TNT_TRAP.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
            
    // One Way Block from previous implementation
    public static final RegistryObject<Item> ONE_WAY_BLOCK_ITEM = ITEMS.register("one_way_block",
            () -> new BlockItem(ModBlocks.ONE_WAY_BLOCK.get(), 
                    new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
}