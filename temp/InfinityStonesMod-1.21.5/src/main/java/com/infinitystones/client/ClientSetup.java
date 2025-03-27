package com.infinitystones.client;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.gui.GauntletScreen;
import com.infinitystones.items.InfinityGauntlet;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Handles client-side setup for the mod
 */
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register container screens
        // ScreenManager.registerFactory(GauntletContainer.TYPE, GauntletScreen::new);
        
        // Register special render types for blocks
        // This would be used if we had custom blocks that need special rendering
        
        // Register entity renderers
        // This would register custom entity renderers for our boss entities
    }
}