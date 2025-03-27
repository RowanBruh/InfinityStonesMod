package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SpikeTrapBlock extends Block {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    
    public SpikeTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            // Deal damage to the entity
            entityIn.attackEntityFrom(DamageSource.CACTUS, 4.0F);
            
            // Add a poison effect
            if (!worldIn.isRemote && worldIn.rand.nextFloat() < 0.4f) {
                ((LivingEntity) entityIn).addPotionEffect(
                    new EffectInstance(Effects.POISON, 100, 0)
                );
            }
            
            // Add a slowness effect due to injury
            if (!worldIn.isRemote && worldIn.rand.nextFloat() < 0.6f) {
                ((LivingEntity) entityIn).addPotionEffect(
                    new EffectInstance(Effects.SLOWNESS, 60, 1)
                );
            }
            
            // Play sound effect
            worldIn.playSound(null, pos, SoundEvents.ENTITY_PLAYER_HURT, 
                           SoundCategory.BLOCKS, 0.7F, 1.0F);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        // Also trigger on collision (not just when walking on top)
        if (entityIn instanceof LivingEntity) {
            entityIn.attackEntityFrom(DamageSource.CACTUS, 2.0F);
            
            // Play sound effect
            worldIn.playSound(null, pos, SoundEvents.ENTITY_PLAYER_HURT, 
                           SoundCategory.BLOCKS, 0.5F, 1.2F);
        }
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }
    
    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }
}