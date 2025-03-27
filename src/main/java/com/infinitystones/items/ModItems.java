package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfectedInfinityStones.InfectedStoneItem;
import com.infinitystones.items.InfinityStones.InfinityStoneItem;
import com.infinitystones.items.InfinityStones.StoneType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for all mod items
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);

    // Basic utility items
    public static final RegistryObject<Item> TELEPORT_PEARL = ITEMS.register("teleport_pearl", 
            () -> new TeleportPearl(new Item.Properties().group(InfinityStonesMod.INFINITY_STONES_GROUP)));
            
    public static final RegistryObject<Item> TNT_STICK = ITEMS.register("tnt_stick", 
            () -> new TntStick(new Item.Properties().group(InfinityStonesMod.INFINITY_STONES_GROUP)));

    // Infinity Stones
    public static final RegistryObject<Item> SPACE_STONE = ITEMS.register("space_stone", 
            () -> new InfinityStoneItem(StoneType.SPACE));
            
    public static final RegistryObject<Item> MIND_STONE = ITEMS.register("mind_stone", 
            () -> new InfinityStoneItem(StoneType.MIND));
            
    public static final RegistryObject<Item> REALITY_STONE = ITEMS.register("reality_stone", 
            () -> new InfinityStoneItem(StoneType.REALITY));
            
    public static final RegistryObject<Item> POWER_STONE = ITEMS.register("power_stone", 
            () -> new InfinityStoneItem(StoneType.POWER));
            
    public static final RegistryObject<Item> TIME_STONE = ITEMS.register("time_stone", 
            () -> new InfinityStoneItem(StoneType.TIME));
            
    public static final RegistryObject<Item> SOUL_STONE = ITEMS.register("soul_stone", 
            () -> new InfinityStoneItem(StoneType.SOUL));

    // Gauntlets
    public static final RegistryObject<Item> INFINITY_GAUNTLET = ITEMS.register("infinity_gauntlet", 
            InfinityGauntlet::new);
            
    public static final RegistryObject<Item> ADVANCED_INFINITY_GAUNTLET = ITEMS.register("advanced_infinity_gauntlet", 
            AdvancedInfinityGauntlet::new);

    // Rowan Industries items
    public static final RegistryObject<Item> COMMON_BOX = ITEMS.register("common_box", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES)));
            
    public static final RegistryObject<Item> RARE_BOX = ITEMS.register("rare_box", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES)));
            
    public static final RegistryObject<Item> EPIC_BOX = ITEMS.register("epic_box", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES)));
            
    public static final RegistryObject<Item> LEGENDARY_BOX = ITEMS.register("legendary_box", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES)));
            
    public static final RegistryObject<Item> COSMIC_BOX = ITEMS.register("cosmic_box", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES)));

    // Rowan Lucky Blocks
    public static final RegistryObject<Item> ROWAN_LUCKY_BLOCK_ITEM = ITEMS.register("rowan_lucky_block",
            () -> new BlockItem(com.infinitystones.blocks.ModBlocks.ROWAN_LUCKY_BLOCK.get(), 
                    new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES)));

    // Bionic Items
    public static final RegistryObject<Item> LIGHTNING_ROD = ITEMS.register("lightning_rod", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.BONC_ITEMS)));
            
    public static final RegistryObject<Item> GRAVITY_HAMMER = ITEMS.register("gravity_hammer", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.BONC_ITEMS)));
            
    public static final RegistryObject<Item> ULTRA_GRAPPLING_HOOK = ITEMS.register("ultra_grappling_hook", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.BONC_ITEMS)));
            
    public static final RegistryObject<Item> BIONIC_LUCKY_BLOCK_ITEM = ITEMS.register("bionic_lucky_block",
            () -> new BlockItem(com.infinitystones.blocks.ModBlocks.BIONIC_LUCKY_BLOCK.get(), 
                    new Item.Properties().group(InfinityStonesMod.BONC_ITEMS)));
                    
    // Crafting materials/components
    public static final RegistryObject<Item> ENCHANTED_METAL = ITEMS.register("enchanted_metal", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_STONES_GROUP)));
            
    public static final RegistryObject<Item> DRAGON_SCALE = ITEMS.register("dragon_scale", 
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_STONES_GROUP)));
            
    // Nano Tech Items
    public static final RegistryObject<Item> NANO_TECH_CORE = ITEMS.register("nano_tech_core", 
            () -> new NanoTechItems.NanoTechCore());
            
    public static final RegistryObject<Item> NANO_TECH_HELMET = ITEMS.register("nano_tech_helmet", 
            () -> new NanoTechItems.NanoTechArmor(EquipmentSlotWrapper.HEAD));
            
    public static final RegistryObject<Item> NANO_TECH_ARMOR = ITEMS.register("nano_tech_armor", 
            () -> new NanoTechItems.NanoTechArmor(EquipmentSlotWrapper.CHEST));
            
    public static final RegistryObject<Item> NANO_TECH_LEGGINGS = ITEMS.register("nano_tech_leggings", 
            () -> new NanoTechItems.NanoTechArmor(EquipmentSlotWrapper.LEGS));
            
    public static final RegistryObject<Item> NANO_TECH_BOOTS = ITEMS.register("nano_tech_boots", 
            () -> new NanoTechItems.NanoTechArmor(EquipmentSlotWrapper.FEET));
            
    public static final RegistryObject<Item> NANO_TECH_SWORD = ITEMS.register("nano_tech_sword", 
            () -> new NanoTechItems.NanoTechSword());
            
    // Infected Infinity Stones
    public static final RegistryObject<Item> INFECTED_SPACE_STONE = ITEMS.register("infected_space_stone", 
            () -> new InfectedStoneItem(StoneType.SPACE));
            
    public static final RegistryObject<Item> INFECTED_MIND_STONE = ITEMS.register("infected_mind_stone", 
            () -> new InfectedStoneItem(StoneType.MIND));
            
    public static final RegistryObject<Item> INFECTED_REALITY_STONE = ITEMS.register("infected_reality_stone", 
            () -> new InfectedStoneItem(StoneType.REALITY));
            
    public static final RegistryObject<Item> INFECTED_POWER_STONE = ITEMS.register("infected_power_stone", 
            () -> new InfectedStoneItem(StoneType.POWER));
            
    public static final RegistryObject<Item> INFECTED_TIME_STONE = ITEMS.register("infected_time_stone", 
            () -> new InfectedStoneItem(StoneType.TIME));
            
    public static final RegistryObject<Item> INFECTED_SOUL_STONE = ITEMS.register("infected_soul_stone", 
            () -> new InfectedStoneItem(StoneType.SOUL));
            
    // Infected Infinity Gauntlet
    public static final RegistryObject<Item> INFECTED_INFINITY_GAUNTLET = ITEMS.register("infected_infinity_gauntlet", 
            InfectedInfinityGauntlet::new);
}