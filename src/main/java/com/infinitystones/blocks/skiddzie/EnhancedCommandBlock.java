package com.infinitystones.blocks.skiddzie;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class EnhancedCommandBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public EnhancedCommandBlock() {
        super(Block.Properties.create(Material.IRON)
                .hardnessAndResistance(5.0F, 6.0F)
                .sound(SoundType.METAL)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2)
                .setLightLevel(state -> 5) // Light level of 5
        );
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnhancedCommandBlockTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }

        if (player instanceof ServerPlayerEntity) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof EnhancedCommandBlockTileEntity) {
                EnhancedCommandBlockTileEntity commandBlock = (EnhancedCommandBlockTileEntity) tileEntity;
                
                // If player is sneaking, cycle command modes
                if (player.isSneaking()) {
                    commandBlock.cycleCommandMode();
                    player.sendMessage(new StringTextComponent("Command Mode: " + commandBlock.getCurrentMode().getName())
                            .mergeStyle(TextFormatting.GOLD), player.getUniqueID());
                    return ActionResultType.SUCCESS;
                }
                
                // Open the command editing GUI
                openCommandGui((ServerPlayerEntity) player, commandBlock);
            }
        }

        return ActionResultType.SUCCESS;
    }
    
    private void openCommandGui(ServerPlayerEntity player, EnhancedCommandBlockTileEntity tileEntity) {
        // In a real implementation, this would open a GUI for command editing
        // For now, we'll just show a message about the current command
        String currentCommand = tileEntity.getCommand();
        if (currentCommand.isEmpty()) {
            player.sendMessage(new StringTextComponent("No command set. Right-click to edit.")
                    .mergeStyle(TextFormatting.RED), player.getUniqueID());
        } else {
            player.sendMessage(new StringTextComponent("Current command: " + currentCommand)
                    .mergeStyle(TextFormatting.GREEN), player.getUniqueID());
            player.sendMessage(new StringTextComponent("Mode: " + tileEntity.getCurrentMode().getName())
                    .mergeStyle(TextFormatting.GOLD), player.getUniqueID());
        }
    }
}