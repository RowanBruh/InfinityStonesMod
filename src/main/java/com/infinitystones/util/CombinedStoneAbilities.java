package com.infinitystones.util;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityStones.StoneType;
import com.infinitystones.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

/**
 * Handles combined abilities when multiple stones are used together in the Advanced Infinity Gauntlet
 */
public class CombinedStoneAbilities {
    private static final Random random = new Random();
    
    /**
     * Executes the combined power of multiple stones in a single effect
     * @param world The world the player is in
     * @param player The player using the combined stones
     * @param stones The set of stones being used together
     * @param cooldown Whether to apply cooldown after use
     */
    public static void executeCombinedPower(World world, PlayerEntity player, Set<StoneType> stones, boolean cooldown) {
        InfinityStonesMod.LOGGER.info("Executing combined power with " + stones.size() + " stones");
        
        // Display info to the player about the combined ability
        player.sendStatusMessage(new StringTextComponent("Combined Infinity Stones power activated!")
                .mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD), true);
        
        // The more stones, the more powerful the effect
        int stonesCount = stones.size();
        
        // Different combinations yield different results
        if (stones.contains(StoneType.POWER) && stones.contains(StoneType.SPACE)) {
            powerSpaceCombination(world, player, stonesCount);
        }
        
        if (stones.contains(StoneType.REALITY) && stones.contains(StoneType.TIME)) {
            realityTimeCombination(world, player, stonesCount);
        }
        
        if (stones.contains(StoneType.MIND) && stones.contains(StoneType.SOUL)) {
            mindSoulCombination(world, player, stonesCount);
        }
        
        // Triple Stone Combinations
        if (stones.contains(StoneType.POWER) && stones.contains(StoneType.SPACE) && stones.contains(StoneType.REALITY)) {
            triggerDestructiveWave(world, player, stonesCount);
        }
        
        if (stones.contains(StoneType.MIND) && stones.contains(StoneType.SOUL) && stones.contains(StoneType.TIME)) {
            triggerTimeFreeze(world, player, stonesCount);
        }
        
        // All stones together create "The Snap" effect
        if (stones.size() >= 6) {
            theSnapEffect(world, player);
        }
        
