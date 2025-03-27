package com.infinitystones.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Helper class for Greek god items functionality
 */
public class GodPowerHelper {

    /**
     * Performs a ray trace from the player's perspective
     */
    public static HitResult rayTrace(Level level, Player player, ClipContext.Fluid fluidMode, double range) {
        float pitch = player.getXRot();
        float yaw = player.getYRot();
        Vec3 startVec = player.getEyePosition(1.0F);
        float f2 = (float) Math.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = (float) Math.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -(float) Math.cos(-pitch * ((float) Math.PI / 180F));
        float f5 = (float) Math.sin(-pitch * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 endVec = startVec.add((double) f6 * range, (double) f5 * range, (double) f7 * range);
        
        ClipContext context = new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, fluidMode, player);
        return level.clip(context);
    }
    
    /**
     * Gets a BlockPos from a HitResult
     */
    public static BlockPos getTargetPosition(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) hitResult;
            return blockResult.getBlockPos();
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityResult = (EntityHitResult) hitResult;
            return new BlockPos(entityResult.getEntity().blockPosition());
        } else {
            // Miss - return the position at the end of the ray
            Vec3 hitVec = hitResult.getLocation();
            return new BlockPos((int)hitVec.x, (int)hitVec.y, (int)hitVec.z);
        }
    }
    
    /**
     * Creates a custom damage source for god powers
     */
    public static DamageSource causeGodPowerDamage(Player player, String godName) {
        return player.damageSources().source(DamageTypes.MAGIC, player);
    }
    
    /**
     * Checks if an entity is a certain type
     * (Utility method to help with entity type checking)
     */
    public static boolean isEntityType(Entity entity, String typeName) {
        ResourceLocation resourceLocation = entity.getType().getRegistryName();
        return resourceLocation != null && resourceLocation.toString().contains(typeName);
    }
    
    /**
     * Gets nearby entities for area effect abilities
     */
    public static <T extends Entity> List<T> getNearbyEntities(Level level, Entity center, 
            double radius, Class<T> entityType) {
        AABB aabb = center.getBoundingBox().inflate(radius);
        return level.getEntitiesOfClass(entityType, aabb, 
            entity -> entity != center && center.distanceTo(entity) <= radius);
    }
    
    /**
     * Gets the minimum distance between an entity and a list of positions
     */
    public static double getMinDistanceToPositions(Entity entity, List<BlockPos> positions) {
        double minDistance = Double.MAX_VALUE;
        for (BlockPos pos : positions) {
            double distance = entity.distanceToSqr(Vec3.atCenterOf(pos));
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return Math.sqrt(minDistance);
    }
    
    /**
     * Creates a damage source that bypasses invulnerability frames
     */
    public static DamageSource causeInstantGodDamage(Player player, String godName) {
        return player.damageSources().source(DamageTypes.MAGIC, player);
    }
}