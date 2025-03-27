package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class OneWayBlock extends Block {
    // Direction the block is facing - the open side where you can see through
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public OneWayBlock() {
        super(Block.Properties.create(Material.ROCK)
                .hardnessAndResistance(2.0F, 6.0F)
                .sound(SoundType.STONE)
                .notSolid()); // notSolid is important for transparency
        
        // Set default state to face north
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // Get the direction the player is looking (opposite to where the block will face)
        Direction direction = context.getNearestLookingDirection().getOpposite();
        
        // Save the block that was clicked on - for tile entity to mimic its texture
        BlockPos clickedPos = context.getPos().offset(context.getFace().getOpposite());
        BlockState clickedState = context.getWorld().getBlockState(clickedPos);
        
        // Only store valid blocks that have a visible texture (not air, not tile entities)
        if (clickedState.getBlock() != Blocks.AIR && !(clickedState.getBlock() instanceof OneWayBlock)) {
            // Store the block identity for the tile entity to reference
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("MimicBlock", clickedState.getBlock().getRegistryName().toString());
            
            // Get any additional block state properties that might be important for the texture
            if (!clickedState.getProperties().isEmpty()) {
                CompoundNBT stateNBT = new CompoundNBT();
                for (net.minecraft.state.Property<?> prop : clickedState.getProperties()) {
                    stateNBT.putString(prop.getName(), clickedState.get(prop).toString());
                }
                nbt.put("MimicState", stateNBT);
            }
            
            // Store this data for later use when the tile entity is created
            context.getItem().getOrCreateTag().put("BlockData", nbt);
        }
        
        return this.getDefaultState().with(FACING, direction);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new OneWayBlockTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable net.minecraft.entity.LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        
        // Transfer the stored block data to the tile entity
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof OneWayBlockTileEntity && stack.hasTag() && stack.getTag().contains("BlockData")) {
            ((OneWayBlockTileEntity) te).setMimicData(stack.getTag().getCompound("BlockData"));
        }
    }

    // Block appears solid from all sides except the facing direction
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.fullCube();
    }

    // Determine collision - for walking into the block
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        // Entities can't walk through the block from any side
        return VoxelShapes.fullCube();
    }

    // This determines rendering - crucial for one-way functionality
    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        // The block's render shape is always a full cube
        return VoxelShapes.fullCube();
    }

    // Determine if the block is opaque - important for rendering
    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F; // No ambient occlusion
    }

    // Rotation and mirroring support for the facing property
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    // Prevent mob spawning on this block
    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, EntityType<?> entityType) {
        return false;
    }
}