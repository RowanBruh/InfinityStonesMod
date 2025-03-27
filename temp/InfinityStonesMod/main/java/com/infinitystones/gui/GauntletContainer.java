package com.infinitystones.gui;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityGauntlet;
import com.infinitystones.items.InfinityStones;
import com.infinitystones.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The container for the Infinity Gauntlet GUI
 */
public class GauntletContainer extends Container {
    
    // The container type - will be registered later
    @ObjectHolder(InfinityStonesMod.MOD_ID + ":gauntlet_container")
    public static ContainerType<GauntletContainer> TYPE;
    
    private final ItemStackHandler itemHandler;
    private final ItemStack gauntletStack;
    private final Hand hand;
    
    /**
     * Creates a container on the server
     */
    public GauntletContainer(int windowId, PlayerInventory playerInventory, Hand hand) {
        this(windowId, playerInventory, hand, new ItemStackHandler(6));
    }
    
    /**
     * Creates a container on the client
     */
    public GauntletContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory, extraData.readEnumValue(Hand.class), new ItemStackHandler(6));
        
        // Read the stone inventory from the network
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, extraData.readItemStack());
        }
    }
    
    /**
     * Constructor with all parameters
     */
    public GauntletContainer(int windowId, PlayerInventory playerInventory, Hand hand, ItemStackHandler itemHandler) {
        super(TYPE, windowId);
        this.hand = hand;
        this.gauntletStack = playerInventory.player.getHeldItem(hand);
        this.itemHandler = itemHandler;
        
        // Add stone slots
        for (int i = 0; i < InfinityStones.StoneType.values().length; i++) {
            // Position the slots around the gauntlet image
            int x = 0;
            int y = 0;
            
            switch (i) {
                case 0: // SPACE
                    x = 80; y = 20;
                    break;
                case 1: // MIND
                    x = 50; y = 40;
                    break;
                case 2: // REALITY
                    x = 110; y = 40;
                    break;
                case 3: // POWER
                    x = 50; y = 70;
                    break;
                case 4: // TIME
                    x = 110; y = 70;
                    break;
                case 5: // SOUL
                    x = 80; y = 90;
                    break;
            }
            
            final InfinityStones.StoneType stoneType = InfinityStones.StoneType.values()[i];
            
            // Add a slot for this stone type
            addSlot(new SlotItemHandler(itemHandler, i, x, y) {
                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return stack.getItem() instanceof InfinityStones.InfinityStoneItem &&
                           ((InfinityStones.InfinityStoneItem) stack.getItem()).getStoneType() == stoneType;
                }
            });
        }
        
        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 166 - (4 - row) * 18 - 10));
            }
        }
        
        // Add player hotbar slots
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn.getHeldItem(hand).getItem() instanceof InfinityGauntlet;
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            
            // If we're moving from a stone slot to the player inventory
            if (index < 6) {
                if (!this.mergeItemStack(slotStack, 6, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } 
            // If we're moving from the player inventory to a stone slot
            else if (!this.mergeItemStack(slotStack, 0, 6, false)) {
                return ItemStack.EMPTY;
            }
            
            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        
        return itemStack;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        
        // Save the inventory back to the gauntlet
        if (playerIn.getHeldItem(hand).getItem() instanceof InfinityGauntlet) {
            InfinityGauntlet gauntlet = (InfinityGauntlet) playerIn.getHeldItem(hand).getItem();
            gauntlet.saveInventory(playerIn.getHeldItem(hand), itemHandler);
        }
    }
}