package com.infinitystones.blocks.skiddzie;

import com.infinitystones.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;

public class EnhancedCommandBlockTileEntity extends TileEntity implements ITickableTileEntity {

    private String command = "";
    private int executionCooldown = 0;
    private static final int DEFAULT_COOLDOWN = 20; // 1 second

    // Command execution behavior modes
    public enum CommandMode {
        SINGLE("Single Use", 0),
        REPEATING("Repeating", 1),
        CHAIN("Chain", 2),
        REDSTONE("Redstone", 3);

        private final String name;
        private final int id;

        CommandMode(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public static CommandMode fromId(int id) {
            for (CommandMode mode : values()) {
                if (mode.id == id) return mode;
            }
            return SINGLE;
        }
    }

    private CommandMode mode = CommandMode.SINGLE;
    private boolean hasExecuted = false;
    private boolean needsRedstone = false;
    private UUID lastExecutedBy = null;

    public EnhancedCommandBlockTileEntity() {
        super(ModBlocks.ENHANCED_COMMAND_BLOCK_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if (world == null || world.isRemote) return;

        if (executionCooldown > 0) {
            executionCooldown--;
            return;
        }

        // Check execution conditions based on mode
        switch (mode) {
            case SINGLE:
                if (!hasExecuted) {
                    executeCommand(null);
                    hasExecuted = true;
                }
                break;
            case REPEATING:
                executeCommand(null);
                executionCooldown = DEFAULT_COOLDOWN;
                break;
            case CHAIN:
                // Chain mode would check the previous command block in a chain
                // For simplicity, we're treating it like repeating here
                executeCommand(null);
                executionCooldown = DEFAULT_COOLDOWN;
                break;
            case REDSTONE:
                boolean powered = world.isBlockPowered(pos);
                if (powered) {
                    executeCommand(null);
                    executionCooldown = 5; // Shorter cooldown for redstone
                }
                break;
        }
    }

    public void executeCommand(@Nullable PlayerEntity player) {
        if (world == null || world.isRemote || command.isEmpty()) return;

        ServerWorld serverWorld = (ServerWorld) world;
        
        try {
            // Create command source
            CommandSource source;
            if (player != null) {
                lastExecutedBy = player.getUniqueID();
                source = player.getCommandSource();
            } else {
                // Create a command source at this block's position
                source = new CommandSource(
                        serverWorld.getServer(),
                        new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                        Vector2f.ZERO,
                        serverWorld,
                        4, // Permission level: 4 for op-level commands
                        "EnhancedCommandBlock",
                        new StringTextComponent("EnhancedCommandBlock"),
                        serverWorld.getServer(),
                        null
                );
            }

            // Execute the command
            serverWorld.getServer().getCommandManager().handleCommand(source, command);
            
            // If this is a non-admin player, notify them of execution
            if (player != null && !player.hasPermissionLevel(4)) {
                player.sendMessage(
                        new StringTextComponent("Command executed: " + command).mergeStyle(TextFormatting.GREEN),
                        player.getUniqueID()
                );
            }
        } catch (Exception e) {
            if (player != null) {
                player.sendMessage(
                        new StringTextComponent("Error executing command: " + e.getMessage()).mergeStyle(TextFormatting.RED),
                        player.getUniqueID()
                );
            }
        }
    }

    public CommandMode getCurrentMode() {
        return mode;
    }

    public void cycleCommandMode() {
        int nextModeId = (mode.getId() + 1) % CommandMode.values().length;
        mode = CommandMode.fromId(nextModeId);
        markDirty();
        
        // Reset execution state when mode changes
        hasExecuted = false;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command, PlayerEntity player) {
        this.command = command;
        this.hasExecuted = false;
        this.lastExecutedBy = player.getUniqueID();
        markDirty();
    }

    public void setNeedsRedstone(boolean needsRedstone) {
        this.needsRedstone = needsRedstone;
        markDirty();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        command = nbt.getString("Command");
        mode = CommandMode.fromId(nbt.getInt("Mode"));
        hasExecuted = nbt.getBoolean("HasExecuted");
        needsRedstone = nbt.getBoolean("NeedsRedstone");
        if (nbt.hasUniqueId("LastExecutedBy")) {
            lastExecutedBy = nbt.getUniqueId("LastExecutedBy");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        nbt.putString("Command", command);
        nbt.putInt("Mode", mode.getId());
        nbt.putBoolean("HasExecuted", hasExecuted);
        nbt.putBoolean("NeedsRedstone", needsRedstone);
        if (lastExecutedBy != null) {
            nbt.putUniqueId("LastExecutedBy", lastExecutedBy);
        }
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(getBlockState(), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }
}