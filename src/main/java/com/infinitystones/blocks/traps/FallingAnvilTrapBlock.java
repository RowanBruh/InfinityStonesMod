package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FallingAnvilTrapBlock extends Block {
    private static final int ANVIL_HEIGHT = 5; // How high to spawn the anvils
    
    public FallingAnvilTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            triggerAnvilTrap(worldIn, pos, (LivingEntity) entityIn);
            
            // Remove the trap block after activation
            worldIn.removeBlock(pos, false);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    private void triggerAnvilTrap(World world, BlockPos pos, LivingEntity target) {
        // Play sound effect
        world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_HURT, 
                      SoundCategory.BLOCKS, 1.0F, 0.5F);
        
        // Summon the anvils
        summonAnvils(world, pos);
    }
    
    private void summonAnvils(World world, BlockPos centerPos) {
        Random random = world.rand;
        
        // Summon anvils in a pattern above the player
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // Skip some positions randomly for a more natural pattern
                if (random.nextFloat() < 0.3f) {
                    continue;
                }
                
                // Calculate spawn position for anvil
                BlockPos anvilPos = new BlockPos(
                    centerPos.getX() + x,
                    centerPos.getY() + ANVIL_HEIGHT + random.nextInt(3), // Add some height variation
                    centerPos.getZ() + z
                );
                
                // Determine which type of anvil to use (damaged or not)
                BlockState anvilState;
                float damageChance = random.nextFloat();
                if (damageChance < 0.3f) {
                    // 30% chance for very damaged anvil
                    anvilState = Blocks.CHIPPED_ANVIL.getDefaultState();
                } else if (damageChance < 0.6f) {
                    // 30% chance for chipped anvil
                    anvilState = Blocks.DAMAGED_ANVIL.getDefaultState();
                } else {
                    // 40% chance for normal anvil
                    anvilState = Blocks.ANVIL.getDefaultState();
                }
                
                // Create the falling anvil entity
                FallingBlockEntity fallingAnvil = new FallingBlockEntity(
                    world, 
                    anvilPos.getX() + 0.5D, 
                    anvilPos.getY(), 
                    anvilPos.getZ() + 0.5D, 
                    anvilState
                );
                
                // Set some properties to make it more dangerous
                fallingAnvil.fallTime = 1; // Start falling immediately
                fallingAnvil.shouldDropItem = false; // Don't drop as item if it can't land
                
                // Add the falling anvil to the world
                world.addEntity(fallingAnvil);
                
                // Play an additional falling sound
                world.playSound(null, anvilPos, SoundEvents.BLOCK_ANVIL_FALL, 
                              SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            }
        }
        
        // Add a guaranteed anvil directly above the trap
        BlockPos directAnvilPos = centerPos.up(ANVIL_HEIGHT);
        FallingBlockEntity directAnvil = new FallingBlockEntity(
            world, 
            directAnvilPos.getX() + 0.5D, 
            directAnvilPos.getY(), 
            directAnvilPos.getZ() + 0.5D, 
            Blocks.ANVIL.getDefaultState()
        );
        
        // Add the direct anvil to the world
        world.addEntity(directAnvil);
        world.playSound(null, directAnvilPos, SoundEvents.BLOCK_ANVIL_FALL, 
                      SoundCategory.BLOCKS, 1.0F, 0.8F);
    }
}