package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for all mod items
 */
public class ModItems {
    // Item registry
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Infinity Stones
    public static final RegistryObject<Item> SPACE_STONE = ITEMS.register("space_stone",
            () -> new InfinityStone(StoneType.SPACE));
    
    public static final RegistryObject<Item> MIND_STONE = ITEMS.register("mind_stone",
            () -> new InfinityStone(StoneType.MIND));
    
    public static final RegistryObject<Item> REALITY_STONE = ITEMS.register("reality_stone",
            () -> new InfinityStone(StoneType.REALITY));
    
    public static final RegistryObject<Item> POWER_STONE = ITEMS.register("power_stone",
            () -> new InfinityStone(StoneType.POWER));
    
    public static final RegistryObject<Item> TIME_STONE = ITEMS.register("time_stone",
            () -> new InfinityStone(StoneType.TIME));
    
    public static final RegistryObject<Item> SOUL_STONE = ITEMS.register("soul_stone",
            () -> new InfinityStone(StoneType.SOUL));
    
    // Gauntlets
    public static final RegistryObject<Item> INFINITY_GAUNTLET = ITEMS.register("infinity_gauntlet",
            InfinityGauntlet::new);
    
    public static final RegistryObject<Item> ADVANCED_INFINITY_GAUNTLET = ITEMS.register("advanced_infinity_gauntlet",
            AdvancedInfinityGauntlet::new);
    
    // Nano Tech Items
    public static final RegistryObject<Item> NANO_TECH_CORE = ITEMS.register("nano_tech_core",
            () -> new Item(new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    
    public static final RegistryObject<Item> NANO_TECH_HELMET = ITEMS.register("nano_tech_helmet",
            () -> new NanoTechArmor(NanoTechArmor.ArmorType.HELMET));
    
    public static final RegistryObject<Item> NANO_TECH_CHESTPLATE = ITEMS.register("nano_tech_chestplate",
            () -> new NanoTechArmor(NanoTechArmor.ArmorType.CHESTPLATE));
    
    public static final RegistryObject<Item> NANO_TECH_LEGGINGS = ITEMS.register("nano_tech_leggings",
            () -> new NanoTechArmor(NanoTechArmor.ArmorType.LEGGINGS));
    
    public static final RegistryObject<Item> NANO_TECH_BOOTS = ITEMS.register("nano_tech_boots",
            () -> new NanoTechArmor(NanoTechArmor.ArmorType.BOOTS));
    
    public static final RegistryObject<Item> NANO_TECH_SWORD = ITEMS.register("nano_tech_sword",
            NanoTechSword::new);
    
    // Infected Infinity Stones
    public static final RegistryObject<Item> INFECTED_SPACE_STONE = ITEMS.register("infected_space_stone",
            () -> new InfectedInfinityStone(StoneType.SPACE));
    
    public static final RegistryObject<Item> INFECTED_MIND_STONE = ITEMS.register("infected_mind_stone",
            () -> new InfectedInfinityStone(StoneType.MIND));
    
    public static final RegistryObject<Item> INFECTED_REALITY_STONE = ITEMS.register("infected_reality_stone",
            () -> new InfectedInfinityStone(StoneType.REALITY));
    
    public static final RegistryObject<Item> INFECTED_POWER_STONE = ITEMS.register("infected_power_stone",
            () -> new InfectedInfinityStone(StoneType.POWER));
    
    public static final RegistryObject<Item> INFECTED_TIME_STONE = ITEMS.register("infected_time_stone",
            () -> new InfectedInfinityStone(StoneType.TIME));
    
    public static final RegistryObject<Item> INFECTED_SOUL_STONE = ITEMS.register("infected_soul_stone",
            () -> new InfectedInfinityStone(StoneType.SOUL));
    
    // Infected Infinity Gauntlet
    public static final RegistryObject<Item> INFECTED_INFINITY_GAUNTLET = ITEMS.register("infected_infinity_gauntlet",
            InfectedInfinityGauntlet::new);
    
    // Boxes for Rowan Industries tab
    public static final RegistryObject<Item> COMMON_BOX = ITEMS.register("common_box",
            () -> new Item(new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    
    public static final RegistryObject<Item> RARE_BOX = ITEMS.register("rare_box",
            () -> new Item(new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    
    public static final RegistryObject<Item> EPIC_BOX = ITEMS.register("epic_box",
            () -> new Item(new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    
    public static final RegistryObject<Item> LEGENDARY_BOX = ITEMS.register("legendary_box",
            () -> new Item(new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    
    public static final RegistryObject<Item> COSMIC_BOX = ITEMS.register("cosmic_box",
            () -> new Item(new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    
    // Bionic's items tab (officially named "Bonc's Items" in game)
    public static final RegistryObject<Item> TELEPORT_PEARL = ITEMS.register("teleport_pearl",
            () -> new Item(new Item.Properties().group(ModItemGroups.BONCS_ITEMS)));
    
    public static final RegistryObject<Item> ULTRA_GRAPPLING_HOOK = ITEMS.register("ultra_grappling_hook",
            () -> new Item(new Item.Properties().group(ModItemGroups.BONCS_ITEMS)));
    
    public static final RegistryObject<Item> LIGHTNING_ROD = ITEMS.register("lightning_rod",
            () -> new Item(new Item.Properties().group(ModItemGroups.BONCS_ITEMS)));
    
    public static final RegistryObject<Item> GRAVITY_HAMMER = ITEMS.register("gravity_hammer",
            () -> new Item(new Item.Properties().group(ModItemGroups.BONCS_ITEMS)));
    
    public static final RegistryObject<Item> TNT_STICK = ITEMS.register("tnt_stick",
            () -> new Item(new Item.Properties().group(ModItemGroups.BONCS_ITEMS)));
}