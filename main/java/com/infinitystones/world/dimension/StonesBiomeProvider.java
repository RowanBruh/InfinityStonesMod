package com.infinitystones.world.dimension;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.SimplexNoiseGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides biomes for custom Infinity Stone dimensions
 */
public class StonesBiomeProvider extends BiomeProvider {
    // Codec for serialization
    public static final Codec<StonesBiomeProvider> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(provider -> provider.biomeRegistry),
            Codec.LONG.fieldOf("seed").stable().forGetter(provider -> provider.seed),
            Codec.STRING.fieldOf("dimension_type").stable().forGetter(provider -> provider.dimensionType)
        ).apply(instance, instance.stable(StonesBiomeProvider::new))
    );
    
    private final Registry<Biome> biomeRegistry;
    private final long seed;
    private final String dimensionType;
    private final SimplexNoiseGenerator generator;
    private final List<Biome> biomes;
    
    public StonesBiomeProvider(Registry<Biome> biomeRegistry, long seed, String dimensionType) {
        this.biomeRegistry = biomeRegistry;
        this.seed = seed;
        this.dimensionType = dimensionType;
        this.generator = new SimplexNoiseGenerator(seed);
        
        // Get the biomes based on the dimension type
        if (dimensionType.equals("soul")) {
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.SOUL_SAND_VALLEY),
                    biomeRegistry.getOrThrow(Biomes.WARPED_FOREST)
            );
        } else if (dimensionType.equals("time")) {
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.END_BARRENS),
                    biomeRegistry.getOrThrow(Biomes.END_HIGHLANDS)
            );
        } else if (dimensionType.equals("space")) {
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.END_MIDLANDS),
                    biomeRegistry.getOrThrow(Biomes.SMALL_END_ISLANDS)
            );
        } else if (dimensionType.equals("reality")) {
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.CRIMSON_FOREST),
                    biomeRegistry.getOrThrow(Biomes.NETHER_WASTES)
            );
        } else if (dimensionType.equals("power")) {
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.BASALT_DELTAS),
                    biomeRegistry.getOrThrow(Biomes.NETHER_WASTES)
            );
        } else if (dimensionType.equals("mind")) {
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.JUNGLE),
                    biomeRegistry.getOrThrow(Biomes.JUNGLE_EDGE)
            );
        } else {
            // Default biomes if no matching dimension type
            this.biomes = ImmutableList.of(
                    biomeRegistry.getOrThrow(Biomes.PLAINS),
                    biomeRegistry.getOrThrow(Biomes.DESERT)
            );
        }
    }
    
    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }
    
    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        return new StonesBiomeProvider(this.biomeRegistry, seed, this.dimensionType);
    }
    
    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        // Use the noise generator to blend between available biomes
        double noise = generator.getValue(x * 0.1, z * 0.1, false);
        int index = Math.abs((int) (noise * biomes.size())) % biomes.size();
        return biomes.get(index);
    }
    
    @Override
    public List<Biome> getBiomes() {
        return biomes;
    }
}