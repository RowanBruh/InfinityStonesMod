package com.infinitystones.gui;

import com.infinitystones.items.InfinityGauntlet;
import com.infinitystones.items.InfinityStones;
import com.infinitystones.items.InfinityStones.StoneType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import com.infinitystones.InfinityStonesMod;

public class GauntletContainer extends Container {
    private final ItemStack gauntletStack;
    private final ItemStackHandler stoneHandler;
    private final PlayerEntity player;
    
    public GauntletContainer(int windowId, PlayerInventory playerInventory, ItemStack gauntletStack) {
        super(InfinityStonesMod.GAUNTLET_CONTAINER, windowId);
        this.gauntletStack = gauntletStack;
        this.player = playerInventory.player;
        this.stoneHandler = InfinityGauntlet.getStoneHandler(gauntletStack);
        
        // Add the Infinity Stone slots
        addStoneSlots();
        
        // Add player inventory slots
        addPlayerInventorySlots(playerInventory);
    }
    
    private void addStoneSlots() {
        // Add 6 slots for the Infinity Stones in a 3x2 grid
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int index = col + row * 3;
                int xPos = 44 + col * 30;
                int yPos = 20 + row * 30;
                
                // Add slots that only accept the corresponding Infinity Stone
                final int slotIndex = index;
                addSlot(new SlotItemHandler(stoneHandler, index, xPos, yPos) {
                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        if (stack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                            InfinityStones.InfinityStoneItem stone = (InfinityStones.InfinityStoneItem) stack.getItem();
                            StoneType type = stone.getStoneType();
                            
                            // Map slot index to stone type
                            StoneType expectedType = null;
                            switch (slotIndex) {
                                case 0: expectedType = StoneType.SPACE; break;
                                case 1: expectedType = StoneType.MIND; break;
                                case 2: expectedType = StoneType.REALITY; break;
                                case 3: expectedType = StoneType.POWER; break;
                                case 4: expectedType = StoneType.TIME; break;
                                case 5: expectedType = StoneType.SOUL; break;
                            }
                            
                            return type == expectedType;
                        }
                        return false;
                    }
                });
            }
        }
    }
    
    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // Add player hotbar slots
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        // Check if player can still use the gauntlet
        ItemStack mainHand = playerIn.getHeldItemMainhand();
        ItemStack offHand = playerIn.getHeldItemOffhand();
        return (mainHand == gauntletStack || offHand == gauntletStack);
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();
            
            if (index < 6) {
                // If clicking a stone slot, move to player inventory
                if (!this.mergeItemStack(slotStack, 6, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // If clicking player inventory, try to move to appropriate stone slot
                if (slotStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                    InfinityStones.InfinityStoneItem stone = (InfinityStones.InfinityStoneItem) slotStack.getItem();
                    int targetSlot = stone.getStoneType().ordinal();
                    
                    if (!this.inventorySlots.get(targetSlot).getHasStack() && 
                        this.inventorySlots.get(targetSlot).isItemValid(slotStack)) {
                        if (!this.mergeItemStack(slotStack, targetSlot, targetSlot + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        
        // Save the stone inventory back to the gauntlet NBT
        InfinityGauntlet.saveStoneHandler(gauntletStack, stoneHandler);
    }
}
