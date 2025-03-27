package com.infinitystones.network;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Handles network packets for the mod
 */
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(InfinityStonesMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    private static int packetId = 0;
    
    /**
     * Initializes the network handler
     */
    public static void init() {
        // Register all packets
        CHANNEL.registerMessage(
                nextID(),
                AdminCommandPacket.class,
                AdminCommandPacket::encode,
                AdminCommandPacket::decode,
                AdminCommandPacket::handle
        );
    }
    
    /**
     * Gets the next packet ID
     *
     * @return the next packet ID
     */
    private static int nextID() {
        return packetId++;
    }
}