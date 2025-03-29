package com.infinitystones.world.structures;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;

public class SkiddzieBaseStructure extends Structure<NoFeatureConfig> {
    
    // Structure resource location
    private static final ResourceLocation STRUCTURE_ID = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "skiddzie_base");

    public SkiddzieBaseStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return SkiddzieBaseStructure.Start::new;
    }

    @Override
    public String getStructureName() {
        return STRUCTURE_ID.toString();
    }

    /**
     * This method controls where structures can spawn
     */
    @Override
    protected boolean func_230363_a_(ChunkGenerator chunkGenerator, 
                                     Biome biome, 
                                     BlockPos pos, 
                                     List<? extends Biome.SpawnListEntry> entitySpawnList) {
        // Allow spawning in forest and plains biomes
        return (biome.getCategory().equals(Biome.Category.FOREST) || 
                biome.getCategory().equals(Biome.Category.PLAINS)) && 
               !biome.getCategory().equals(Biome.Category.NETHER) &&
               !biome.getCategory().equals(Biome.Category.THEEND);
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structure, 
                    int chunkX, 
                    int chunkZ, 
                    MutableBoundingBox boundingBox, 
                    int references, 
                    long seed) {
            super(structure, chunkX, chunkZ, boundingBox, references, seed);
        }

        @Override
        public void func_230364_a_(DynamicRegistries registries, 
                                 ChunkGenerator generator, 
                                 TemplateManager templateManager, 
                                 int chunkX, 
                                 int chunkZ, 
                                 Biome biome, 
                                 NoFeatureConfig config) {
            // Calculate absolute position
            int x = chunkX * 16;
            int z = chunkZ * 16;
            
            // Find surface height at this position
            int y = generator.getHeight(x, z, 
                    net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG);
            
            BlockPos centerPos = new BlockPos(x, y, z);
            
            // Add the base structure piece
            JigsawManager.func_242837_a(
                    registries,
                    new VillageConfig(() -> registries.getRegistry(Registry.JIGSAW_POOL_KEY)
                            .getOrDefault(new ResourceLocation(InfinityStonesMod.MOD_ID, "skiddzie_base/start_pool")),
                            10), // Maximum depth for jigsaw generation
                    AbstractVillagePiece::new,
                    generator,
                    templateManager,
                    centerPos,
                    this.components,
                    this.rand,
                    false,
                    true);
            
            // Adjust height of structure
            this.components.forEach(piece -> piece.offset(0, 1, 0));
            this.components.forEach(piece -> piece.getBoundingBox().minY += 1);
            
            // Update bounding box to contain all components
            this.recalculateStructureSize();
        }
    }
}