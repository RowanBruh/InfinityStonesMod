package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PitfallTrapBlock extends Block {
    private static final int PIT_DEPTH = 5; // How deep the pit will be
    
    public PitfallTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            triggerPitfallTrap(worldIn, pos);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    private void triggerPitfallTrap(World world, BlockPos pos) {
        // Play sound effect
        world.playSound(null, pos, SoundEvents.BLOCK_GRAVEL_BREAK, 
                      SoundCategory.BLOCKS, 1.0F, 0.8F);
        
        // Create a pit
        createPit(world, pos);
        
        // The trap block is removed as part of creating the pit
    }
    
    private void createPit(World world, BlockPos centerPos) {
        // Create a 3x3 pit
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // Position of blocks to remove for the pit
                BlockPos pitPos = new BlockPos(
                    centerPos.getX() + x,
                    centerPos.getY(),
                    centerPos.getZ() + z
                );
                
                // Create the pit with specified depth
                for (int y = 0; y >= -PIT_DEPTH; y--) {
                    BlockPos currentPos = pitPos.add(0, y, 0);
                    
                    // Don't replace bedrock
                    if (world.getBlockState(currentPos).getBlock() != Blocks.BEDROCK) {
                        // Clear space for the pit
                        world.removeBlock(currentPos, false);
                    }
                }
                
                // Add some dangerous blocks at the bottom for extra trap effect
                if (world.rand.nextFloat() < 0.3f) {
                    // 30% chance for lava at the bottom
                    BlockPos bottomPos = pitPos.add(0, -PIT_DEPTH, 0);
                    world.setBlockState(bottomPos, Blocks.LAVA.getDefaultState());
                } else if (world.rand.nextFloat() < 0.5f) {
                    // 50% chance for pointed iron blocks (representing spikes)
                    BlockPos bottomPos = pitPos.add(0, -PIT_DEPTH, 0);
                    world.setBlockState(bottomPos, Blocks.IRON_BARS.getDefaultState());
                }
            }
        }
        
        // Add some special effects around the edges of the pit
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // Only work on the border
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    BlockPos edgePos = new BlockPos(
                        centerPos.getX() + x,
                        centerPos.getY() - 1, // Just below the surface
                        centerPos.getZ() + z
                    );
                    
                    // If there's a solid block here
                    if (!world.isAirBlock(edgePos) && 
                            world.getBlockState(edgePos).isSolidSide(world, edgePos, Direction.UP)) {
                        // Randomly add falling blocks like gravel or sand to make it seem unstable
                        if (world.rand.nextFloat() < 0.4f) {
                            if (world.rand.nextBoolean()) {
                                world.setBlockState(edgePos, Blocks.GRAVEL.getDefaultState());
                            } else {
                                world.setBlockState(edgePos, Blocks.SAND.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }
}