package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import com.infinitystones.util.StoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Infinity Gauntlet that can hold Infinity Stones
 */
public class InfinityGauntlet extends Item {
    
    /**
     * Constructor for the Infinity Gauntlet
     */
    public InfinityGauntlet() {
        super(new Item.Properties()
                .group(ModItemGroups.INFINITY_STONES)
                .maxStackSize(1)
                .rarity(net.minecraft.item.Rarity.EPIC));
    }
    
    /**
     * Called when the item is right-clicked
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            // For now, let's just use the first stone in the gauntlet
            // In a full implementation, this would open a GUI to select which stone to use
            StoneType activeStone = getFirstStoneInGauntlet(stack);
            
            if (activeStone != null) {
                // Activate the stone's ability
                StoneAbilities.activateStoneAbility(activeStone, player, world);
                
                // Apply cooldown
                player.getCooldownTracker().setCooldown(this, 200); // 10 seconds cooldown
            } else {
                player.sendMessage(new StringTextComponent("The Infinity Gauntlet has no stones equipped."), 
                        player.getUniqueID());
            }
        } else {
            // In a full implementation, this would open a GUI to manage the stones
            // For demonstration purposes, we just print a message
            System.out.println("Opening Infinity Gauntlet GUI");
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Gets the first stone in the gauntlet
     *
     * @param stack The gauntlet item stack
     * @return The first stone type, or null if no stones are present
     */
    private StoneType getFirstStoneInGauntlet(ItemStack stack) {
        Map<StoneType, Boolean> stones = getStonesInGauntlet(stack);
        
        for (Map.Entry<StoneType, Boolean> entry : stones.entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        
        return null;
    }
    
    /**
     * Gets all stones in the gauntlet
     *
     * @param stack The gauntlet item stack
     * @return A map of stone types to their presence (true if present, false if not)
     */
    public Map<StoneType, Boolean> getStonesInGauntlet(ItemStack stack) {
        Map<StoneType, Boolean> stones = new HashMap<>();
        CompoundNBT nbt = stack.getOrCreateTag();
        
        for (StoneType type : StoneType.values()) {
            stones.put(type, nbt.getBoolean(type.getId() + "_stone"));
        }
        
        return stones;
    }
    
    /**
     * Sets a stone in the gauntlet
     *
     * @param stack The gauntlet item stack
     * @param type The stone type
     * @param present Whether the stone is present
     */
    public void setStoneInGauntlet(ItemStack stack, StoneType type, boolean present) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putBoolean(type.getId() + "_stone", present);
    }
    
    /**
     * Adds information to the tooltip
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("The Infinity Gauntlet")
                .mergeStyle(TextFormatting.GOLD));
        
        tooltip.add(new StringTextComponent("A powerful artifact that can harness the power of Infinity Stones")
                .mergeStyle(TextFormatting.GRAY));
        
        Map<StoneType, Boolean> stones = getStonesInGauntlet(stack);
        boolean hasAnyStone = false;
        
        tooltip.add(new StringTextComponent("Equipped Stones:").mergeStyle(TextFormatting.DARK_GRAY));
        
        for (StoneType type : StoneType.values()) {
            boolean present = stones.getOrDefault(type, false);
            hasAnyStone |= present;
            
            if (present) {
                tooltip.add(new StringTextComponent(" ✓ " + type.getDisplayName()).mergeStyle(getColorForType(type)));
            } else {
                tooltip.add(new StringTextComponent(" ✗ " + type.getDisplayName()).mergeStyle(TextFormatting.DARK_GRAY));
            }
        }
        
        if (hasAnyStone) {
            tooltip.add(new StringTextComponent("Right-click to use the gauntlet power").mergeStyle(TextFormatting.GOLD));
        } else {
            tooltip.add(new StringTextComponent("Add Infinity Stones to unlock powers").mergeStyle(TextFormatting.DARK_GRAY));
        }
        
        super.addInformation(stack, world, tooltip, flag);
    }
    
    /**
     * Gets the color formatting for the stone type
     *
     * @param type The stone type
     * @return The color formatting
     */
    private TextFormatting getColorForType(StoneType type) {
        switch (type) {
            case SPACE:
                return TextFormatting.BLUE;
            case MIND:
                return TextFormatting.YELLOW;
            case REALITY:
                return TextFormatting.RED;
            case POWER:
                return TextFormatting.DARK_PURPLE;
            case TIME:
                return TextFormatting.GREEN;
            case SOUL:
                return TextFormatting.GOLD;
            default:
                return TextFormatting.WHITE;
        }
    }
}