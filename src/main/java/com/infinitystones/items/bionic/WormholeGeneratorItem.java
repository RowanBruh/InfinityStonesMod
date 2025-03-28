package com.infinitystones.items.bionic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.particles.ParticleTypes;

public class WormholeGeneratorItem extends Item {
    
    private static final int MAX_DISTANCE = 64;
    private static final int COOLDOWN_TICKS = 100; // 5 seconds
    
    public WormholeGeneratorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Cast a ray from the player's view
        Vector3d eyePosition = player.getEyePosition(1.0F);
        Vector3d lookVector = player.getLook(1.0F);
        Vector3d endPos = eyePosition.add(lookVector.x * MAX_DISTANCE, lookVector.y * MAX_DISTANCE, lookVector.z * MAX_DISTANCE);
        
        RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(
                eyePosition, 
                endPos, 
                RayTraceContext.BlockMode.COLLIDER, 
                RayTraceContext.FluidMode.NONE, 
                player));
        
        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = new BlockPos(rayTraceResult.getHitVec());
            
            // Find a safe teleport position above the hit block
            BlockPos teleportPos = findSafeTeleportLocation(world, hitPos);
            
            if (teleportPos != null && !world.isRemote) {
                // Teleport player to the target location
                player.setPositionAndUpdate(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5);
                
                // Play teleportation sound
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                
                // Add portal particles at start and end locations
                spawnWormholeParticles(world, player.getPosition(), teleportPos);
                
                // Set cooldown
                player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            }
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    private BlockPos findSafeTeleportLocation(World world, BlockPos hitPos) {
        // First try the position two blocks above the hit block
        BlockPos targetPos = hitPos.up(2);
        
        // Check if we have 2 blocks of space and a solid block below
        if (isLocationSafe(world, targetPos)) {
            return targetPos;
        }
        
        // If not, search nearby for a safe spot
        for (int y = 1; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = hitPos.add(x, y, z);
                    if (isLocationSafe(world, pos)) {
                        return pos;
                    }
                }
            }
        }
        
        return null;
    }
    
    private boolean isLocationSafe(World world, BlockPos pos) {
        return world.isAirBlock(pos) && 
               world.isAirBlock(pos.up()) && 
               !world.isAirBlock(pos.down());
    }
    
    private void spawnWormholeParticles(World world, BlockPos startPos, BlockPos endPos) {
        for (int i = 0; i < 30; i++) {
            double offsetX = world.rand.nextGaussian() * 0.5;
            double offsetY = world.rand.nextGaussian() * 0.5;
            double offsetZ = world.rand.nextGaussian() * 0.5;
            
            // Particles at start position
            world.addParticle(
                    ParticleTypes.PORTAL, 
                    startPos.getX() + 0.5 + offsetX, 
                    startPos.getY() + 1.0 + offsetY, 
                    startPos.getZ() + 0.5 + offsetZ, 
                    0, 0, 0);
            
            // Particles at end position
            world.addParticle(
                    ParticleTypes.PORTAL, 
                    endPos.getX() + 0.5 + offsetX, 
                    endPos.getY() + 0.5 + offsetY, 
                    endPos.getZ() + 0.5 + offsetZ, 
                    0, 0, 0);
        }
    }
}