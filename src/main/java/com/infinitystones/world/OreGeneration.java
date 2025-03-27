package com.infinitystones.world;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles the generation of special ores and structures in the world
 */
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID)
public class OreGeneration {
    
    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        
        // Skip certain biomes
        if (event.getCategory() == Biome.Category.NETHER || event.getCategory() == Biome.Category.THEEND) {
            return;
        }
        
        // Add rare special ore generation - these would be custom blocks that drop the stones
        // In a full implementation, we would define custom ore blocks that could drop the infinity stones
        
        // Add ore generation for Insane Craft materials 
        generation.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                                Blocks.DIAMOND_ORE.getDefaultState(), // This would be a custom block in a full implementation
                                3)) // Vein size
                        .withPlacement(Placement.RANGE.configure(
                                new TopSolidRangeConfig(0, 0, 16))) // Min height, max height
                        .square() // Distribution
                        .count(1)); // Number of veins per chunk
    }
}