package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compatibility with Thermal Series mods
 * Adds custom energy dynamics, materials, and machine recipes
 */
public class ThermalCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Thermal integration resources
    public static final ResourceLocation INFINITY_FLUX_STORAGE = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_flux_storage");
    public static final ResourceLocation POWER_STONE_DYNAMO = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "power_stone_dynamo");
    public static final ResourceLocation REALITY_STONE_PULVERIZER = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "reality_stone_pulverizer");
    public static final ResourceLocation TIME_STONE_AUGMENT = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "time_stone_augment");
    
    // Energy capacity values for Minecraft 1.21.5
    public static final int INFINITY_FLUX_STORAGE_CAPACITY = 100000000;
    public static final int POWER_STONE_DYNAMO_PRODUCTION = 2000;
    public static final float TIME_STONE_AUGMENT_SPEED_MULTIPLIER = 5.0f;
    
    /**
     * Initialize Thermal compatibility
     */
    public static void init() {
        LOGGER.info("Initializing Thermal compatibility");
        
        // Register the IMC event handler
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ThermalCompat::enqueueIMC);
    }
    
    /**
     * Send InterModComms to Thermal
     */
    private static void enqueueIMC(InterModEnqueueEvent event) {
        if (!ModCompat.isModLoaded(ModCompat.THERMAL_MOD_ID)) {
            return;
        }
        
        LOGGER.info("Sending Thermal IMC messages");
        
        // Register flux storage
        InterModComms.sendTo("thermal", "register_energy_storage", 
                () -> new EnergyStorageInfo(
                        INFINITY_FLUX_STORAGE, 
                        INFINITY_FLUX_STORAGE_CAPACITY
                ));
        
        // Register dynamo
        InterModComms.sendTo("thermal", "register_dynamo", 
                () -> new DynamoInfo(
                        POWER_STONE_DYNAMO, 
                        POWER_STONE_DYNAMO_PRODUCTION
                ));
        
        // Register augment
        InterModComms.sendTo("thermal", "register_augment", 
                () -> new AugmentInfo(
                        TIME_STONE_AUGMENT, 
                        "machine.speed", 
                        TIME_STONE_AUGMENT_SPEED_MULTIPLIER
                ));
        
        // Register material
        InterModComms.sendTo("thermal", "register_material", 
                () -> new MaterialInfo(
                        new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_material"), 
                        0xA020F0 // Deep purple
                ));
    }
    
    /**
     * Helper class for Energy Storage registration
     * This would normally use real Thermal API classes
     */
    private static class EnergyStorageInfo {
        private final ResourceLocation id;
        private final int capacity;
        
        public EnergyStorageInfo(ResourceLocation id, int capacity) {
            this.id = id;
            this.capacity = capacity;
        }
        
        // These methods would interact with actual Thermal API
    }
    
    /**
     * Helper class for Dynamo registration
     * This would normally use real Thermal API classes
     */
    private static class DynamoInfo {
        private final ResourceLocation id;
        private final int production;
        
        public DynamoInfo(ResourceLocation id, int production) {
            this.id = id;
            this.production = production;
        }
        
        // These methods would interact with actual Thermal API
    }
    
    /**
     * Helper class for Augment registration
     * This would normally use real Thermal API classes
     */
    private static class AugmentInfo {
        private final ResourceLocation id;
        private final String type;
        private final float multiplier;
        
        public AugmentInfo(ResourceLocation id, String type, float multiplier) {
            this.id = id;
            this.type = type;
            this.multiplier = multiplier;
        }
        
        // These methods would interact with actual Thermal API
    }
    
    /**
     * Helper class for Material registration
     * This would normally use real Thermal API classes
     */
    private static class MaterialInfo {
        private final ResourceLocation id;
        private final int color;
        
        public MaterialInfo(ResourceLocation id, int color) {
            this.id = id;
            this.color = color;
        }
        
        // These methods would interact with actual Thermal API
    }
}