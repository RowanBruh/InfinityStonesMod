package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class HadesBident extends Item {
    
    private static final int COOLDOWN_TICKS = 80; // 4 seconds cooldown
    private static final int SOUL_FLAME_RANGE = 6; // Range for soul flame attack
    private static final int UNDEAD_CONTROL_RANGE = 20; // Range for undead control
    
    private final Random random = new Random();

    public HadesBident(Properties properties) {
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
            // Control undead ability when sneaking
            if (!world.isRemote) {
                controlUndead(world, player);
                
                // Damage the item less for this ability
                if (!player.abilities.isCreativeMode) {
                    itemStack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
                }
            }
        } else {
            // Soul flame attack when not sneaking
            // Add cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            // Calculate direction
            RayTraceResult rayTrace = GodPowerHelper.rayTrace(world, player, RayTraceContext.FluidMode.NONE, 20);
            
            if (!world.isRemote) {
                // Create soul flame effect
                createSoulFlame(world, player, rayTrace);
                
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
    
    private void createSoulFlame(World world, PlayerEntity player, RayTraceResult rayTrace) {
        // Get the target position from the ray trace
        BlockPos targetPos = GodPowerHelper.getTargetPosition(rayTrace);
        
        // Create a soul flame that damages entities and sets them on fire
        AxisAlignedBB affectedArea = new AxisAlignedBB(
            targetPos.getX() - SOUL_FLAME_RANGE, targetPos.getY() - 2, targetPos.getZ() - SOUL_FLAME_RANGE,
            targetPos.getX() + SOUL_FLAME_RANGE, targetPos.getY() + 4, targetPos.getZ() + SOUL_FLAME_RANGE
        );
        
        // Get all living entities in the affected area
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, affectedArea, 
            entity -> entity != player && !entity.isSpectator());
        
        if (!entities.isEmpty()) {
            // For each entity, apply damage and soul fire
            for (LivingEntity entity : entities) {
                // Calculate distance
                double dx = entity.getPosX() - targetPos.getX();
                double dz = entity.getPosZ() - targetPos.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                
                // Skip if too far
                if (distance > SOUL_FLAME_RANGE) continue;
                
                // The closer to the center, the more damage
                float damage = 8.0f * (1.0f - (float)(distance / SOUL_FLAME_RANGE));
                
                // Undead take less damage, living take more
                if (entity.isImmuneToFire()) {
                    damage *= 0.5f;  // Less damage to fire-immune creatures
                }
                
                // Apply damage
                entity.attackEntityFrom(GodPowerHelper.causeGodPowerDamage(player, "hades"), damage);
                
                // Set on fire with soul flame effect (longer duration for non-undead)
                boolean isUndead = entity instanceof MonsterEntity && GodPowerHelper.isEntityType(entity, "zombie") || 
                                   GodPowerHelper.isEntityType(entity, "skeleton") || 
                                   GodPowerHelper.isEntityType(entity, "wither");
                
                int fireDuration = isUndead ? 2 : 6;
                entity.setFire(fireDuration);
            }
        }
        
        // Spawn particles for the soul flame
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Create a flame pattern
            for (int i = 0; i < 50; i++) {
                double offsetX = random.nextFloat() * SOUL_FLAME_RANGE * 2 - SOUL_FLAME_RANGE;
                double offsetZ = random.nextFloat() * SOUL_FLAME_RANGE * 2 - SOUL_FLAME_RANGE;
                
                // Ensure the particles are within the circle
                if (offsetX * offsetX + offsetZ * offsetZ <= SOUL_FLAME_RANGE * SOUL_FLAME_RANGE) {
                    serverWorld.spawnParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        targetPos.getX() + offsetX, 
                        targetPos.getY() + random.nextFloat() * 2, 
                        targetPos.getZ() + offsetZ,
                        1, 0, 0.1, 0, 0.01);
                }
            }
            
            // Play sound effect
            world.playSound(null, targetPos, SoundEvents.BLOCK_SOUL_SAND_BREAK, SoundCategory.PLAYERS, 
                    1.0F, 0.5F);
            world.playSound(null, targetPos, SoundEvents.ENTITY_BLAZE_BURN, SoundCategory.PLAYERS, 
                    1.0F, 0.5F);
        }
    }
    
    private void controlUndead(World world, PlayerEntity player) {
        // Find all undead mobs within range
        AxisAlignedBB searchArea = new AxisAlignedBB(
            player.getPosX() - UNDEAD_CONTROL_RANGE, player.getPosY() - 5, player.getPosZ() - UNDEAD_CONTROL_RANGE,
            player.getPosX() + UNDEAD_CONTROL_RANGE, player.getPosY() + 5, player.getPosZ() + UNDEAD_CONTROL_RANGE
        );
        
        // Get all mobs that might be undead
        List<MobEntity> mobs = world.getEntitiesWithinAABB(MobEntity.class, searchArea, 
            entity -> !entity.isSpectator() && isUndead(entity));
        
        if (!mobs.isEmpty()) {
            // For each undead, make them target player's target or nearest hostile
            LivingEntity playerTarget = player.getLastAttackedEntity();
            
            for (MobEntity undead : mobs) {
                if (playerTarget != null && playerTarget.isAlive() && !(isUndead(playerTarget))) {
                    // Make undead attack player's target
                    undead.setAttackTarget(playerTarget);
                } else {
                    // Find a non-undead target nearby
                    List<MobEntity> targets = world.getEntitiesWithinAABB(MobEntity.class, 
                        new AxisAlignedBB(
                            undead.getPosX() - 10, undead.getPosY() - 5, undead.getPosZ() - 10,
                            undead.getPosX() + 10, undead.getPosY() + 5, undead.getPosZ() + 10
                        ),
                        entity -> entity != undead && !isUndead(entity) && entity.isAlive() && (entity instanceof MonsterEntity)
                    );
                    
                    if (!targets.isEmpty()) {
                        // Find closest target
                        MobEntity closestTarget = null;
                        double closestDistance = Double.MAX_VALUE;
                        
                        for (MobEntity target : targets) {
                            double distance = undead.getDistanceSq(target);
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                closestTarget = target;
                            }
                        }
                        
                        if (closestTarget != null) {
                            undead.setAttackTarget(closestTarget);
                        }
                    }
                }
                
                // Visual effect to show control
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                        ParticleTypes.SOUL,
                        undead.getPosX(), undead.getPosY() + undead.getHeight() + 0.5, undead.getPosZ(),
                        10, 0.3, 0.3, 0.3, 0.02);
                }
            }
            
            // Play sound effect
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.PLAYERS, 
                    0.5F, 0.8F);
        }
    }
    
    private boolean isUndead(Entity entity) {
        return GodPowerHelper.isEntityType(entity, "zombie") || 
               GodPowerHelper.isEntityType(entity, "skeleton") || 
               GodPowerHelper.isEntityType(entity, "wither") ||
               GodPowerHelper.isEntityType(entity, "phantom") ||
               GodPowerHelper.isEntityType(entity, "drowned") ||
               GodPowerHelper.isEntityType(entity, "stray") ||
               GodPowerHelper.isEntityType(entity, "husk");
    }
}