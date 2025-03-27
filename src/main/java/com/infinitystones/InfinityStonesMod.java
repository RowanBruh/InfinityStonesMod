package com.infinitystones;

import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    // Creative Tabs
    public static final ItemGroup INFINITY_STONES_TAB = new ItemGroup("infinitystones.infinity_stones") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.INFINITY_GAUNTLET.get());
        }
    };

    public static final ItemGroup ROWAN_INDUSTRIES_TAB = new ItemGroup("infinitystones.rowan_industries") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COSMIC_BOX.get());
        }
    };

    public static final ItemGroup BONCS_ITEMS_TAB = new ItemGroup("infinitystones.boncs_items") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.GRAVITY_HAMMER.get());
        }
    };

    public InfinityStonesMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register deferred registers
        ModItems.ITEMS.register(eventBus);
        ModBlocks.register(eventBus);

        // Register mod events
        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Infinity Stones Mod: Common setup started");
        // Dimension registration and other common setup here
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("Infinity Stones Mod: Client setup started");
        // Client rendering setup here
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Register client renderers
        }
    }
}