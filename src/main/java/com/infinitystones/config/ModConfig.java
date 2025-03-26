package com.infinitystones.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ModConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON_CONFIG;
    
    static {
        final ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON_CONFIG = new CommonConfig(commonBuilder);
        COMMON_SPEC = commonBuilder.build();
    }
    
    public static class CommonConfig {
        // General config settings
        public final BooleanValue enableInsaneCraftIntegration;
        
        // Stone appearance settings
        public final DoubleValue stoneSpawnChance;
        public final IntValue stoneSpawnY;
        
        // Stone ability settings
        public final DoubleValue spaceStonePowerRadius;
        public final IntValue mindStoneControlDuration;
        public final DoubleValue realityStonePowerMultiplier;
        public final DoubleValue powerStoneDamageMultiplier;
        public final IntValue timeStoneEffectDuration;
        public final DoubleValue soulStoneLifeStealAmount;
        
        // Gauntlet settings
        public final BooleanValue enableGauntletSnapAbility;
        public final IntValue gauntletSnapCooldown;
        
        // InsaneCraft boss integration
        public final BooleanValue enableBossDrops;
        public final ConfigValue<List<? extends String>> bossNames;
        
        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Infinity Stones Mod Configuration")
                   .push("general");
            
            enableInsaneCraftIntegration = builder
                    .comment("Enable integration with Insane Craft elements")
                    .define("enableInsaneCraftIntegration", true);
            
            builder.pop().push("stone_generation");
            
            stoneSpawnChance = builder
                    .comment("Chance of infinity stone ore spawning (0.0-1.0)")
                    .defineInRange("stoneSpawnChance", 0.02, 0.0, 1.0);
            
            stoneSpawnY = builder
                    .comment("Maximum Y level for stone ore generation")
                    .defineInRange("stoneSpawnY", 16, 1, 255);
            
            builder.pop().push("stone_abilities");
            
            spaceStonePowerRadius = builder
                    .comment("Space Stone teleportation radius in blocks")
                    .defineInRange("spaceStonePowerRadius", 100.0, 10.0, 10000.0);
            
            mindStoneControlDuration = builder
                    .comment("Mind Stone control duration in seconds")
                    .defineInRange("mindStoneControlDuration", 30, 5, 300);
            
            realityStonePowerMultiplier = builder
                    .comment("Reality Stone power multiplier")
                    .defineInRange("realityStonePowerMultiplier", 2.0, 1.0, 10.0);
            
            powerStoneDamageMultiplier = builder
                    .comment("Power Stone damage multiplier")
                    .defineInRange("powerStoneDamageMultiplier", 5.0, 1.0, 50.0);
            
            timeStoneEffectDuration = builder
                    .comment("Time Stone effect duration in seconds")
                    .defineInRange("timeStoneEffectDuration", 60, 10, 600);
            
            soulStoneLifeStealAmount = builder
                    .comment("Soul Stone life steal amount (hearts)")
                    .defineInRange("soulStoneLifeStealAmount", 1.0, 0.5, 10.0);
            
            builder.pop().push("gauntlet");
            
            enableGauntletSnapAbility = builder
                    .comment("Enable the snap ability of the Infinity Gauntlet")
                    .define("enableGauntletSnapAbility", true);
            
            gauntletSnapCooldown = builder
                    .comment("Cooldown for the snap ability (in seconds)")
                    .defineInRange("gauntletSnapCooldown", 300, 60, 3600);
            
            builder.pop().push("insane_craft");
            
            enableBossDrops = builder
                    .comment("Enable Infinity Stone drops from Insane Craft bosses")
                    .define("enableBossDrops", true);
            
            bossNames = builder
                    .comment("List of Insane Craft bosses that can drop Infinity Stones")
                    .defineList("bossNames", 
                            Arrays.asList("CrazyWither", "MegaDragon", "UltimateBoss", "ChaosGuardian"), 
                            obj -> obj instanceof String);
            
            builder.pop();
        }
    }
    
    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        
        configData.load();
        spec.setConfig(configData);
    }
}
