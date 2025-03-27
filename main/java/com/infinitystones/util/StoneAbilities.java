package com.infinitystones.util;

import com.infinitystones.items.StoneType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

/**
 * Utility class for Infinity Stone abilities
 */
public class StoneAbilities {
    
    /**
     * Activates the specific stone ability for a player
     *
     * @param type The type of stone
     * @param player The player
     * @param world The world
     */
    public static void activateStoneAbility(StoneType type, PlayerEntity player, World world) {
        switch (type) {
            case SPACE:
                activateSpaceStoneAbility(player, world);
                break;
            case MIND:
                activateMindStoneAbility(player, world);
                break;
            case REALITY:
                activateRealityStoneAbility(player, world);
                break;
            case POWER:
                activatePowerStoneAbility(player, world);
                break;
            case TIME:
                activateTimeStoneAbility(player, world);
                break;
            case SOUL:
                activateSoulStoneAbility(player, world);
                break;
        }
    }
    
    /**
     * Activates the Space Stone ability (teleportation)
     */
    private static void activateSpaceStoneAbility(PlayerEntity player, World world) {
        // For demonstration purposes, teleport the player 10 blocks forward
        double lookX = -Math.sin(Math.toRadians(player.rotationYaw));
        double lookZ = Math.cos(Math.toRadians(player.rotationYaw));
        
        double targetX = player.getPosX() + (lookX * 10);
        double targetY = player.getPosY();
        double targetZ = player.getPosZ() + (lookZ * 10);
        
        // Ensure the teleport location is safe
        BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
        if (world.getBlockState(targetPos).isAir() && world.getBlockState(targetPos.up()).isAir()) {
            player.setPositionAndUpdate(targetX, targetY, targetZ);
            player.sendMessage(new StringTextComponent("Space Stone: Teleported!"), player.getUniqueID());
        } else {
            player.sendMessage(new StringTextComponent("Space Stone: Cannot teleport to an unsafe location."), player.getUniqueID());
        }
    }
    
    /**
     * Activates the Mind Stone ability (various mental effects)
     */
    private static void activateMindStoneAbility(PlayerEntity player, World world) {
        // Grant night vision and speed as representation of enhanced mental abilities
        player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 600, 0));
        player.addPotionEffect(new EffectInstance(Effects.SPEED, 600, 1));
        player.sendMessage(new StringTextComponent("Mind Stone: Enhanced mental abilities activated!"), player.getUniqueID());
    }
    
    /**
     * Activates the Reality Stone ability (manipulation of surroundings)
     */
    private static void activateRealityStoneAbility(PlayerEntity player, World world) {
        // For demonstration, add temporary levitation and slow falling effects
        player.addPotionEffect(new EffectInstance(Effects.LEVITATION, 100, 1));
        player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 300, 0));
        player.sendMessage(new StringTextComponent("Reality Stone: Reality manipulation activated!"), player.getUniqueID());
    }
    
    /**
     * Activates the Power Stone ability (strength enhancement)
     */
    private static void activatePowerStoneAbility(PlayerEntity player, World world) {
        // Add strength, resistance and health boost
        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 600, 2));
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 600, 1));
        player.addPotionEffect(new EffectInstance(Effects.HEALTH_BOOST, 600, 1));
        player.sendMessage(new StringTextComponent("Power Stone: Power surge activated!"), player.getUniqueID());
    }
    
    /**
     * Activates the Time Stone ability (time manipulation)
     */
    private static void activateTimeStoneAbility(PlayerEntity player, World world) {
        // Add haste and regeneration effects as representation of time control
        player.addPotionEffect(new EffectInstance(Effects.HASTE, 600, 2));
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, 1));
        player.sendMessage(new StringTextComponent("Time Stone: Time manipulation activated!"), player.getUniqueID());
    }
    
    /**
     * Activates the Soul Stone ability (soul-based powers)
     */
    private static void activateSoulStoneAbility(PlayerEntity player, World world) {
        // Add absorption and fire resistance effects
        player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 600, 2));
        player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 600, 0));
        player.sendMessage(new StringTextComponent("Soul Stone: Soul powers activated!"), player.getUniqueID());
    }
}