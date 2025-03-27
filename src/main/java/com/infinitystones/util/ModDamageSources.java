package com.infinitystones.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Custom damage sources for Infinity Stones mod
 */
public class ModDamageSources {
    // Registry keys for our custom damage types
    public static final RegistryKey<DamageType> NANO_TECH_INFECTION_KEY = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE, 
            new Identifier("infinitystones", "nano_tech_infection"));
    
    /**
     * Creates a damage source for Nano Tech infection
     */
    public static DamageSource nanoTechInfection(World world) {
        return world.getDamageSources().create(NANO_TECH_INFECTION_KEY);
    }
    
    // Add more custom damage sources as needed for the mod
}