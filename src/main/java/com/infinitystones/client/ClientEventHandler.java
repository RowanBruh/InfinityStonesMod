package com.infinitystones.client;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.blocks.ModTileEntities;
import com.infinitystones.client.render.OneWayBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register the special renderer for our One Way Block
        ClientRegistry.bindTileEntityRenderer(
                ModTileEntities.ONE_WAY_BLOCK.get(),
                OneWayBlockRenderer::new);
    }
}