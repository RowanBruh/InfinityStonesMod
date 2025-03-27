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

/**
 * A command block that can be used by non-creative players
 */
public class AdminCommandBlock extends Block {
    
    /**
     * Constructor for the Admin Command Block
     */
    public AdminCommandBlock() {
        super(Block.Properties.create(Material.IRON, MaterialColor.RED)
                .hardnessAndResistance(3.0F, 6.0F)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2)
                .setRequiresTool()
                .setLightLevel(state -> 7));  // Emit light level 7
    }
    
    /**
     * Called when the block is right-clicked
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, 
                                             Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            // Server side logic is handled by the packet system
            return ActionResultType.SUCCESS;
        } else {
            // Client side - open the GUI
            openCommandGui(player);
            return ActionResultType.SUCCESS;
        }
    }
    
    /**
     * Opens the command GUI
     *
     * @param player The player to open the GUI for
     */
    private void openCommandGui(PlayerEntity player) {
        // In a real implementation, this would open a GUI screen
        // For demonstration purposes, we'll just print a message to the console
        System.out.println("Opening Admin Command Block GUI for player: " + player.getName().getString());
    }
}