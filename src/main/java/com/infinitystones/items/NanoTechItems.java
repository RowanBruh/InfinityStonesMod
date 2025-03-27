package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.custom.NanoTechArmorItem;
import com.infinitystones.items.custom.NanoTechCoreItem;
import com.infinitystones.items.custom.NanoTechPickaxeItem;
import com.infinitystones.items.custom.NanoTechSwordItem;
import com.infinitystones.registry.ModCreativeTabs;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class NanoTechItems {
    // DeferredRegister for items
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Custom armor materials
    public static final ArmorMaterial NANO_TECH_ARMOR_MATERIAL = new NanoTechArmorMaterial(
            "nano_tech",
            35, // durability multiplier
            new int[] {3, 6, 8, 3}, // protection values
            18, // enchantability
            SoundEvents.ARMOR_EQUIP_NETHERITE, // equip sound
            1.0f, // toughness
            0.0f, // knockback resistance
            Ingredient.EMPTY // repair ingredient - will be set in recipe
    );
    
    public static final ArmorMaterial INFECTED_NANO_TECH_ARMOR_MATERIAL = new NanoTechArmorMaterial(
            "infected_nano_tech",
            40, // durability multiplier
            new int[] {4, 7, 9, 4}, // protection values
            15, // enchantability
            SoundEvents.ARMOR_EQUIP_NETHERITE, // equip sound
            2.0f, // toughness
            0.1f, // knockback resistance
            Ingredient.EMPTY // repair ingredient - will be set in recipe
    );
    
    // Regular Nano Tech items
    public static final RegistryObject<Item> NANO_TECH_CORE = ITEMS.register("nano_tech_core",
            () -> new NanoTechCoreItem(defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    public static final RegistryObject<Item> NANO_TECH_SWORD = ITEMS.register("nano_tech_sword",
            () -> new NanoTechSwordItem(Tiers.NETHERITE, 5, -2.4f, defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    public static final RegistryObject<Item> NANO_TECH_PICKAXE = ITEMS.register("nano_tech_pickaxe",
            () -> new NanoTechPickaxeItem(Tiers.NETHERITE, 1, -2.8f, defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    // Nano Tech Armor
    public static final RegistryObject<Item> NANO_TECH_HELMET = ITEMS.register("nano_tech_helmet",
            () -> new NanoTechArmorItem(NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.HEAD, 
                    defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    public static final RegistryObject<Item> NANO_TECH_CHESTPLATE = ITEMS.register("nano_tech_chestplate",
            () -> new NanoTechArmorItem(NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.CHEST, 
                    defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    public static final RegistryObject<Item> NANO_TECH_LEGGINGS = ITEMS.register("nano_tech_leggings",
            () -> new NanoTechArmorItem(NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.LEGS, 
                    defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    public static final RegistryObject<Item> NANO_TECH_BOOTS = ITEMS.register("nano_tech_boots",
            () -> new NanoTechArmorItem(NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.FEET, 
                    defaultItemProps().rarity(Rarity.UNCOMMON), false));
    
    // Infected Nano Tech items
    public static final RegistryObject<Item> INFECTED_NANO_TECH_CORE = ITEMS.register("infected_nano_tech_core",
            () -> new NanoTechCoreItem(defaultItemProps().rarity(Rarity.RARE), true));
    
    public static final RegistryObject<Item> INFECTED_NANO_TECH_SWORD = ITEMS.register("infected_nano_tech_sword",
            () -> new NanoTechSwordItem(Tiers.NETHERITE, 8, -2.2f, defaultItemProps().rarity(Rarity.RARE), true));
    
    public static final RegistryObject<Item> INFECTED_NANO_TECH_PICKAXE = ITEMS.register("infected_nano_tech_pickaxe",
            () -> new NanoTechPickaxeItem(Tiers.NETHERITE, 2, -2.6f, defaultItemProps().rarity(Rarity.RARE), true));
    
    // Infected Nano Tech Armor
    public static final RegistryObject<Item> INFECTED_NANO_TECH_HELMET = ITEMS.register("infected_nano_tech_helmet",
            () -> new NanoTechArmorItem(INFECTED_NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.HEAD, 
                    defaultItemProps().rarity(Rarity.RARE), true));
    
    public static final RegistryObject<Item> INFECTED_NANO_TECH_CHESTPLATE = ITEMS.register("infected_nano_tech_chestplate",
            () -> new NanoTechArmorItem(INFECTED_NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.CHEST, 
                    defaultItemProps().rarity(Rarity.RARE), true));
    
    public static final RegistryObject<Item> INFECTED_NANO_TECH_LEGGINGS = ITEMS.register("infected_nano_tech_leggings",
            () -> new NanoTechArmorItem(INFECTED_NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.LEGS, 
                    defaultItemProps().rarity(Rarity.RARE), true));
    
    public static final RegistryObject<Item> INFECTED_NANO_TECH_BOOTS = ITEMS.register("infected_nano_tech_boots",
            () -> new NanoTechArmorItem(INFECTED_NANO_TECH_ARMOR_MATERIAL, EquipmentSlot.FEET, 
                    defaultItemProps().rarity(Rarity.RARE), true));
    
    private static Item.Properties defaultItemProps() {
        return new Item.Properties();
    }
    
    /**
     * Register this class with the event bus
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    
    /**
     * Adds items to the creative mode tabs
     */
    public static void addItemsToTabs() {
        // Add regular Nano Tech items to Bonc's Items tab
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_CORE.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_SWORD.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_PICKAXE.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_HELMET.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_CHESTPLATE.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_LEGGINGS.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.BONCS_ITEMS_TAB, NANO_TECH_BOOTS.get());
        
        // Add infected Nano Tech items to Rowan Industries tab
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_CORE.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_SWORD.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_PICKAXE.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_HELMET.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_CHESTPLATE.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_LEGGINGS.get());
        ModCreativeTabs.addToTab(ModCreativeTabs.ROWAN_INDUSTRIES_TAB, INFECTED_NANO_TECH_BOOTS.get());
    }
    
    /**
     * Custom armor material implementation for Nano Tech armor
     */
    private static class NanoTechArmorMaterial implements ArmorMaterial {
        private static final int[] HEALTH_PER_SLOT = new int[] {13, 15, 16, 11};
        private final String name;
        private final int durabilityMultiplier;
        private final int[] protectionAmounts;
        private final int enchantability;
        private final net.minecraft.sounds.SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final Ingredient repairIngredient;
        
        public NanoTechArmorMaterial(String name, int durabilityMultiplier, int[] protectionAmounts, 
                int enchantability, net.minecraft.sounds.SoundEvent equipSound, float toughness, 
                float knockbackResistance, Ingredient repairIngredient) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.protectionAmounts = protectionAmounts;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredient = repairIngredient;
        }
        
        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
        }
        
        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return this.protectionAmounts[slot.getIndex()];
        }
        
        @Override
        public int getEnchantmentValue() {
            return this.enchantability;
        }
        
        @Override
        public net.minecraft.sounds.SoundEvent getEquipSound() {
            return this.equipSound;
        }
        
        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient;
        }
        
        @Override
        public String getName() {
            return InfinityStonesMod.MOD_ID + ":" + this.name;
        }
        
        @Override
        public float getToughness() {
            return this.toughness;
        }
        
        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }
}