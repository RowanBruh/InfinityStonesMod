package com.infinitystones.world.dimension;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * Handles registration of our custom biome providers and chunk generators
 */
public class ModDimensionCodec {
    
    public static final DeferredRegister<BiomeProvider.Factory<?>> BIOME_PROVIDER_TYPES = 
            DeferredRegister.create(ForgeRegistries.BIOME_PROVIDERS, InfinityStonesMod.MOD_ID);
            
    public static final DeferredRegister<ChunkGenerator.Factory<?>> CHUNK_GENERATOR_TYPES = 
            DeferredRegister.create(ForgeRegistries.CHUNK_GENERATOR_TYPES, InfinityStonesMod.MOD_ID);
    
    // Register biome provider
    public static final RegistryObject<BiomeProvider.Factory<StonesBiomeProvider>> STONE_BIOME_PROVIDER = 
            BIOME_PROVIDER_TYPES.register("stone_biome_provider",
                    () -> (codec) -> StonesBiomeProvider.CODEC);
    
    // Register chunk generator
    public static final RegistryObject<ChunkGenerator.Factory<StoneDimensionChunkGenerator>> STONE_CHUNK_GENERATOR = 
            CHUNK_GENERATOR_TYPES.register("stone_chunk_generator",
                    () -> (codec) -> StoneDimensionChunkGenerator.CODEC);
    
    /**
     * Register everything with the game's event bus
     */
    public static void register(IEventBus eventBus) {
        InfinityStonesMod.LOGGER.info("Registering dimension codecs");
        BIOME_PROVIDER_TYPES.register(eventBus);
        CHUNK_GENERATOR_TYPES.register(eventBus);
    }
}