package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class FakeDiamondOreBlock extends Block {

    public FakeDiamondOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote) {
            triggerFakeDiamondTrap(worldIn, pos, player);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }
    
    private void triggerFakeDiamondTrap(World world, BlockPos pos, PlayerEntity player) {
        // Determine the kind of trap to trigger
        float trapChance = world.rand.nextFloat();
        
        if (trapChance < 0.3f) {
            // 30% chance for an explosion trap
            explosionTrap(world, pos);
        } else if (trapChance < 0.6f) {
            // 30% chance for a lava trap
            lavaTrap(world, pos);
        } else if (trapChance < 0.9f) {
            // 30% chance for a false diamond trap (drops coal)
            falseDiamondTrap(world, pos);
        } else {
            // 10% chance for a lucky escape (drops actual diamond)
            luckyEscape(world, pos);
        }
    }
    
    private void explosionTrap(World world, BlockPos pos) {
        // Play creeper hiss sound
        world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Delayed explosion
        world.getPendingBlockTicks().scheduleTick(pos, this, 20); // 1-second delay
    }
    
    private void lavaTrap(World world, BlockPos pos) {
        // Play lava sound effect
        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_AMBIENT, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Replace the ore block and blocks below with lava
        world.setBlockState(pos, Blocks.LAVA.getDefaultState());
        
        // Also place lava below if possible
        BlockPos belowPos = pos.down();
        if (world.isAirBlock(belowPos) || world.getBlockState(belowPos).getMaterial().isReplaceable()) {
            world.setBlockState(belowPos, Blocks.LAVA.getDefaultState());
        }
    }
    
    private void falseDiamondTrap(World world, BlockPos pos) {
        // Play break sound
        world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Spawn some coal instead of diamonds
        for (int i = 0; i < 2 + world.rand.nextInt(3); i++) {
            ItemEntity coalEntity = new ItemEntity(
                world,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                new ItemStack(Items.COAL, 1)
            );
            
            // Add random motion
            coalEntity.setMotion(
                (world.rand.nextDouble() - 0.5D) * 0.1D,
                world.rand.nextDouble() * 0.1D + 0.1D,
                (world.rand.nextDouble() - 0.5D) * 0.1D
            );
            
            world.addEntity(coalEntity);
        }
        
        // Also play a witch laugh sound
        world.playSound(null, pos, SoundEvents.ENTITY_WITCH_CELEBRATE, 
                      SoundCategory.HOSTILE, 1.0F, 1.0F);
    }
    
    private void luckyEscape(World world, BlockPos pos) {
        // Play a positive sound
        world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Spawn an actual diamond
        ItemEntity diamondEntity = new ItemEntity(
            world,
            pos.getX() + 0.5D,
            pos.getY() + 0.5D,
            pos.getZ() + 0.5D,
            new ItemStack(Items.DIAMOND, 1)
        );
        
        // Add upward motion
        diamondEntity.setMotion(0, 0.2D, 0);
        
        world.addEntity(diamondEntity);
    }
    
    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, java.util.Random rand) {
        if (!worldIn.isRemote) {
            // Create an explosion (this is called after the delay set in explosionTrap)
            worldIn.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 
                               2.0F, Explosion.Mode.DESTROY);
        }
    }
}