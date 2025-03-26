package com.infinitystones.client;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.gui.GauntletScreen;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    
    public static void init() {
        // Register screen for the Infinity Gauntlet container
        ScreenManager.registerFactory(InfinityStonesMod.GAUNTLET_CONTAINER, GauntletScreen::new);
        
        // Register entity renderers for Insane Craft bosses
        // This would be implemented with custom entity renderer registration
    }
}
