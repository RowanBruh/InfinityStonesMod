package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityStones.StoneType;
import com.infinitystones.util.StoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class InfinityGauntlet extends Item {
    // NBT key for the stones
    private static final String NBT_STONES = "InfinityStones";
    
    // The inventory handler for the stones
    private final ItemStackHandler stoneInventory;
    
    public InfinityGauntlet() {
        super(new Item.Properties()
                .group(InfinityStonesMod.INFINITY_GROUP)
                .maxStackSize(1)
                .setNoRepair());
        
        // Create inventory with 6 slots, one for each stone
        this.stoneInventory = new ItemStackHandler(6);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (playerIn.isCrouching()) {
            // Open the GUI when the player is sneaking and right-clicks
            if (!worldIn.isRemote) {
                openGUI(playerIn, stack);
            }
            return ActionResult.resultConsume(stack);
        } else {
            // Activate special ability when right-clicking without sneaking
            if (!playerIn.getCooldownTracker().hasCooldown(this)) {
                // Check if we have all stones equipped
                if (hasAllStones(stack)) {
                    // Activate the ultimate ability
                    boolean success = StoneAbilities.activateInfinityGauntlet(worldIn, playerIn);
                    
                    if (success) {
                        return ActionResult.resultSuccess(stack);
                    }
                } else {
                    // If we don't have all stones, check which stones we have and activate one randomly
                    StoneType[] equippedStones = getEquippedStones(stack);
                    
                    if (equippedStones.length > 0) {
                        // Pick a random stone from the ones we have
                        StoneType randomStone = equippedStones[worldIn.rand.nextInt(equippedStones.length)];
                        boolean success = StoneAbilities.activateStoneAbility(worldIn, playerIn, randomStone);
                        
                        if (success) {
                            // Set a shorter cooldown for individual stone use
                            playerIn.getCooldownTracker().setCooldown(this, 60);
                            return ActionResult.resultSuccess(stack);
                        }
                    } else {
                        // No stones equipped
                        if (!worldIn.isRemote) {
                            playerIn.sendMessage(
                                    new StringTextComponent("The Infinity Gauntlet has no stones equipped!")
                                            .mergeStyle(TextFormatting.YELLOW),
                                    playerIn.getUniqueID());
                        }
                    }
                }
            }
            
            return ActionResult.resultPass(stack);
        }
    }
    
    private void openGUI(PlayerEntity player, ItemStack stack) {
        // Create the container provider for the GUI
        player.openContainer(new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("Infinity Gauntlet");
            }
            
            @Nullable
            @Override
            public Container createMenu(int windowId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
                return new GauntletContainer(windowId, playerInventory, getInventory(stack));
            }
        });
    }
    
    /**
     * Gets the inventory handler for the gauntlet
     */
    public ItemStackHandler getInventory(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(6);
        
        // Load the inventory from NBT if it exists
        if (stack.hasTag() && stack.getTag().contains(NBT_STONES)) {
            handler.deserializeNBT((CompoundNBT) stack.getTag().get(NBT_STONES));
        }
        
        return handler;
    }
    
    /**
     * Saves the inventory handler to the gauntlet's NBT
     */
    public void saveInventory(ItemStack stack, ItemStackHandler inventory) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        
        stack.getTag().put(NBT_STONES, inventory.serializeNBT());
    }
    
    /**
     * Checks if the gauntlet has all infinity stones
     */
    public boolean hasAllStones(ItemStack stack) {
        ItemStackHandler inventory = getInventory(stack);
        boolean[] stonesPresent = new boolean[StoneType.values().length];
        
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stoneStack = inventory.getStackInSlot(i);
            
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                InfinityStones.StoneType stoneType = ((InfinityStones.InfinityStoneItem) stoneStack.getItem()).getStoneType();
                stonesPresent[stoneType.ordinal()] = true;
            }
        }
        
        // Check if all stones are present
        for (boolean stonePresent : stonesPresent) {
            if (!stonePresent) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets an array of the equipped stone types
     */
    public StoneType[] getEquippedStones(ItemStack stack) {
        ItemStackHandler inventory = getInventory(stack);
        int stoneCount = 0;
        
        // Count stones first
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stoneStack = inventory.getStackInSlot(i);
            
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                stoneCount++;
            }
        }
        
        // Create array and fill it
        StoneType[] result = new StoneType[stoneCount];
        int index = 0;
        
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stoneStack = inventory.getStackInSlot(i);
            
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                result[index++] = ((InfinityStones.InfinityStoneItem) stoneStack.getItem()).getStoneType();
            }
        }
        
        return result;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        // Add basic information
        tooltip.add(new StringTextComponent("The Infinity Gauntlet").mergeStyle(TextFormatting.GOLD));
        tooltip.add(new StringTextComponent("Right-click to use, Shift+Right-click to open")
                .mergeStyle(TextFormatting.GRAY));
        
        // Add information about equipped stones
        StoneType[] equippedStones = getEquippedStones(stack);
        
        if (equippedStones.length > 0) {
            tooltip.add(new StringTextComponent("Equipped Stones:").mergeStyle(TextFormatting.AQUA));
            
            for (StoneType stoneType : equippedStones) {
                tooltip.add(new StringTextComponent(" - " + stoneType.name()).mergeStyle(stoneType.getColor()));
            }
            
            if (hasAllStones(stack)) {
                tooltip.add(new StringTextComponent("UNLIMITED POWER!").mergeStyle(TextFormatting.RED, TextFormatting.BOLD));
            }
        } else {
            tooltip.add(new StringTextComponent("No stones equipped").mergeStyle(TextFormatting.GRAY));
        }
        
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    /**
     * Container class for the Infinity Gauntlet
     */
    public static class GauntletContainer extends Container {
        private final ItemStackHandler inventory;
        
        public GauntletContainer(int windowId, net.minecraft.entity.player.PlayerInventory playerInventory, ItemStackHandler inventory) {
            super(null, windowId); // Container type will be registered separately
            this.inventory = inventory;
            
            // Add stone slots
            // This is where we would add the slots to the container
            // For simplicity, we're not implementing the full container mechanics here
        }
        
        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return true;
        }
    }
}