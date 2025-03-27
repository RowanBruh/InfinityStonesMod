package com.infinitystones;

import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.config.ModConfig;
import com.infinitystones.items.ModItems;
import com.infinitystones.mobs.InsaneCraftBosses;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final ItemGroup INFINITY_GROUP = new ItemGroup("infinitystones") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.INFINITY_GAUNTLET.get());
        }
    };
    
    public static final ItemGroup ROWAN_INDUSTRIES = new ItemGroup("rowan_industries") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COSMIC_BOX.get());
        }
    };
    
    public InfinityStonesMod() {
        // Register the setup method for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        
        // Register configs
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        
        // Register DeferredRegisters
        ModItems.ITEMS.register(modEventBus);
        ModItems.BLOCKS.register(modEventBus);
        InsaneCraftBosses.ENTITY_TYPES.register(modEventBus);
        
        // Register the ModBlocks class for event handling
        MinecraftForge.EVENT_BUS.register(ModBlocks.class);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("Infinity Stones Mod initialized!");
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
        LOGGER.info("Infinity Stones Mod: Common Setup");
        
        // Register entity attributes
        event.enqueueWork(() -> {
            // Register Chaos Guardian attributes
            GlobalEntityTypeAttributes.put(
                    InsaneCraftBosses.CHAOS_GUARDIAN,
                    InsaneCraftBosses.ChaosGuardianEntity.getAttributes().create());
            
            // Register Cosmic Titan attributes
            GlobalEntityTypeAttributes.put(
                    InsaneCraftBosses.COSMIC_TITAN,
                    InsaneCraftBosses.CosmicTitanEntity.getAttributes().create());
        });
    }
    
    private void clientSetup(final FMLClientSetupEvent event) {
        // Client-side setup
        LOGGER.info("Infinity Stones Mod: Client Setup");
        
        // Register renderers for entities
        // In a full implementation, we'd register renderers for our custom entities here
    }
    
    /**
     * Helper method to find an item by its registry name
     * @param registryName The registry name of the item to find (e.g. "infinitystones:space_stone")
     * @return The item, or null if not found
     */
    public static Item findItem(String registryName) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
    }
}