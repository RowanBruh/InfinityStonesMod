package com.infinitystones;

import com.infinitystones.items.ModItems;
import com.infinitystones.items.gods.GreekGodItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
}