package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LavaTrapBlock extends Block {
    public LavaTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            triggerLavaTrap(worldIn, pos);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    private void triggerLavaTrap(World world, BlockPos pos) {
        // Play a lava sound effect
        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_AMBIENT, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Create a lava pool pattern
        createLavaPool(world, pos);
        
        // Remove the trap block after activation
        world.removeBlock(pos, false);
    }
    
    private void createLavaPool(World world, BlockPos centerPos) {
        // Create a 3x3 lava pool centered on the trap
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos lavaPos = new BlockPos(
                    centerPos.getX() + x,
                    centerPos.getY(),
                    centerPos.getZ() + z
                );
                
                // If the block is air or can be replaced, set it to lava
                if (world.isAirBlock(lavaPos) || world.getBlockState(lavaPos).getMaterial().isReplaceable()) {
                    world.setBlockState(lavaPos, Blocks.LAVA.getDefaultState());
                }
            }
        }
        
        // Check if we can place lava below
        BlockPos belowPos = centerPos.down();
        if (world.isAirBlock(belowPos) || world.getBlockState(belowPos).getMaterial().isReplaceable()) {
            world.setBlockState(belowPos, Blocks.LAVA.getDefaultState());
            
            // Create a deeper pool in a 3x3 area below if possible
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos deepPos = new BlockPos(
                        belowPos.getX() + x,
                        belowPos.getY(),
                        belowPos.getZ() + z
                    );
                    
                    if (world.isAirBlock(deepPos) || world.getBlockState(deepPos).getMaterial().isReplaceable()) {
                        world.setBlockState(deepPos, Blocks.LAVA.getDefaultState());
                    }
                }
            }
        }
    }
}