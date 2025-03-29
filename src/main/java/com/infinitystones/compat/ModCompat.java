package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;

/**
 * Central class for handling mod compatibility
 */
public class ModCompat {
    /**
     * Initialize all compatibility handlers
     * Called during mod initialization
     */
    public static void initialize() {
        InfinityStonesMod.LOGGER.info("Initializing mod compatibility systems");
        
        // Initialize JEI compatibility
        JEICompat.register();
        
        // Initialize Curios compatibility
        CuriosCompat.register();
        
        // Initialize compatibility with future mods
        // PatchouliCompat.register();
        // TinkersConstructCompat.register();
        // BotaniaCompat.register();
        // AppliedEnergisticsCompat.register();
        // MekanismCompat.register();
        // CreateCompat.register();
        // ThermalCompat.register();
    }
}