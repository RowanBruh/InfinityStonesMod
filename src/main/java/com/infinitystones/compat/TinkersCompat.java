package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Compatibility with Tinkers' Construct
 * Adds Infinity Stone materials for tool crafting
 */
public class TinkersCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Material IDs
    public static final ResourceLocation SPACE_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "space_stone");
    public static final ResourceLocation MIND_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "mind_stone");
    public static final ResourceLocation REALITY_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "reality_stone");
    public static final ResourceLocation POWER_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "power_stone");
    public static final ResourceLocation TIME_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "time_stone");
    public static final ResourceLocation SOUL_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "soul_stone");
    public static final ResourceLocation INFINITY_MATERIAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_material");
    
    // Custom tool traits
    public static final ResourceLocation SPACE_WARPING_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "space_warping");
    public static final ResourceLocation MIND_CONTROL_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "mind_control");
    public static final ResourceLocation REALITY_BENDING_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "reality_bending");
    public static final ResourceLocation POWER_SURGE_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "power_surge");
    public static final ResourceLocation TIME_MANIPULATION_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "time_manipulation");
    public static final ResourceLocation SOUL_HARVEST_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "soul_harvest");
    public static final ResourceLocation INFINITY_TRAIT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity");
    
    /**
     * Initialize Tinkers' Construct compatibility
     */
    public static void init() {
        LOGGER.info("Initializing Tinkers' Construct compatibility");
        
        // Register the IMC event handler
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(TinkersCompat::enqueueIMC);
    }
    
    /**
     * Send InterModComms to Tinkers' Construct
     */
    private static void enqueueIMC(InterModEnqueueEvent event) {
        if (!ModCompat.isModLoaded(ModCompat.TINKERS_MOD_ID)) {
            return;
        }
        
        LOGGER.info("Sending Tinkers' Construct IMC messages");
        
        // Register material traits
        registerMaterialTrait(SPACE_MATERIAL, SPACE_WARPING_TRAIT);
        registerMaterialTrait(MIND_MATERIAL, MIND_CONTROL_TRAIT);
        registerMaterialTrait(REALITY_MATERIAL, REALITY_BENDING_TRAIT);
        registerMaterialTrait(POWER_MATERIAL, POWER_SURGE_TRAIT);
        registerMaterialTrait(TIME_MATERIAL, TIME_MANIPULATION_TRAIT);
        registerMaterialTrait(SOUL_MATERIAL, SOUL_HARVEST_TRAIT);
        registerMaterialTrait(INFINITY_MATERIAL, INFINITY_TRAIT);
        
        // Register materials
        registerTinkersMaterial(SPACE_MATERIAL, 4);
        registerTinkersMaterial(MIND_MATERIAL, 4);
        registerTinkersMaterial(REALITY_MATERIAL, 4);
        registerTinkersMaterial(POWER_MATERIAL, 5);
        registerTinkersMaterial(TIME_MATERIAL, 4);
        registerTinkersMaterial(SOUL_MATERIAL, 4);
        registerTinkersMaterial(INFINITY_MATERIAL, 6);
    }
    
    /**
     * Register a material trait with Tinkers' Construct
     */
    private static void registerMaterialTrait(ResourceLocation material, ResourceLocation trait) {
        InterModComms.sendTo("tconstruct", "register_material_trait", 
                () -> new MaterialTraitInfo(material, trait));
    }
    
    /**
     * Register a material with Tinkers' Construct
     */
    private static void registerTinkersMaterial(ResourceLocation id, int tier) {
        InterModComms.sendTo("tconstruct", "register_material", 
                () -> new TinkersMaterialInfo(id, tier));
    }
    
    /**
     * Helper class for material trait registration
     * This would normally use real Tinkers' API classes
     */
    private static class MaterialTraitInfo {
        private final ResourceLocation material;
        private final ResourceLocation trait;
        
        public MaterialTraitInfo(ResourceLocation material, ResourceLocation trait) {
            this.material = material;
            this.trait = trait;
        }
        
        // These methods would interact with actual Tinkers' API
    }
    
    /**
     * Helper class for material registration
     * This would normally use real Tinkers' API classes
     */
    private static class TinkersMaterialInfo {
        private final ResourceLocation id;
        private final int tier;
        
        public TinkersMaterialInfo(ResourceLocation id, int tier) {
            this.id = id;
            this.tier = tier;
        }
        
        // These methods would interact with actual Tinkers' API
    }
    
    /**
     * Define a custom Item Tier for Infinity materials for Minecraft 1.21.5
     */
    public static class InfinityTier implements Tier {
        private final float attackDamageBonus;
        private final int level;
        private final int enchantmentValue;
        private final Supplier<Ingredient> repairIngredient;
        private final float speed;
        private final int uses;
        
        public InfinityTier(
            float attackDamageBonus,
            int level,
            int enchantmentValue,
            Supplier<Ingredient> repairIngredient,
            float speed,
            int uses
        ) {
            this.attackDamageBonus = attackDamageBonus;
            this.level = level;
            this.enchantmentValue = enchantmentValue;
            this.repairIngredient = repairIngredient;
            this.speed = speed;
            this.uses = uses;
        }
        
        @Override
        public int getUses() {
            return uses;
        }
        
        @Override
        public float getSpeed() {
            return speed;
        }
        
        @Override
        public float getAttackDamageBonus() {
            return attackDamageBonus;
        }
        
        @Override
        public int getLevel() {
            return level;
        }
        
        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }
        
        @Override
        public Ingredient getRepairIngredient() {
            return repairIngredient.get();
        }
    }
}