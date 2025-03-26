package com.infinitystones;

import com.infinitystones.config.ModConfig;
import com.infinitystones.items.ModItems;
import com.infinitystones.client.ClientSetup;
import com.infinitystones.events.StoneEvents;
import com.infinitystones.gui.GauntletContainer;
import com.infinitystones.mobs.InsaneCraftBosses;
import com.infinitystones.world.OreGeneration;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ObjectHolder;

// The main mod class
@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    
    // Create a custom creative tab for mod items
    public static final ItemGroup INFINITY_GROUP = new ItemGroup("infinitystones") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.INFINITY_GAUNTLET.get());
        }
    };
    
    @ObjectHolder(MOD_ID + ":gauntlet_container")
    public static ContainerType<GauntletContainer> GAUNTLET_CONTAINER;
    
    public InfinityStonesMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register the setup methods for modloading
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new StoneEvents());
        
        // Register our items and entities
        ModItems.ITEMS.register(modEventBus);
        InsaneCraftBosses.ENTITY_TYPES.register(modEventBus);
        
        // Register Config
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        ModConfig.loadConfig(ModConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        // Initialize ore generation
        OreGeneration.setupOreGeneration();
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        // Register client side handlers
        ClientSetup.init();
    }
    
    // Register the container for the gauntlet GUI
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(new ContainerType<>(GauntletContainer::new)
                    .setRegistryName(new ResourceLocation(MOD_ID, "gauntlet_container")));
        }
    }
}
