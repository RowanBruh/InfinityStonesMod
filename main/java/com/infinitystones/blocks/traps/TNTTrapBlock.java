package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTTrapBlock extends Block {
    public TNTTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            // When an entity walks on this block, spawn TNT in a ring
            spawnTNT(worldIn, pos);
            
            // Remove the trap block after activation
            worldIn.removeBlock(pos, false);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    private void spawnTNT(World world, BlockPos triggerPos) {
        // Play TNT sound
        world.playSound(null, triggerPos, SoundEvents.ENTITY_TNT_PRIMED, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Create a ring of TNT around the trap
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // Only place TNT in a ring pattern
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    BlockPos tntPos = new BlockPos(
                        triggerPos.getX() + x,
                        triggerPos.getY(),
                        triggerPos.getZ() + z
                    );
                    
                    // Check if we can spawn TNT here
                    if (world.isAirBlock(tntPos) || world.getBlockState(tntPos).getMaterial().isReplaceable()) {
                        // Create a primed TNT entity with random fuse time
                        TNTEntity tnt = new TNTEntity(
                            world,
                            tntPos.getX() + 0.5D,
                            tntPos.getY(),
                            tntPos.getZ() + 0.5D,
                            null);
                        
                        // Set a short random fuse
                        tnt.setFuse(10 + world.rand.nextInt(30));
                        
                        // Add the TNT to the world
                        world.addEntity(tnt);
                    }
                }
            }
        }
        
        // Also spawn some TNT inside the circle with very short fuses
        for (int i = 0; i < 3; i++) {
            int x = world.rand.nextInt(3) - 1;
            int z = world.rand.nextInt(3) - 1;
            
            BlockPos innerTNTPos = new BlockPos(
                triggerPos.getX() + x,
                triggerPos.getY(),
                triggerPos.getZ() + z
            );
            
            TNTEntity innerTNT = new TNTEntity(
                world,
                innerTNTPos.getX() + 0.5D,
                innerTNTPos.getY(),
                innerTNTPos.getZ() + 0.5D,
                null);
            
            // Very short fuse for inner TNT
            innerTNT.setFuse(5 + world.rand.nextInt(10));
            
            // Add the TNT to the world
            world.addEntity(innerTNT);
        }
    }
}