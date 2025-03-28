package com.infinitystones;

import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.items.bionic.BionicItems;
import com.infinitystones.items.skiddzie.SkiddzieItems;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    private static final Logger LOGGER = LogManager.getLogger();

    // Creative Tabs
    public static final ItemGroup INFINITY_STONES_TAB = new ItemGroup("infinityStonesTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.DIAMOND); // Placeholder - would be replaced with an Infinity Stone
        }
    };
    
    public static final ItemGroup BONC_TAB = new ItemGroup("boncItemsTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(BionicItems.TNT_SWORD.get());
        }
    };
    
    public static final ItemGroup ROWAN_INDUSTRIES_TAB = new ItemGroup("rowanIndustriesTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(SkiddzieItems.CHAOS_STONE.get());
        }
    };

    public InfinityStonesMod() {
        // Register event listeners
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        // Register DeferredRegisters
        BionicItems.ITEMS.register(modEventBus);
        SkiddzieItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlocks.TILE_ENTITIES.register(modEventBus);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Infinity Stones Mod initializing...");
        // Common setup code
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Infinity Stones Mod client setup...");
        // Client-only setup code
    }
}