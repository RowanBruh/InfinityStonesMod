package com.infinitystones.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

/**
 * Utility class for combined Infinity Stone abilities
 */
public class CombinedStoneAbilities {
    
    /**
     * Activates all stone abilities at once in combined mode
     *
     * @param player The player
     * @param world The world
     */
    public static void activateCombinedAbilities(PlayerEntity player, World world) {
        // Add an extremely powerful set of effects to represent the combined power of all stones
        
        // Movement and vision enhancements (Space + Mind)
        player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 1200, 0));
        player.addPotionEffect(new EffectInstance(Effects.SPEED, 1200, 2));
        player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 1200, 1));
        
        // Strength enhancements (Power + Reality)
        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 1200, 3));
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 1200, 2));
        player.addPotionEffect(new EffectInstance(Effects.HEALTH_BOOST, 1200, 2));
        
        // Regeneration and protection (Time + Soul)
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 1200, 2));
        player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 1200, 3));
        player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 1200, 0));
        player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 1200, 0));
        
        // Let the player know all powers are active
        player.sendMessage(new StringTextComponent("§6§lALL INFINITY STONES: §r§eUltimate power activated!"), player.getUniqueID());
    }
    
    /**
     * Activates the "snap" ability (for demonstration purposes)
     *
     * @param player The player
     * @param world The world
     */
    public static void activateSnapAbility(PlayerEntity player, World world) {
        // In a complete implementation, this would affect all entities within a large radius
        // For demonstration, we just add some special effects to the player
        
        // Add special effects
        player.addPotionEffect(new EffectInstance(Effects.GLOWING, 600, 0));
        player.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 200, 0));
        
        // Show message
        player.sendMessage(new StringTextComponent("§6§lSNAP! §r§eThe power of all Infinity Stones has been unleashed!"), 
                player.getUniqueID());
        
        // The actual snap functionality would be implemented here in a full mod
        // This could include damaging or removing half of all entities in the world
    }
}