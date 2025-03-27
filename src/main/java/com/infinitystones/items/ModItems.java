package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityStones.StoneType;
import com.infinitystones.items.InsaneCraftWeapons.RoyalGuardianSword;
import com.infinitystones.items.InsaneCraftWeapons.UltimateBow;
import com.infinitystones.items.InsaneCraftWeapons.ThorHammer;
import com.infinitystones.items.SkiddzieCustomBoxes.BoxRarity;
import com.infinitystones.items.SkiddzieCustomBoxes.CustomBoxItem;
import com.infinitystones.items.SkiddzieSpecialItems.UltraGrapplingHook;
import com.infinitystones.items.SkiddzieSpecialItems.GravityHammer;
import com.infinitystones.items.SkiddzieSpecialItems.LightningRod;
import com.infinitystones.items.RowanLuckyBlock;
import com.infinitystones.items.RowanLuckyBlock.RowanLuckyBlockItem;
import com.infinitystones.items.AdvancedInfinityGauntlet;
import com.infinitystones.items.NanoTechItems.NanoTechArmorItem;
import com.infinitystones.items.NanoTechItems.NanoTechCore;
import com.infinitystones.items.NanoTechItems.NanoTechSword;
import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InfinityStonesMod.MOD_ID);
    
    // Infinity Stones
    public static final RegistryObject<Item> SPACE_STONE = ITEMS.register(
            StoneType.SPACE.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.SPACE));
    
    public static final RegistryObject<Item> MIND_STONE = ITEMS.register(
            StoneType.MIND.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.MIND));
    
    public static final RegistryObject<Item> REALITY_STONE = ITEMS.register(
            StoneType.REALITY.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.REALITY));
    
    public static final RegistryObject<Item> POWER_STONE = ITEMS.register(
            StoneType.POWER.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.POWER));
    
    public static final RegistryObject<Item> TIME_STONE = ITEMS.register(
            StoneType.TIME.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.TIME));
    
    public static final RegistryObject<Item> SOUL_STONE = ITEMS.register(
            StoneType.SOUL.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.SOUL));
    
    // Infinity Gauntlet
    public static final RegistryObject<Item> INFINITY_GAUNTLET = ITEMS.register(
            "infinity_gauntlet",
            () -> new InfinityGauntlet());
            
    // Advanced Infinity Gauntlet with combined powers
    public static final RegistryObject<Item> ADVANCED_INFINITY_GAUNTLET = ITEMS.register(
            "advanced_infinity_gauntlet",
            () -> new AdvancedInfinityGauntlet());
    
    // Base Insane Craft Items
    public static final RegistryObject<Item> ULTIMATE_INGOT = ITEMS.register(
            "ultimate_ingot",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
    
    public static final RegistryObject<Item> CHAOS_SHARD = ITEMS.register(
            "chaos_shard",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
    
    public static final RegistryObject<Item> COSMIC_DUST = ITEMS.register(
            "cosmic_dust",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
            
    // Special crafting components from Insane Craft
    public static final RegistryObject<Item> DRAGON_SCALE = ITEMS.register(
            "dragon_scale",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
            
    public static final RegistryObject<Item> WITHERED_BONE = ITEMS.register(
            "withered_bone",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
            
    public static final RegistryObject<Item> COSMIC_PEARL = ITEMS.register(
            "cosmic_pearl",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
            
    public static final RegistryObject<Item> ENCHANTED_METAL = ITEMS.register(
            "enchanted_metal",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
            
    // Insane Craft Weapons
    public static final RegistryObject<Item> ROYAL_GUARDIAN_SWORD = ITEMS.register(
            "royal_guardian_sword",
            RoyalGuardianSword::new);
            
    public static final RegistryObject<Item> ULTIMATE_BOW = ITEMS.register(
            "ultimate_bow",
            UltimateBow::new);
            
    public static final RegistryObject<Item> THOR_HAMMER = ITEMS.register(
            "thor_hammer",
            ThorHammer::new);
            
    // Overpowered tools from Insane Craft
    public static final RegistryObject<Item> SUPER_BREAKER = ITEMS.register(
            "super_breaker",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP).maxStackSize(1)));
            
    public static final RegistryObject<Item> ULTIMATE_FISHING_ROD = ITEMS.register(
            "ultimate_fishing_rod",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP).maxStackSize(1)));
            
    // Infinity Armor Set
    public static final RegistryObject<Item> INFINITY_HELMET = ITEMS.register(
            "infinity_helmet",
            () -> new InfinityArmorItem(EquipmentSlotType.HEAD));
            
    public static final RegistryObject<Item> INFINITY_CHESTPLATE = ITEMS.register(
            "infinity_chestplate",
            () -> new InfinityArmorItem(EquipmentSlotType.CHEST));
            
    public static final RegistryObject<Item> INFINITY_LEGGINGS = ITEMS.register(
            "infinity_leggings",
            () -> new InfinityArmorItem(EquipmentSlotType.LEGS));
            
    public static final RegistryObject<Item> INFINITY_BOOTS = ITEMS.register(
            "infinity_boots",
            () -> new InfinityArmorItem(EquipmentSlotType.FEET));
            
    // SkiddziePlays Custom Boxes
    public static final RegistryObject<Item> COMMON_BOX = ITEMS.register(
            BoxRarity.COMMON.getRegistryName(),
            () -> new CustomBoxItem(BoxRarity.COMMON, InfinityStonesMod.ROWAN_INDUSTRIES));
            
    public static final RegistryObject<Item> RARE_BOX = ITEMS.register(
            BoxRarity.RARE.getRegistryName(),
            () -> new CustomBoxItem(BoxRarity.RARE, InfinityStonesMod.ROWAN_INDUSTRIES));
            
    public static final RegistryObject<Item> EPIC_BOX = ITEMS.register(
            BoxRarity.EPIC.getRegistryName(),
            () -> new CustomBoxItem(BoxRarity.EPIC, InfinityStonesMod.ROWAN_INDUSTRIES));
            
    public static final RegistryObject<Item> LEGENDARY_BOX = ITEMS.register(
            BoxRarity.LEGENDARY.getRegistryName(),
            () -> new CustomBoxItem(BoxRarity.LEGENDARY, InfinityStonesMod.ROWAN_INDUSTRIES));
            
    public static final RegistryObject<Item> COSMIC_BOX = ITEMS.register(
            BoxRarity.COSMIC.getRegistryName(),
            () -> new CustomBoxItem(BoxRarity.COSMIC, InfinityStonesMod.ROWAN_INDUSTRIES));
            
    public static final RegistryObject<Item> INSANE_BOX = ITEMS.register(
            BoxRarity.INSANE.getRegistryName(),
            () -> new CustomBoxItem(BoxRarity.INSANE, InfinityStonesMod.ROWAN_INDUSTRIES));
            
    // SkiddziePlays custom items
    public static final RegistryObject<Item> COSMIC_FRAGMENT = ITEMS.register(
            "cosmic_fragment",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
            
    // SkiddziePlays special tools
    public static final RegistryObject<Item> ULTRA_GRAPPLING_HOOK = ITEMS.register(
            "ultra_grappling_hook",
            UltraGrapplingHook::new);
            
    public static final RegistryObject<Item> GRAVITY_HAMMER = ITEMS.register(
            "gravity_hammer",
            GravityHammer::new);
            
    public static final RegistryObject<Item> LIGHTNING_ROD = ITEMS.register(
            "lightning_rod",
            LightningRod::new);
            
    // Rowan Industries Lucky Block
    public static final RegistryObject<Block> ROWAN_LUCKY_BLOCK = BLOCKS.register(
            "rowan_lucky_block",
            RowanLuckyBlock::new);
            
    public static final RegistryObject<Item> ROWAN_LUCKY_BLOCK_ITEM = ITEMS.register(
            "rowan_lucky_block",
            () -> new RowanLuckyBlockItem(ROWAN_LUCKY_BLOCK.get()));
            
    // Infected Nano Tech items
    public static final RegistryObject<Item> NANO_TECH_CORE = ITEMS.register(
            "nano_tech_core",
            NanoTechCore::new);
            
    public static final RegistryObject<Item> NANO_TECH_HELMET = ITEMS.register(
            "nano_tech_helmet",
            () -> new NanoTechArmorItem(EquipmentSlotType.HEAD));
            
    public static final RegistryObject<Item> NANO_TECH_ARMOR = ITEMS.register(
            "nano_tech_armor",
            () -> new NanoTechArmorItem(EquipmentSlotType.CHEST));
            
    public static final RegistryObject<Item> NANO_TECH_LEGGINGS = ITEMS.register(
            "nano_tech_leggings",
            () -> new NanoTechArmorItem(EquipmentSlotType.LEGS));
            
    public static final RegistryObject<Item> NANO_TECH_BOOTS = ITEMS.register(
            "nano_tech_boots",
            () -> new NanoTechArmorItem(EquipmentSlotType.FEET));
            
    public static final RegistryObject<Item> NANO_TECH_SWORD = ITEMS.register(
            "nano_tech_sword",
            NanoTechSword::new);
}
