package com.infinitystones;

import com.infinitystones.client.KeyBindings;
import com.infinitystones.items.ModItems;
import com.infinitystones.items.gods.GreekGodItems;
import com.infinitystones.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    public static final Logger LOGGER = LogManager.getLogger();

    public InfinityStonesMod() {
        LOGGER.info("Initializing Infinity Stones Mod");
        
        // Register with the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register items
        ModItems.ITEMS.register(modEventBus);
        
        // Register Greek God items
        GreekGodItems.ITEMS.register(modEventBus);
        
        // Register setup methods
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    /**
     * Common setup for both client and server
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Initialize network handler
        NetworkHandler.register();
    }
    
    /**
     * Client-only setup
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        // Initialize key bindings on client side
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> KeyBindings::register);
    }
}