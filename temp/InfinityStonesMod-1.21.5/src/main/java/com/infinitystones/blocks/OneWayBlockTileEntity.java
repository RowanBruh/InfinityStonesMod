package com.infinitystones.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class OneWayBlockTileEntity extends TileEntity {
    // The block to mimic on the visible side
    private String mimicBlockId = "minecraft:stone";
    private CompoundNBT mimicBlockState = new CompoundNBT();

    public OneWayBlockTileEntity() {
        super(ModTileEntities.ONE_WAY_BLOCK.get());
    }

    public void setMimicData(CompoundNBT data) {
        if (data.contains("MimicBlock")) {
            mimicBlockId = data.getString("MimicBlock");
        }
        
        if (data.contains("MimicState")) {
            mimicBlockState = data.getCompound("MimicState");
        }
        
        // Mark the tile entity as needing to save and update clients
        this.markDirty();
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("MimicBlock")) {
            mimicBlockId = nbt.getString("MimicBlock");
        }
        
        if (nbt.contains("MimicState")) {
            mimicBlockState = nbt.getCompound("MimicState");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putString("MimicBlock", mimicBlockId);
        compound.put("MimicState", mimicBlockState);
        return compound;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        // Send the full NBT data
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        // Prepare update packet with the full tag
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        // Handle incoming data packet
        this.read(this.getBlockState(), pkt.getNbtCompound());
    }

    /**
     * Gets the block that this one-way block should mimic for rendering
     */
    public Block getMimicBlock() {
        // Convert the stored ID back to a Block
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(mimicBlockId));
        return block != null ? block : Blocks.STONE; // Default to stone if the block can't be found
    }

    /**
     * Creates a BlockState for the mimic block with properties from stored NBT
     */
    public BlockState getMimicBlockState() {
        Block block = getMimicBlock();
        BlockState state = block.getDefaultState();
        
        // Apply stored properties to the block state if possible
        for (String key : mimicBlockState.keySet()) {
            try {
                // Find the property with this name in the block's properties
                for (Property<?> property : state.getProperties()) {
                    if (property.getName().equals(key)) {
                        // This is an unsafe cast but it's the only way to handle this generically
                        state = applyProperty(state, property, mimicBlockState.getString(key));
                        break;
                    }
                }
            } catch (Exception e) {
                // If any property can't be applied, just continue with the next one
                continue;
            }
        }
        
        return state;
    }
    
    /**
     * Helper method to apply a property value from a string
     */
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String valueString) {
        return property.parseValue(valueString).map(value -> (BlockState)state.with(property, value)).orElse(state);
    }

    /**
     * For client-side model rendering to get the mimicked block's appearance
     */
    public BlockState getRenderState() {
        return getMimicBlockState();
    }
}