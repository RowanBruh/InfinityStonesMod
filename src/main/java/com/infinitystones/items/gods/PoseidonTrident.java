package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class PoseidonTrident extends Item {
    
    private static final int COOLDOWN_TICKS = 60; // 3 seconds cooldown
    private static final int WATER_WAVE_RANGE = 8; // Range for water wave attack
    private static final int WATER_BREATHING_DURATION = 600; // 30 seconds of water breathing

    public PoseidonTrident(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        // Check if player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultPass(itemStack);
        }

        // Different ability based on sneak state
        if (player.isSneaking()) {
            // Water breathing ability when sneaking
            if (!world.isRemote) {
                // Apply water breathing effect
                player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, WATER_BREATHING_DURATION, 0));
                
                // Also apply slow falling for swimming up
                player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, WATER_BREATHING_DURATION, 0));
                
                // Add dolphin's grace for faster swimming
                player.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, WATER_BREATHING_DURATION, 1));
                
                // Play sound and particles
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_DOLPHIN_SPLASH, SoundCategory.PLAYERS, 1.0F, 1.0F);
                
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                        ParticleTypes.DOLPHIN, 
                        player.getPosX(), player.getPosY() + 1, player.getPosZ(), 
                        20, 0.5, 0.5, 0.5, 0.1);
                }
                
                // Damage the item less for this ability
                if (!player.abilities.isCreativeMode) {
                    itemStack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
                }
            }
        } else {
            // Water wave attack when not sneaking
            // Add cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            // Calculate direction
            RayTraceResult rayTrace = GodPowerHelper.rayTrace(world, player, RayTraceContext.FluidMode.ANY, 30);
            
            if (!world.isRemote) {
                // Create water wave effect
                createWaterWave(world, player, rayTrace);
                
                // Damage the item
                if (!player.abilities.isCreativeMode) {
                    itemStack.damageItem(2, player, (p) -> p.sendBreakAnimation(hand));
                }
            }
        }

        // Add some visual and sound effects
        player.swingArm(hand);
        
        return ActionResult.resultConsume(itemStack);
    }
    
    private void createWaterWave(World world, PlayerEntity player, RayTraceResult rayTrace) {
        // Get the target position from the ray trace
        BlockPos targetPos = GodPowerHelper.getTargetPosition(rayTrace);
        
        // Create a water wave that pushes entities away and deals damage
        AxisAlignedBB affectedArea = new AxisAlignedBB(
            targetPos.getX() - WATER_WAVE_RANGE, targetPos.getY() - 2, targetPos.getZ() - WATER_WAVE_RANGE,
            targetPos.getX() + WATER_WAVE_RANGE, targetPos.getY() + 4, targetPos.getZ() + WATER_WAVE_RANGE
        );
        
        // Get all living entities in the affected area
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, affectedArea, 
            entity -> entity != player && !entity.isSpectator());
        
        if (!entities.isEmpty()) {
            // For each entity, apply knockback and damage
            for (LivingEntity entity : entities) {
                // Calculate direction to push the entity
                double dx = entity.getPosX() - targetPos.getX();
                double dz = entity.getPosZ() - targetPos.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                
                // Skip if too far
                if (distance > WATER_WAVE_RANGE) continue;
                
                // Normalize the direction
                if (distance > 0) {
                    dx /= distance;
                    dz /= distance;
                }
                
                // The closer to the center, the stronger the push
                double strength = 2.0 * (1.0 - distance / WATER_WAVE_RANGE);
                
                // Apply knockback
                entity.setMotion(entity.getMotion().add(dx * strength, 0.5 * strength, dz * strength));
                entity.velocityChanged = true;
                
                // Deal water damage (more to fire-based mobs, less to water-based)
                float damage = 6.0f;
                
                // Water creatures take less damage
                if (entity.isImmuneToFire()) {
                    damage = 10.0f;  // More damage to fire creatures
                } else if (entity.canSwim()) {
                    damage = 2.0f;   // Less damage to water creatures
                }
                
                entity.attackEntityFrom(GodPowerHelper.causeGodPowerDamage(player, "poseidon"), damage);
            }
        }
        
        // Spawn particles for the water wave
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Create a circle of particles
            for (int i = 0; i < 360; i += 10) {
                for (int r = 1; r <= WATER_WAVE_RANGE; r += 2) {
                    double angle = Math.toRadians(i);
                    double particleX = targetPos.getX() + r * Math.cos(angle);
                    double particleZ = targetPos.getZ() + r * Math.sin(angle);
                    
                    serverWorld.spawnParticle(
                        ParticleTypes.SPLASH,
                        particleX, targetPos.getY() + 0.5, particleZ,
                        3, 0.2, 0.2, 0.2, 0.1);
                }
            }
            
            // Play sound effect
            world.playSound(null, targetPos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.PLAYERS, 
                    2.0F, 0.8F);
        }
    }
}