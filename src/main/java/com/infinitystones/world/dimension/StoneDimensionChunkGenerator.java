package com.infinitystones.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Custom chunk generator for Infinity Stone dimensions
 */
public class StoneDimensionChunkGenerator extends ChunkGenerator {

    // Codec for serialization
    public static final Codec<StoneDimensionChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeProvider.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeProvider),
                    Codec.LONG.fieldOf("seed").stable().forGetter(generator -> generator.seed),
                    Codec.STRING.fieldOf("dimension_type").stable().forGetter(generator -> generator.dimensionType)
            ).apply(instance, instance.stable(StoneDimensionChunkGenerator::new))
    );

    private final long seed;
    private final String dimensionType;
    private static final Blockreader EMPTY_BLOCK_READER = new Blockreader(new BlockState[0]);
    
    // Map to store dimension type to default block
    private static final Map<String, BlockState> DIMENSION_BLOCKS = new HashMap<>();
    static {
        DIMENSION_BLOCKS.put("soul", Blocks.SOUL_SOIL.getDefaultState());
        DIMENSION_BLOCKS.put("time", Blocks.OBSIDIAN.getDefaultState());
        DIMENSION_BLOCKS.put("space", Blocks.END_STONE.getDefaultState());
        DIMENSION_BLOCKS.put("reality", Blocks.RED_TERRACOTTA.getDefaultState());
        DIMENSION_BLOCKS.put("power", Blocks.MAGMA_BLOCK.getDefaultState());
        DIMENSION_BLOCKS.put("mind", Blocks.YELLOW_CONCRETE.getDefaultState());
    }

    public StoneDimensionChunkGenerator(BiomeProvider biomeProvider, long seed, String dimensionType) {
        super(biomeProvider, new DimensionStructuresSettings(false));
        this.seed = seed;
        this.dimensionType = dimensionType;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getChunkGeneratorCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator createChunkGenerator() {
        return new StoneDimensionChunkGenerator(this.getBiomeProvider(), this.seed, this.dimensionType);
    }

    @Override
    public void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
        // No noise, we'll use a flat or custom terrain pattern
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return 64; // Default height
    }

    @Override
    public void generateSurface(WorldGenRegion region, IChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        
        BlockState defaultBlock = DIMENSION_BLOCKS.getOrDefault(
                dimensionType, Blocks.STONE.getDefaultState());
                
        // Generate terrain based on dimension type
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = chunkX * 16 + x;
                int realZ = chunkZ * 16 + z;
                
                // Base height for the terrain
                int height = 60;
                
                // Add some variation based on dimension type
                if (dimensionType.equals("time")) {
                    // Time dimension has floating islands
                    double noise = Math.sin(realX * 0.05) * Math.cos(realZ * 0.05) * 15;
                    height += (int) noise;
                    
                    // Some floating islands
                    if (Math.abs(noise) > 8 && Math.random() > 0.7) {
                        height += 20 + (int)(Math.random() * 10);
                    }
                } else if (dimensionType.equals("space")) {
                    // Space dimension is sparse with floating platforms
                    if (Math.random() > 0.9) {
                        height += 20 + (int)(Math.random() * 20);
                    } else {
                        height = 40; // Lower base height
                    }
                } else if (dimensionType.equals("soul")) {
                    // Soul dimension has a rolling landscape
                    height += (int)(Math.sin(realX * 0.03) * Math.cos(realZ * 0.03) * 10);
                } else if (dimensionType.equals("reality")) {
                    // Reality dimension has dramatic terrain
                    height += (int)(Math.sin(realX * 0.02) * Math.sin(realZ * 0.02) * 20);
                } else if (dimensionType.equals("power")) {
                    // Power dimension has mountains and valleys
                    height += (int)(Math.max(0, Math.sin(realX * 0.04) * Math.sin(realZ * 0.04) * 25));
                } else if (dimensionType.equals("mind")) {
                    // Mind dimension has gentle rolling hills
                    height += (int)(Math.sin(realX * 0.01) * Math.cos(realZ * 0.01) * 8);
                }
                
                // Set blocks from bedrock to height
                for (int y = 0; y < height; y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), defaultBlock, false);
                }
                
                // Set bedrock at the bottom
                chunk.setBlockState(new BlockPos(x, 0, z), Blocks.BEDROCK.getDefaultState(), false);
            }
        }
    }

    @Override
    public void buildSurface(WorldGenRegion region, IChunk chunk) {
        // We generate our surface in generateSurface
    }

    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        return EMPTY_BLOCK_READER;
    }
    
    @Override
    public void func_230352_b_(IWorld world, StructureManager structureManager, IChunk chunk) {
        // Generate basic terrain
        generateSurface((WorldGenRegion) world, chunk);
    }
    
    @Override
    public void func_230350_a_(long seed, BiomeManager biomeManager, IChunk chunk, 
                              net.minecraft.world.gen.GenerationStage.Carving carving) {
        // No carving in these dimensions by default
    }
}