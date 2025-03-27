package com.infinitystones.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Helper class for Greek god items functionality
 */
public class GodPowerHelper {

    /**
     * Performs a ray trace from the player's perspective
     */
    public static RayTraceResult rayTrace(World world, PlayerEntity player, RayTraceContext.FluidMode fluidMode, double range) {
        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;
        Vector3d startVec = player.getEyePosition(1.0F);
        float f2 = (float) Math.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = (float) Math.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -(float) Math.cos(-pitch * ((float) Math.PI / 180F));
        float f5 = (float) Math.sin(-pitch * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vector3d endVec = startVec.add((double) f6 * range, (double) f5 * range, (double) f7 * range);
        
        RayTraceContext context = new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, fluidMode, player);
        return world.rayTraceBlocks(context);
    }
    
    /**
     * Gets a BlockPos from a RayTraceResult
     */
    public static BlockPos getTargetPosition(RayTraceResult rayTraceResult) {
        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockResult = (BlockRayTraceResult) rayTraceResult;
            return blockResult.getPos();
        } else if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityResult = (EntityRayTraceResult) rayTraceResult;
            return entityResult.getEntity().getPosition();
        } else {
            // Miss - return the position at the end of the ray
            Vector3d hitVec = rayTraceResult.getHitVec();
            return new BlockPos(hitVec.x, hitVec.y, hitVec.z);
        }
    }
    
    /**
     * Creates a custom damage source for god powers
     */
    public static DamageSource causeGodPowerDamage(PlayerEntity player, String godName) {
        return new DamageSource("godpower." + godName).setMagicDamage().setDamageBypassesArmor();
    }
    
    /**
     * Checks if an entity is a certain type
     * (Utility method to help with entity type checking)
     */
    public static boolean isEntityType(Entity entity, String typeName) {
        return entity.getType().getRegistryName() != null && 
               entity.getType().getRegistryName().toString().contains(typeName);
    }
}