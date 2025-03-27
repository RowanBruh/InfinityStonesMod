package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TrapChestBlock extends Block {

    public TrapChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, 
                                           PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            // Activate trap when the chest is right-clicked
            triggerTrapChest(worldIn, pos, player);
            
            // Play chest open sound for effect
            worldIn.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, 
                           SoundCategory.BLOCKS, 0.5F, 0.9F);
            
            // Remove the trap chest block after activation
            worldIn.removeBlock(pos, false);
        }
        
        return ActionResultType.SUCCESS;
    }
    
    private void triggerTrapChest(World world, BlockPos pos, PlayerEntity player) {
        // Determine the trap type based on random chance
        float trapChance = world.rand.nextFloat();
        
        if (trapChance < 0.25f) {
            // 25% chance for explosion
            explosionTrap(world, pos);
        } else if (trapChance < 0.50f) {
            // 25% chance for poison/wither effect
            poisonTrap(world, pos, player);
        } else if (trapChance < 0.75f) {
            // 25% chance for arrow trap
            arrowTrap(world, pos, player);
        } else {
            // 25% chance for fire trap
            fireTrap(world, pos, player);
        }
        
        // Play additional sound effect
        world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_AMBIENT, 
                      SoundCategory.HOSTILE, 1.0F, 0.5F);
    }
    
    private void explosionTrap(World world, BlockPos pos) {
        // Create a small explosion
        world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 
                           1.5F, false, Explosion.Mode.DESTROY);
    }
    
    private void poisonTrap(World world, BlockPos pos, PlayerEntity player) {
        // Add negative potion effects
        if (player instanceof ServerPlayerEntity) {
            player.addPotionEffect(new EffectInstance(Effects.POISON, 200, 1));
            player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 300, 1));
            
            // Play poison sound
            world.playSound(null, pos, SoundEvents.ENTITY_WITCH_DRINK, 
                          SoundCategory.HOSTILE, 1.0F, 1.0F);
        }
    }
    
    private void arrowTrap(World world, BlockPos pos, PlayerEntity player) {
        // Damage the player directly, simulating arrow hits
        player.attackEntityFrom(DamageSource.MAGIC, 6.0F);
        
        // Play arrow sound
        world.playSound(null, pos, SoundEvents.ENTITY_ARROW_HIT_PLAYER, 
                      SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
    
    private void fireTrap(World world, BlockPos pos, PlayerEntity player) {
        // Set the player on fire
        player.setFire(5); // 5 seconds of fire
        
        // Also deal some direct fire damage
        player.attackEntityFrom(DamageSource.IN_FIRE, 4.0F);
        
        // Play fire sound
        world.playSound(null, pos, SoundEvents.ENTITY_GHAST_SHOOT, 
                      SoundCategory.HOSTILE, 1.0F, 1.0F);
        
        // Place some fire blocks around the chest
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (world.rand.nextFloat() < 0.4f) {
                    BlockPos firePos = new BlockPos(
                        pos.getX() + x,
                        pos.getY(),
                        pos.getZ() + z
                    );
                    
                    if (world.isAirBlock(firePos)) {
                        world.setBlockState(firePos, net.minecraft.block.Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }
}