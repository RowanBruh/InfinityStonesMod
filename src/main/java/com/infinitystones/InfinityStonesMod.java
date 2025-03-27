package com.infinitystones;

import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.items.ModItems;
import com.infinitystones.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main mod class for the Infinity Stones mod
 */
@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    
    // Logger for the mod
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * Constructor for the mod instance
     */
    public InfinityStonesMod() {
        // Register the setup method for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        
        // Register item and block deferred registers
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("Infinity Stones Mod initialized");
    }
    
    /**
     * Setup for common initialization
     */
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Infinity Stones Mod common setup");
        
        // Initialize network handler
        NetworkHandler.init();
    }
    
    /**
     * Setup for client-specific initialization
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Infinity Stones Mod client setup");
        
        // Register client-side components (screens, renderers, etc.)
    }
}