package com.infinitystones;

import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.compat.ModCompat;
import com.infinitystones.entities.ModEntities;
import com.infinitystones.items.ModItems;
import com.infinitystones.items.bionic.BionicItems;
import com.infinitystones.items.gods.GreekGodItems;
import com.infinitystones.items.skiddzie.SkiddzieItems;
import com.infinitystones.networking.ModMessages;
import com.infinitystones.world.dimensions.ModDimensions;
import com.infinitystones.world.structures.ModStructures;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

/**
 * Main mod class for the Infinity Stones Mod
 */
@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    // Creative mode tabs
    public static CreativeModeTab INFINITY_STONES_TAB;
    public static CreativeModeTab BIONIC_ITEMS_TAB;
    public static CreativeModeTab ROWAN_INDUSTRIES_TAB;
    public static CreativeModeTab GREEK_GOD_ITEMS_TAB;
    
    public InfinityStonesMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register items and blocks
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        BionicItems.register(modEventBus);
        SkiddzieItems.register(modEventBus);
        GreekGodItems.register(modEventBus);
        
        // Register entities
        ModEntities.register(modEventBus);
        
        // Register dimensions
        ModDimensions.register();
        
        // Register structures
        ModStructures.register(modEventBus);
        
        // Register event listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    /**
     * Common setup method
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Initialize network handling
        ModMessages.register();
        
        // Initialize mod compatibility systems
        event.enqueueWork(ModCompat::initialize);
        
        LOGGER.info("Infinity Stones Mod initialized successfully");
    }
    
    /**
     * Create creative mode tabs
     */
    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == INFINITY_STONES_TAB) {
            // Add Infinity Stones
            event.accept(ModItems.SPACE_STONE.get());
            event.accept(ModItems.MIND_STONE.get());
            event.accept(ModItems.REALITY_STONE.get());
            event.accept(ModItems.POWER_STONE.get());
            event.accept(ModItems.TIME_STONE.get());
            event.accept(ModItems.SOUL_STONE.get());
            
            // Add Gauntlets
            event.accept(ModItems.INFINITY_GAUNTLET.get());
            event.accept(ModItems.ADVANCED_INFINITY_GAUNTLET.get());
        }
        
        if (event.getTab() == BIONIC_ITEMS_TAB) {
            // Add Bionic's items
            event.accept(BionicItems.BIONIC_ULTRA_PICKAXE.get());
            event.accept(BionicItems.BIONIC_ULTRA_SWORD.get());
            event.accept(BionicItems.BIONIC_ULTRA_AXE.get());
            event.accept(BionicItems.BIONIC_ULTRA_HELMET.get());
            event.accept(BionicItems.BIONIC_ULTRA_CHESTPLATE.get());
            event.accept(BionicItems.BIONIC_ULTRA_LEGGINGS.get());
            event.accept(BionicItems.BIONIC_ULTRA_BOOTS.get());
            event.accept(BionicItems.BIONIC_BASE_LOCATOR.get());
        }
        
        if (event.getTab() == ROWAN_INDUSTRIES_TAB) {
            // Add Skiddzie's items
            event.accept(SkiddzieItems.ROWAN_LUCKY_BLOCK.get());
            event.accept(SkiddzieItems.SKIDDZIE_BASE_LOCATOR.get());
            event.accept(SkiddzieItems.SKEPPY_ARENA_LOCATOR.get());
            event.accept(SkiddzieItems.ADMIN_COMMAND_BLOCK.get());
            event.accept(SkiddzieItems.ONE_WAY_BLOCK.get());
            event.accept(SkiddzieItems.TNT_TRAP_BLOCK.get());
            event.accept(SkiddzieItems.ARROW_TRAP_BLOCK.get());
            event.accept(SkiddzieItems.PITFALL_TRAP_BLOCK.get());
            
            // Add infected items
            event.accept(ModItems.INFECTED_SPACE_STONE.get());
            event.accept(ModItems.INFECTED_MIND_STONE.get());
            event.accept(ModItems.INFECTED_REALITY_STONE.get());
            event.accept(ModItems.INFECTED_POWER_STONE.get());
            event.accept(ModItems.INFECTED_TIME_STONE.get());
            event.accept(ModItems.INFECTED_SOUL_STONE.get());
            event.accept(ModItems.INFECTED_INFINITY_GAUNTLET.get());
            
            // Add nano tech items
            event.accept(ModItems.NANO_TECH_CORE.get());
            event.accept(ModItems.NANO_TECH_HELMET.get());
            event.accept(ModItems.NANO_TECH_CHESTPLATE.get());
            event.accept(ModItems.NANO_TECH_LEGGINGS.get());
            event.accept(ModItems.NANO_TECH_BOOTS.get());
            event.accept(ModItems.NANO_TECH_SWORD.get());
            event.accept(ModItems.NANO_TECH_PICKAXE.get());
            
            // Add infected nano tech items
            event.accept(ModItems.INFECTED_NANO_TECH_CORE.get());
            event.accept(ModItems.INFECTED_NANO_TECH_HELMET.get());
            event.accept(ModItems.INFECTED_NANO_TECH_CHESTPLATE.get());
            event.accept(ModItems.INFECTED_NANO_TECH_LEGGINGS.get());
            event.accept(ModItems.INFECTED_NANO_TECH_BOOTS.get());
            event.accept(ModItems.INFECTED_NANO_TECH_SWORD.get());
            event.accept(ModItems.INFECTED_NANO_TECH_PICKAXE.get());
        }
        
        if (event.getTab() == GREEK_GOD_ITEMS_TAB) {
            // Add Greek God items
            event.accept(GreekGodItems.ZEUS_LIGHTNING_BOLT.get());
            event.accept(GreekGodItems.POSEIDON_TRIDENT.get());
            event.accept(GreekGodItems.HADES_HELMET.get());
            event.accept(GreekGodItems.ATHENA_SHIELD.get());
            event.accept(GreekGodItems.APOLLO_BOW.get());
            event.accept(GreekGodItems.HERMES_BOOTS.get());
            event.accept(GreekGodItems.ARTEMIS_BOW.get());
            event.accept(GreekGodItems.ARES_SWORD.get());
        }
    }
    
    /**
     * Register creative mode tabs
     */
    @SubscribeEvent
    public void onCreativeModeTabRegister(CreativeModeTabEvent.Register event) {
        // Register Infinity Stones tab
        INFINITY_STONES_TAB = event.registerCreativeModeTab(
                new ResourceLocation(MOD_ID, "infinity_stones_tab"),
                builder -> builder.icon(() -> ModItems.INFINITY_GAUNTLET.get().getDefaultInstance())
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup.infinitystones"))
        );
        
        // Register Bionic Items tab (named "Bonc's Items")
        BIONIC_ITEMS_TAB = event.registerCreativeModeTab(
                new ResourceLocation(MOD_ID, "bionic_items_tab"),
                builder -> builder.icon(() -> BionicItems.BIONIC_ULTRA_PICKAXE.get().getDefaultInstance())
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup.infinitystones.bionics"))
        );
        
        // Register Rowan Industries tab
        ROWAN_INDUSTRIES_TAB = event.registerCreativeModeTab(
                new ResourceLocation(MOD_ID, "rowan_industries_tab"),
                builder -> builder.icon(() -> SkiddzieItems.ROWAN_LUCKY_BLOCK.get().asItem().getDefaultInstance())
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup.infinitystones.rowan"))
        );
        
        // Register Greek God Items tab
        GREEK_GOD_ITEMS_TAB = event.registerCreativeModeTab(
                new ResourceLocation(MOD_ID, "greek_god_items_tab"),
                builder -> builder.icon(() -> GreekGodItems.ZEUS_LIGHTNING_BOLT.get().getDefaultInstance())
                        .title(net.minecraft.network.chat.Component.translatable("itemGroup.infinitystones.greek_gods"))
        );
    }
    
    // Server setup event handler
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Infinity Stones Mod server components initialized");
    }
    
    /**
     * Client-specific setup and registration
     */
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Infinity Stones Mod client setup complete");
        }
    }
}