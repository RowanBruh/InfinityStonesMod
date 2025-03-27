package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LandmineBlock extends Block {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    
    public LandmineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            // Play a ticking sound before explosion
            worldIn.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, 
                           SoundCategory.BLOCKS, 1.0F, 1.0F);
            
            // Schedule the explosion with a slight delay for dramatic effect
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 10);
            
            // Remove the landmine block
            worldIn.removeBlock(pos, false);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, java.util.Random rand) {
        if (!worldIn.isRemote) {
            // Create an explosion at the landmine position
            createExplosion(worldIn, pos);
        }
    }
    
    private void createExplosion(World world, BlockPos pos) {
        // Create an explosion
        float explosionPower = 3.0F; // Adjust explosion power as needed
        boolean causeFire = false; // Set to true if you want the explosion to cause fire
        boolean destroyBlocks = true; // Set to true to destroy blocks
        
        world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 
                           explosionPower, causeFire, destroyBlocks ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
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