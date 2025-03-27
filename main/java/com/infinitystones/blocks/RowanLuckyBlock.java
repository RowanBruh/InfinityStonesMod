package com.infinitystones.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

/**
 * A custom lucky block implementation for the Rowan Industries tab
 */
public class RowanLuckyBlock extends Block {
    
    private static final Random RANDOM = new Random();
    
    /**
     * Constructor for the Rowan Lucky Block
     */
    public RowanLuckyBlock() {
        super(Block.Properties.create(Material.ROCK, MaterialColor.GOLD)
                .hardnessAndResistance(0.5F)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(0)
                .setLightLevel(state -> 4));  // Emit light level 4
    }
    
    /**
     * Called when the block is right-clicked
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, 
                                             Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            // Trigger a random effect (to be implemented)
            triggerRandomEffect(world, pos, player);
            
            // Remove the block after use
            world.removeBlock(pos, false);
            
            return ActionResultType.SUCCESS;
        }
        
        return ActionResultType.SUCCESS;
    }
    
    /**
     * Called when the block is broken
     */
    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isRemote && !player.isCreative()) {
            // Trigger a random effect when broken
            triggerRandomEffect(world, pos, player);
        }
        
        super.onBlockHarvested(world, pos, state, player);
    }
    
    /**
     * Triggers a random effect
     *
     * @param world The world
     * @param pos The position
     * @param player The player
     */
    private void triggerRandomEffect(World world, BlockPos pos, PlayerEntity player) {
        // In a full implementation, this would have a variety of random effects
        // For demonstration purposes, we'll just print a message to the console
        System.out.println("RowanLuckyBlock triggered a random effect at " + pos + " for player " + player.getName().getString());
    }
}