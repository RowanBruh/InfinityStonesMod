package com.infinitystones.network;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Handles network communication for the mod
 */
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(InfinityStonesMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    private static int id = 0;
    
    /**
     * Registers all packet types
     */
    public static void register() {
        // Register flight activation packet
        INSTANCE.registerMessage(id++, 
                FlightActivationPacket.class, 
                FlightActivationPacket::encode, 
                FlightActivationPacket::decode, 
                FlightActivationPacket::handle);
        
        // Add other packets here as needed
    }
}