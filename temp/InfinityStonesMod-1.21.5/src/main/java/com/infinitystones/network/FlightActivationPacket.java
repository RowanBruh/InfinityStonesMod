package com.infinitystones.network;

import com.infinitystones.items.gods.HermesBoots;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when a player activates Hermes Boots flight
 */
public class FlightActivationPacket {
    
    public FlightActivationPacket() {
        // Empty constructor needed for deserialization
    }
    
    /**
     * Encodes this packet to the buffer
     */
    public void encode(PacketBuffer buffer) {
        // No data to encode
    }
    
    /**
     * Decodes a packet from the buffer
     */
    public static FlightActivationPacket decode(PacketBuffer buffer) {
        return new FlightActivationPacket();
    }
    
    /**
     * Handles this packet on the receiving side
     */
    public static void handle(FlightActivationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        
        context.enqueueWork(() -> {
            // Make sure this is running on the server
            ServerPlayerEntity player = context.getSender();
            if (player == null) {
                return;
            }
            
            // Check if player is wearing Hermes Boots
            ItemStack boots = player.getItemStackFromSlot(EquipmentSlotType.FEET);
            if (!boots.isEmpty() && boots.getItem() instanceof HermesBoots) {
                // Activate flight on the boots
                ((HermesBoots) boots.getItem()).activateFlight(boots, player.world, player);
            }
        });
        
        context.setPacketHandled(true);
    }
}