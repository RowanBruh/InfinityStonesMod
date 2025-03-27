package com.infinitystones.blocks.traps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class ArrowTrapBlock extends Block {
    public ArrowTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            triggerArrowTrap(worldIn, pos, (LivingEntity) entityIn);
            
            // Remove the trap block after activation
            worldIn.removeBlock(pos, false);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }
    
    private void triggerArrowTrap(World world, BlockPos pos, LivingEntity target) {
        // Play sound effect
        world.playSound(null, pos, SoundEvents.ENTITY_ARROW_SHOOT, 
                      SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Spawn arrows from multiple directions
        Random random = world.rand;
        
        // Horizontal directions
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int i = 0; i < 3; i++) { // 3 arrows per direction
                // Calculate spawn position
                BlockPos arrowPos = pos.offset(direction, 2);
                
                // Create arrow entity
                ArrowEntity arrow = new ArrowEntity(world, 
                    arrowPos.getX() + 0.5D, 
                    arrowPos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 0.5D, // slight Y variation
                    arrowPos.getZ() + 0.5D);
                
                // Set arrow properties
                arrow.setDamage(4.0D); // Base damage
                arrow.setKnockbackStrength(1); // Some knockback
                
                // Set velocity (shoot toward the center)
                Vector3d velocity = new Vector3d(
                    pos.getX() - arrowPos.getX(),
                    pos.getY() - arrowPos.getY(),
                    pos.getZ() - arrowPos.getZ()
                ).normalize().scale(1.5D + random.nextDouble() * 0.5D); // Speed with variation
                
                arrow.setMotion(velocity);
                
                // Add a bit of randomness to make it look natural
                arrow.setMotion(
                    arrow.getMotion().x + (random.nextDouble() - 0.5D) * 0.1D,
                    arrow.getMotion().y + (random.nextDouble() - 0.5D) * 0.1D,
                    arrow.getMotion().z + (random.nextDouble() - 0.5D) * 0.1D
                );
                
                // Add to world
                world.addEntity(arrow);
            }
        }
        
        // Also add arrows from above
        for (int i = 0; i < 5; i++) { // 5 arrows from above
            BlockPos abovePos = pos.up(3).add(
                (random.nextDouble() - 0.5D) * 2.0D, 
                0, 
                (random.nextDouble() - 0.5D) * 2.0D
            );
            
            ArrowEntity arrow = new ArrowEntity(world, 
                abovePos.getX(), 
                abovePos.getY(), 
                abovePos.getZ());
            
            arrow.setDamage(5.0D); // Slightly more damage for arrows from above
            arrow.setKnockbackStrength(1);
            
            // Set downward velocity with a slight horizontal component
            arrow.setMotion(
                (random.nextDouble() - 0.5D) * 0.2D,
                -1.0D - random.nextDouble() * 0.5D,
                (random.nextDouble() - 0.5D) * 0.2D
            );
            
            // Add to world
            world.addEntity(arrow);
        }
    }
}