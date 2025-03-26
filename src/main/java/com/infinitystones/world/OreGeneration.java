package com.infinitystones.world;

import com.infinitystones.config.ModConfig;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class OreGeneration {

    /**
     * Sets up ore generation for the infinity stones
     */
    public static void setupOreGeneration() {
        // Register the event handler for biome loading
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(OreGeneration.class);
    }
    
    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        // Only spawn in certain biomes to make them harder to find
        if (shouldGenerateInBiome(event.getCategory())) {
            addStoneOreGeneration(event.getGeneration());
        }
    }
    
    private static boolean shouldGenerateInBiome(Biome.Category category) {
        // Make stones more rare by only spawning in specific biomes
        switch (category) {
            case NETHER:
                return true; // Power Stone and Reality Stone in Nether
            case THE_END:
                return true; // Space Stone and Mind Stone in End
            case EXTREME_HILLS:
            case MESA:
            case DESERT:
                return true; // Time Stone in these overworld biomes
            case SWAMP:
            case TAIGA:
                return true; // Soul Stone in these overworld biomes
            default:
                return false;
        }
    }
    
    private static void addStoneOreGeneration(BiomeGenerationSettingsBuilder settings) {
        // Get the chance of generation
        double chance = ModConfig.COMMON_CONFIG.stoneSpawnChance.get();
        
        // Scale the frequency based on the config chance (1% default)
        int frequency = (int) Math.max(1, Math.round(chance * 100));
        
        // Add all the different stone types based on biome
        RuleTest netherRule = OreFeatureConfig.FillerBlockType.NETHERRACK;
        RuleTest endRule = new BlockMatchRuleTest(Blocks.END_STONE);
        RuleTest stoneRule = OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD;
        
        // Space Stone generation in End
        settings.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(endRule, 
                                Blocks.DIAMOND_ORE.getDefaultState(), 1)) // Just 1 block per vein
                .withPlacement(
                        Placement.RANGE.configure(
                                new TopSolidRangeConfig(5, 0, 50)))
                .chance(200) // Very rare
        );
        
        // Mind Stone generation in End islands
        settings.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(endRule, 
                                Blocks.EMERALD_ORE.getDefaultState(), 1))
                .withPlacement(
                        Placement.RANGE.configure(
                                new TopSolidRangeConfig(20, 10, 60)))
                .chance(200)
        );
        
        // Reality Stone generation in Nether
        settings.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(netherRule, 
                                Blocks.REDSTONE_ORE.getDefaultState(), 1))
                .withPlacement(
                        Placement.RANGE.configure(
                                new TopSolidRangeConfig(10, 5, 40)))
                .chance(200)
        );
        
        // Power Stone generation in Nether
        settings.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(netherRule, 
                                Blocks.GOLD_ORE.getDefaultState(), 1))
                .withPlacement(
                        Placement.RANGE.configure(
                                new TopSolidRangeConfig(15, 5, 30)))
                .chance(200)
        );
        
        // Time Stone generation in Overworld
        settings.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(stoneRule, 
                                Blocks.LAPIS_ORE.getDefaultState(), 1))
                .withPlacement(
                        Placement.RANGE.configure(
                                new TopSolidRangeConfig(ModConfig.COMMON_CONFIG.stoneSpawnY.get(), 5, 30)))
                .chance(200)
        );
        
        // Soul Stone generation in Overworld
        settings.withFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE.withConfiguration(
                        new OreFeatureConfig(stoneRule, 
                                Blocks.GOLD_ORE.getDefaultState(), 1))
                .withPlacement(
                        Placement.RANGE.configure(
                                new TopSolidRangeConfig(ModConfig.COMMON_CONFIG.stoneSpawnY.get(), 5, 20)))
                .chance(200)
        );
    }
}
