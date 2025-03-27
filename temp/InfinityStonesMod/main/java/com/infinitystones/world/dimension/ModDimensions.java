package com.infinitystones.world.dimension;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Registers and manages custom dimensions for the Infinity Stones mod
 */
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID)
public class ModDimensions {
    
    // Create dimension registry keys for each Infinity Stone dimension
    public static final RegistryKey<World> SOUL_DIMENSION = RegistryKey.getOrCreateKey(
            Registry.WORLD_KEY,
            new ResourceLocation(InfinityStonesMod.MOD_ID, "soul_dimension")
    );
    
    public static final RegistryKey<World> TIME_DIMENSION = RegistryKey.getOrCreateKey(
            Registry.WORLD_KEY,
            new ResourceLocation(InfinityStonesMod.MOD_ID, "time_dimension")
    );
    
    public static final RegistryKey<World> SPACE_DIMENSION = RegistryKey.getOrCreateKey(
            Registry.WORLD_KEY,
            new ResourceLocation(InfinityStonesMod.MOD_ID, "space_dimension")
    );
    
    public static final RegistryKey<World> REALITY_DIMENSION = RegistryKey.getOrCreateKey(
            Registry.WORLD_KEY,
            new ResourceLocation(InfinityStonesMod.MOD_ID, "reality_dimension")
    );
    
    public static final RegistryKey<World> POWER_DIMENSION = RegistryKey.getOrCreateKey(
            Registry.WORLD_KEY,
            new ResourceLocation(InfinityStonesMod.MOD_ID, "power_dimension")
    );
    
    public static final RegistryKey<World> MIND_DIMENSION = RegistryKey.getOrCreateKey(
            Registry.WORLD_KEY,
            new ResourceLocation(InfinityStonesMod.MOD_ID, "mind_dimension")
    );
    
    /**
     * Register all dimension-related components with the game event bus
     */
    public static void register(IEventBus eventBus) {
        InfinityStonesMod.LOGGER.info("Registering Infinity Stone dimensions");
    }
}