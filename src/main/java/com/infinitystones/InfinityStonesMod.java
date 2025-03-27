package com.infinitystones;

import org.slf4j.Logger;

import com.infinitystones.items.NanoTechItems;
import com.infinitystones.registry.ModCreativeTabs;
import com.mojang.logging.LogUtils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main class for the Infinity Stones Mod.
 * Handles initialization and registration of mod components.
 */
@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public InfinityStonesMod() {
        // Get the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register creative tabs
        ModCreativeTabs.register(modEventBus);
        
        // Register items
        NanoTechItems.register(modEventBus);
        
        // Register setup methods
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // Register forge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup code here
        LOGGER.info("COMMON SETUP");
        
        event.enqueueWork(() -> {
            // Add items to creative tabs after registration
            NanoTechItems.addItemsToTabs();
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        // Client-side setup code here
        LOGGER.info("CLIENT SETUP");
    }
}