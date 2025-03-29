package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

/**
 * Compatibility handler for Just Enough Items (JEI)
 */
public class JEICompat {
    private static final String JEI_MOD_ID = "jei";
    
    /**
     * Check if JEI is loaded
     */
    public static boolean isJEILoaded() {
        return ModList.get().isLoaded(JEI_MOD_ID);
    }
    
    /**
     * Get resource location for a JEI texture
     */
    public static ResourceLocation getJEITexture(String texturePath) {
        return new ResourceLocation(InfinityStonesMod.MOD_ID, "textures/gui/jei/" + texturePath);
    }
    
    /**
     * Register compatibility with JEI
     * This is called during mod initialization
     */
    public static void register() {
        if (!isJEILoaded()) {
            InfinityStonesMod.LOGGER.info("JEI not detected, skipping JEI compatibility setup");
            return;
        }
        
        InfinityStonesMod.LOGGER.info("Setting up JEI compatibility");
        
        // JEI plugin handles the registration automatically through the @JeiPlugin annotation
    }
}