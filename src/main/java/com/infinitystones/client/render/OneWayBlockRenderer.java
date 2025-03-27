package com.infinitystones.client.render;

import com.infinitystones.blocks.OneWayBlock;
import com.infinitystones.blocks.OneWayBlockTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

public class OneWayBlockRenderer extends TileEntityRenderer<OneWayBlockTileEntity> {

    public OneWayBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(OneWayBlockTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, 
                      IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        
        // Get the block state of the one-way block
        World world = tileEntity.getWorld();
        BlockPos pos = tileEntity.getPos();
        
        if (world == null) return;
        
        BlockState state = world.getBlockState(pos);
        
        // Only render if it's actually our block
        if (!(state.getBlock() instanceof OneWayBlock)) return;
        
        // Get the direction the block is facing
        Direction facing = state.get(OneWayBlock.FACING);
        
        // Get the mimic block state from the tile entity
        BlockState mimicState = tileEntity.getMimicBlockState();
        
        // Get the renderer
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        
        // Remember the original position
        matrixStack.push();
        
        // Position for rendering - we need a slight offset in the facing direction to avoid z-fighting
        // The value 0.005 is small enough to not be noticeable but large enough to fix z-fighting
        Vector3d offset = Vector3d.copy(facing.getDirectionVec()).scale(0.005);
        matrixStack.translate(0.5 + offset.x, 0.5 + offset.y, 0.5 + offset.z);
        
        // Rotate according to facing direction
        switch (facing) {
            case DOWN:
                matrixStack.rotate(net.minecraft.util.math.vector.Quaternion.fromXYZ(180, 0, 0));
                break;
            case UP:
                // No rotation needed for UP
                break;
            case NORTH:
                matrixStack.rotate(net.minecraft.util.math.vector.Quaternion.fromXYZ(90, 0, 0));
                break;
            case SOUTH:
                matrixStack.rotate(net.minecraft.util.math.vector.Quaternion.fromXYZ(-90, 0, 0));
                break;
            case WEST:
                matrixStack.rotate(net.minecraft.util.math.vector.Quaternion.fromXYZ(0, 0, 90));
                break;
            case EAST:
                matrixStack.rotate(net.minecraft.util.math.vector.Quaternion.fromXYZ(0, 0, -90));
                break;
        }
        
        // Move to corner for block rendering
        matrixStack.translate(-0.5, -0.5, -0.5);
        
        // Render just one face of the mimic block (the one facing outwards)
        dispatcher.renderBlock(mimicState, matrixStack, buffer, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
        
        // Restore original position
        matrixStack.pop();
    }
}