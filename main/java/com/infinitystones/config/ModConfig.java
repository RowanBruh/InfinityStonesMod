package com.infinitystones.config;

import com.infinitystones.InfinityStonesMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;

@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final CommonConfig COMMON_CONFIG = new CommonConfig(COMMON_BUILDER);
    public static final ForgeConfigSpec COMMON_SPEC = COMMON_BUILDER.build();
    
    public static class CommonConfig {
        // Infinity Stones configuration
        public final ForgeConfigSpec.IntValue spaceStoneCooldown;
        public final ForgeConfigSpec.IntValue mindStoneCooldown;
        public final ForgeConfigSpec.IntValue realityStoneCooldown;
        public final ForgeConfigSpec.IntValue powerStoneCooldown;
        public final ForgeConfigSpec.IntValue timeStoneCooldown;
        public final ForgeConfigSpec.IntValue soulStoneCooldown;
        
        // InsaneCraft weapons configuration
        public final ForgeConfigSpec.DoubleValue insaneCraftWeaponPower;
        public final ForgeConfigSpec.IntValue royalGuardianSwordDamage;
        public final ForgeConfigSpec.IntValue thorHammerDamage;
        
        // Boss configuration
        public final ForgeConfigSpec.DoubleValue insaneCraftBossHealth;
        public final ForgeConfigSpec.DoubleValue insaneCraftBossDamage;
        
        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Infinity Stones Mod Configuration")
                   .push("infinity_stones");
            
            spaceStoneCooldown = builder
                    .comment("Cooldown in ticks for the Space Stone (20 ticks = 1 second)")
                    .defineInRange("spaceStoneCooldown", 200, 0, 6000);
            
            mindStoneCooldown = builder
                    .comment("Cooldown in ticks for the Mind Stone (20 ticks = 1 second)")
                    .defineInRange("mindStoneCooldown", 300, 0, 6000);
            
            realityStoneCooldown = builder
                    .comment("Cooldown in ticks for the Reality Stone (20 ticks = 1 second)")
                    .defineInRange("realityStoneCooldown", 400, 0, 6000);
            
            powerStoneCooldown = builder
                    .comment("Cooldown in ticks for the Power Stone (20 ticks = 1 second)")
                    .defineInRange("powerStoneCooldown", 300, 0, 6000);
            
            timeStoneCooldown = builder
                    .comment("Cooldown in ticks for the Time Stone (20 ticks = 1 second)")
                    .defineInRange("timeStoneCooldown", 500, 0, 6000);
            
            soulStoneCooldown = builder
                    .comment("Cooldown in ticks for the Soul Stone (20 ticks = 1 second)")
                    .defineInRange("soulStoneCooldown", 400, 0, 6000);
            
            builder.pop();
            
            builder.comment("Insane Craft Weapons Configuration")
                   .push("insane_craft");
            
            insaneCraftWeaponPower = builder
                    .comment("Power multiplier for Insane Craft weapons (higher = more powerful)")
                    .defineInRange("insaneCraftWeaponPower", 3.0, 1.0, 10.0);
            
            royalGuardianSwordDamage = builder
                    .comment("Base damage for the Royal Guardian Sword")
                    .defineInRange("royalGuardianSwordDamage", 25, 1, 100);
            
            thorHammerDamage = builder
                    .comment("Base damage for Thor's Hammer")
                    .defineInRange("thorHammerDamage", 30, 1, 100);
            
            builder.pop();
            
            builder.comment("Insane Craft Boss Configuration")
                   .push("bosses");
            
            insaneCraftBossHealth = builder
                    .comment("Health multiplier for Insane Craft bosses (higher = more health)")
                    .defineInRange("insaneCraftBossHealth", 2.0, 1.0, 10.0);
            
            insaneCraftBossDamage = builder
                    .comment("Damage multiplier for Insane Craft bosses (higher = more damage)")
                    .defineInRange("insaneCraftBossDamage", 1.5, 1.0, 5.0);
            
            builder.pop();
        }
    }
    
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        InfinityStonesMod.LOGGER.info("Loaded Infinity Stones configuration file {}", event.getConfig().getFileName());
    }
}