        // Apply cooldown based on how many stones were used
        if (cooldown) {
            player.getCooldownTracker().setCooldown(ModItems.ADVANCED_INFINITY_GAUNTLET.get(), 20 * 30); // 30 second cooldown
        }
    }
    
    /**
     * Power + Space: Enhanced gravity manipulation and energy projection
     */
    private static void powerSpaceCombination(World world, PlayerEntity player, int stonesCount) {
        double radius = 5.0 + (stonesCount * 2); // Radius increases with more stones
        
        // Get entities in range
        List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                new AxisAlignedBB(
                    player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        // Apply effects to nearby entities
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity) {
                // Calculate direction vector away from player
                Vector3d pushVector = entity.getPositionVec().subtract(player.getPositionVec()).normalize();
                
                // Scale based on stone count (more stones = stronger effect)
                double forceFactor = 2.0 + (stonesCount * 0.5);
                
                // Apply motion
                entity.setMotion(pushVector.x * forceFactor, 1.5, pushVector.z * forceFactor);
                entity.velocityChanged = true;
                
                // Apply damage (optional)
                if (stonesCount >= 3) {
                    entity.attackEntityFrom(DamageSource.MAGIC, stonesCount * 2);
                }
                
                // Visual effect
                if (world.isRemote) {
                    for (int i = 0; i < 10; i++) {
                        world.addParticle(
                            net.minecraft.particles.ParticleTypes.PORTAL, 
                            entity.getPosX() + (random.nextDouble() - 0.5), 
                            entity.getPosY() + random.nextDouble() * 2.0, 
                            entity.getPosZ() + (random.nextDouble() - 0.5),
                            0, 0, 0);
                    }
                }
            }
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 0.8F);
    }
    
    /**
     * Reality + Time: Manipulate environment and slow time for enemies
     */
    private static void realityTimeCombination(World world, PlayerEntity player, int stonesCount) {
        double radius = 20.0;
        int duration = 200 + (stonesCount * 100); // Longer effect with more stones
        
        // Apply slowness to nearby entities
        List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                new AxisAlignedBB(
                    player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                LivingEntity livingEntity = (LivingEntity) entity;
                livingEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, stonesCount));
                
                // Additional effects based on stone count
                if (stonesCount >= 4) {
                    livingEntity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, duration, stonesCount - 2));
                }
            }
        }
        
        // Apply speed and night vision to player
        player.addPotionEffect(new EffectInstance(Effects.SPEED, duration, 1));
        player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, duration, 0));
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0F, 2.0F);
        
        // Message to player
        player.sendStatusMessage(new StringTextComponent("Reality and time bend to your will!").mergeStyle(TextFormatting.LIGHT_PURPLE), true);
    }
    
    /**
     * Mind + Soul: Control minds and manipulate life force
     */
    private static void mindSoulCombination(World world, PlayerEntity player, int stonesCount) {
        double radius = 10.0 + (stonesCount * 3);
        int duration = 300 + (stonesCount * 100); // Longer effect with more stones
        
        // Heal the player
        player.heal(stonesCount * 4);
        
        // Apply effects to nearby entities
        List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                new AxisAlignedBB(
                    player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                LivingEntity livingEntity = (LivingEntity) entity;
                
                // Pacify aggressive mobs
                livingEntity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, duration, 2));
                
                // Confuse them
                if (random.nextFloat() < 0.5) {
                    livingEntity.addPotionEffect(new EffectInstance(Effects.BLINDNESS, duration / 2, 0));
                    livingEntity.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, duration, 1));
                }
                
                // If more stones are combined, chance to heal friendly mobs
                if (stonesCount >= 4 && !(livingEntity.isHostileMob())) {
                    livingEntity.heal(stonesCount);
                }
            }
        }
        
        // Apply beneficial effects to player
        player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, duration, stonesCount - 1));
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, duration / 2, 1));
        
        // Visual effect - soul particles around player
        if (world.isRemote) {
            for (int i = 0; i < 50; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double distance = random.nextDouble() * 2;
                
                world.addParticle(
                    net.minecraft.particles.ParticleTypes.SOUL, 
                    player.getPosX() + Math.cos(angle) * distance, 
                    player.getPosY() + random.nextDouble() * 2, 
                    player.getPosZ() + Math.sin(angle) * distance,
                    0, 0.1, 0);
            }
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 0.5F);
    }
    
    /**
     * Power + Space + Reality: Destructive wave that transforms and damages
     */
    private static void triggerDestructiveWave(World world, PlayerEntity player, int stonesCount) {
        double radius = 8.0 + (stonesCount * 3);
        float damage = 6.0f + (stonesCount * 2);
        
        // Apply damage to entities and transform some blocks
        List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                new AxisAlignedBB(
                    player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        // Damage entities and push them away
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity) {
                // Apply damage
                entity.attackEntityFrom(DamageSource.MAGIC, damage);
                
                // Calculate direction vector away from player
                Vector3d pushVector = entity.getPositionVec().subtract(player.getPositionVec()).normalize();
                
                // Apply strong knockback
                double knockbackFactor = 2.0 + stonesCount;
                entity.setMotion(pushVector.x * knockbackFactor, 1.0, pushVector.z * knockbackFactor);
                entity.velocityChanged = true;
            }
        }
        
        // Visual explosion effect
        if (!world.isRemote) {
            ((ServerWorld) world).spawnParticle(
                net.minecraft.particles.ParticleTypes.EXPLOSION, 
                player.getPosX(), player.getPosY(), player.getPosZ(), 
                20, // particle count
                radius / 2, 2, radius / 2, // spread
                0.1D // speed
            );
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 0.5F);
        
        player.sendStatusMessage(new StringTextComponent("Destructive energy radiates outward!").mergeStyle(TextFormatting.RED, TextFormatting.BOLD), true);
    }
    
    /**
     * Mind + Soul + Time: Freezes time for all entities except the player
     */
    private static void triggerTimeFreeze(World world, PlayerEntity player, int stonesCount) {
        double radius = 30.0 + (stonesCount * 5);
        int duration = 200 + (stonesCount * 60); // 10-20 seconds based on stone count
        int amplifier = Math.min(4, stonesCount - 1); // Cap at 4
        
        // Apply extreme slowness to all nearby entities
        List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                new AxisAlignedBB(
                    player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                LivingEntity livingEntity = (LivingEntity) entity;
                
                // Apply extreme slowness (essentially freezing them)
                livingEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, amplifier));
                livingEntity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, duration, amplifier));
                livingEntity.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, duration, amplifier));
            }
        }
        
        // Apply beneficial effects to player
        player.addPotionEffect(new EffectInstance(Effects.SPEED, duration, 1));
        player.addPotionEffect(new EffectInstance(Effects.HASTE, duration, 1));
        
        // Visual effect - clock particles spiraling outward
        if (!world.isRemote) {
            for (int i = 0; i < 5; i++) {
                double angle = (i / 5.0) * Math.PI * 2;
                for (double dist = 1; dist < radius; dist += 1.5) {
                    double x = player.getPosX() + Math.cos(angle) * dist;
                    double z = player.getPosZ() + Math.sin(angle) * dist;
                    
                    ((ServerWorld) world).spawnParticle(
                        net.minecraft.particles.ParticleTypes.REVERSE_PORTAL, 
                        x, player.getPosY() + 1, z, 
                        1, 0, 0, 0, 0.01
                    );
                }
            }
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 1.0F, 0.5F);
        
        player.sendStatusMessage(new StringTextComponent("Time stands still around you!").mergeStyle(TextFormatting.AQUA, TextFormatting.BOLD), true);
    }
    
    /**
     * The legendary "Snap" effect when all six stones are used together
     */
    private static void theSnapEffect(World world, PlayerEntity player) {
        double radius = 50.0; // Large effect radius
        
        player.sendStatusMessage(new StringTextComponent("\"With all six stones, I can simply snap my fingers...\"")
                .mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD), false);
        
        // Sound effect for the snap
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_IRON_GOLEM_HURT, SoundCategory.PLAYERS, 1.0F, 2.0F);
        
        // Delay the effect for dramatic impact
        if (!world.isRemote) {
            // Apply effects to nearby entities
            List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                    new AxisAlignedBB(
                        player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                        player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            int count = 0;
            List<Entity> affectedEntities = new ArrayList<>();
            
            // Calculate which entities are affected (roughly half)
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity) && random.nextBoolean()) {
                    affectedEntities.add(entity);
                    count++;
                }
            }
            
            // Process the "dusting" effect
            for (Entity entity : affectedEntities) {
                // Apply effects to create a "dusting" visual
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    
                    // Apply damage that will likely kill the entity
                    livingEntity.attackEntityFrom(DamageSource.MAGIC, 1000);
                    
                    // Spawn dust particles where the entity was
                    BlockPos pos = entity.getPosition();
                    ((ServerWorld) world).spawnParticle(
                        net.minecraft.particles.ParticleTypes.ASH, 
                        pos.getX(), pos.getY() + entity.getHeight() / 2, pos.getZ(), 
                        20, // count
                        entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth() / 2, // spread
                        0.05 // speed
                    );
                }
            }
            
            // Final message
            player.sendStatusMessage(new StringTextComponent(count + " beings turned to dust...")
                    .mergeStyle(TextFormatting.DARK_GRAY, TextFormatting.ITALIC), false);
            
            // Make the player pay a price for such power
            // Drain health to a minimum of 1 heart
            float newHealth = Math.max(2.0F, player.getHealth() - 10.0F);
            player.setHealth(newHealth);
            
            // Apply debuffs
            player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 1200, 1)); // 1 minute of weakness
            player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 600, 0)); // 30 seconds of slowness
            
            // Visual feedback of the toll on the user
            player.sendStatusMessage(new StringTextComponent("The power of the stones takes its toll...")
                    .mergeStyle(TextFormatting.DARK_RED), true);
        }
    }
}