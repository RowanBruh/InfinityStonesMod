package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compatibility with Create mod
 * Adds custom materials, processing recipes, and contraptions
 */
public class CreateCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Create integration resources
    public static final ResourceLocation INFINITY_COGWHEEL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_cogwheel");
    public static final ResourceLocation REALITY_STONE_MIXER = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "reality_stone_mixer");
    public static final ResourceLocation TIME_STONE_SEQUENCER = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "time_stone_sequencer");
    public static final ResourceLocation SPACE_STONE_SCHEMATICANNON = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "space_stone_schematicannon");
    
    // Stress values for Minecraft 1.21.5
    public static final double INFINITY_COGWHEEL_STRESS_CAPACITY = 2048.0;
    public static final double REALITY_STONE_MIXER_STRESS_IMPACT = 8.0;
    public static final double TIME_STONE_SEQUENCER_STRESS_IMPACT = 4.0;
    
    /**
     * Initialize Create compatibility
     */
    public static void init() {
        LOGGER.info("Initializing Create compatibility");
        
        // Register the IMC event handler
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CreateCompat::enqueueIMC);
    }
    
    /**
     * Send InterModComms to Create
     */
    private static void enqueueIMC(InterModEnqueueEvent event) {
        if (!ModCompat.isModLoaded(ModCompat.CREATE_MOD_ID)) {
            return;
        }
        
        LOGGER.info("Sending Create IMC messages");
        
        // Register kinetic blocks
        registerKineticBlock(INFINITY_COGWHEEL, INFINITY_COGWHEEL_STRESS_CAPACITY, 0.0);
        registerKineticBlock(REALITY_STONE_MIXER, 0.0, REALITY_STONE_MIXER_STRESS_IMPACT);
        registerKineticBlock(TIME_STONE_SEQUENCER, 0.0, TIME_STONE_SEQUENCER_STRESS_IMPACT);
        
        // Register custom goggles
        InterModComms.sendTo("create", "register_goggle_overrides", 
                () -> new GoggleInfo("infinitystones:mind_stone"));
        
        // Register custom materials
        InterModComms.sendTo("create", "register_material", 
                () -> new MaterialInfo(
                        new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_material"),
                        0xA020F0 // Deep purple
                ));
    }
    
    /**
     * Register a kinetic block with Create
     */
    private static void registerKineticBlock(ResourceLocation id, double capacity, double impact) {
        InterModComms.sendTo("create", "register_kinetic_block", 
                () -> new KineticBlockInfo(id, capacity, impact));
    }
    
    /**
     * Helper class for Kinetic Block registration
     * This would normally use real Create API classes
     */
    private static class KineticBlockInfo {
        private final ResourceLocation id;
        private final double capacity;
        private final double impact;
        
        public KineticBlockInfo(ResourceLocation id, double capacity, double impact) {
            this.id = id;
            this.capacity = capacity;
            this.impact = impact;
        }
        
        // These methods would interact with actual Create API
    }
    
    /**
     * Helper class for Goggle registration
     * This would normally use real Create API classes
     */
    private static class GoggleInfo {
        private final String itemId;
        
        public GoggleInfo(String itemId) {
            this.itemId = itemId;
        }
        
        // These methods would interact with actual Create API
    }
    
    /**
     * Helper class for Material registration
     * This would normally use real Create API classes
     */
    private static class MaterialInfo {
        private final ResourceLocation id;
        private final int color;
        
        public MaterialInfo(ResourceLocation id, int color) {
            this.id = id;
            this.color = color;
        }
        
        // These methods would interact with actual Create API
    }
}