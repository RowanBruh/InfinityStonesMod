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
 * Handles the generation of Insane Craft materials in the world
 * Note: Infinity Stones are crafted items and do not spawn naturally
 */
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID)
public class OreGeneration {
    
    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        
        // Add materials for different dimensions
        if (event.getCategory() == Biome.Category.NETHER) {
            addNetherMaterials(generation);
        } else if (event.getCategory() == Biome.Category.THEEND) {
            addEndMaterials(generation);
        } else {
            addOverworldMaterials(generation);
        }
    }
    
    /**
     * Add Insane Craft materials to the Overworld
     */
    private static void addOverworldMaterials(BiomeGenerationSettingsBuilder generation) {
        // Rare Cosmic Ore - Drops Cosmic Dust
        generation.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                                Blocks.DIAMOND_ORE.getDefaultState(), // Stand-in for Cosmic Ore 
                                4)) // Vein size
                        .withPlacement(Placement.RANGE.configure(
                                new TopSolidRangeConfig(5, 10, 20))) // Min, middle, max height
                        .square() // Distribution
                        .count(2)); // Number of veins per chunk
        
        // Ultra-rare Enchanted Ore - Drops materials for Enchanted Metal
        generation.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                                Blocks.EMERALD_ORE.getDefaultState(), // Stand-in for Enchanted Ore
                                1)) // Vein size (single block)
                        .withPlacement(Placement.RANGE.configure(
                                new TopSolidRangeConfig(5, 5, 15))) // Very limited height range
                        .square() // Distribution
                        .count(1)); // Very rare - only 1 per chunk at most
    }
    
    /**
     * Add Insane Craft materials to the Nether
     */
    private static void addNetherMaterials(BiomeGenerationSettingsBuilder generation) {
        // Chaos Material - for Chaos Shards
        generation.withFeature(
                GenerationStage.Decoration.UNDERGROUND_DECORATION,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.NETHERRACK,
                                Blocks.ANCIENT_DEBRIS.getDefaultState(), // Stand-in for Chaos Material
                                3)) // Vein size
                        .withPlacement(Placement.RANGE.configure(
                                new TopSolidRangeConfig(10, 35, 120))) 
                        .square()
                        .count(2));
    }
    
    /**
     * Add Insane Craft materials to The End
     */
    private static void addEndMaterials(BiomeGenerationSettingsBuilder generation) {
        // Cosmic Pearl deposits - more common in End
        generation.withFeature(
                GenerationStage.Decoration.UNDERGROUND_DECORATION,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                                Blocks.PURPUR_BLOCK.getDefaultState(), // Stand-in for Cosmic Pearl deposits
                                2)) // Vein size
                        .withPlacement(Placement.RANGE.configure(
                                new TopSolidRangeConfig(10, 40, 80)))
                        .square()
                        .count(3));
    }
}