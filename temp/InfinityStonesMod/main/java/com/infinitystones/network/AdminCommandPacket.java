package com.infinitystones.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for Admin Command Block functionality
 */
public class AdminCommandPacket {
    private final String command;
    
    /**
     * Constructor for the packet
     *
     * @param command The command to execute
     */
    public AdminCommandPacket(String command) {
        this.command = command;
    }
    
    /**
     * Encodes the packet
     *
     * @param buf The buffer to encode to
     */
    public void encode(PacketBuffer buf) {
        buf.writeString(command);
    }
    
    /**
     * Decodes the packet
     *
     * @param buf The buffer to decode from
     * @return The decoded packet
     */
    public static AdminCommandPacket decode(PacketBuffer buf) {
        return new AdminCommandPacket(buf.readString(32767));
    }
    
    /**
     * Handles the packet
     *
     * @param ctx The network context
     */
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                MinecraftServer server = player.getServer();
                if (server != null) {
                    // Execute the command
                    server.getCommandManager().handleCommand(server.getCommandSource(), command);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}