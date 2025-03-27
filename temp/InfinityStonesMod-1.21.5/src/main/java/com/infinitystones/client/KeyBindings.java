package com.infinitystones.client;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.gods.HermesBoots;
import com.infinitystones.network.FlightActivationPacket;
import com.infinitystones.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, value = Dist.CLIENT)
public class KeyBindings {
    
    public static KeyBinding activateFlightKey;
    
    /**
     * Registers all the mod's key bindings
     */
    public static void register() {
        activateFlightKey = new KeyBinding("key.infinitystones.activateFlight", GLFW.GLFW_KEY_G, "key.categories.infinitystones");
        ClientRegistry.registerKeyBinding(activateFlightKey);
    }
    
    /**
     * Handles key press events for mod key bindings
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        
        if (player == null) {
            return;
        }
        
        // Handle flight activation key
        if (activateFlightKey.isPressed()) {
            handleFlightActivation(player);
        }
    }
    
    /**
     * Handles activation of Hermes Boots flight mode
     */
    private static void handleFlightActivation(PlayerEntity player) {
        ItemStack boots = player.getItemStackFromSlot(EquipmentSlotType.FEET);
        
        if (!boots.isEmpty() && boots.getItem() instanceof HermesBoots) {
            // Send packet to server to activate flight
            NetworkHandler.INSTANCE.sendToServer(new FlightActivationPacket());
        }
    }
}