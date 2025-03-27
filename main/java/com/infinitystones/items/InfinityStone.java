package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import com.infinitystones.util.StoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents an Infinity Stone item
 */
public class InfinityStone extends Item {
    
    private final StoneType type;
    
    /**
     * Constructor for the Infinity Stone
     *
     * @param type The type of stone
     */
    public InfinityStone(StoneType type) {
        super(new Item.Properties()
                .group(ModItemGroups.INFINITY_STONES)
                .maxStackSize(1)
                .rarity(net.minecraft.item.Rarity.EPIC));
        this.type = type;
    }
    
    /**
     * Gets the type of stone
     *
     * @return The stone type
     */
    public StoneType getType() {
        return type;
    }
    
    /**
     * Called when the item is right-clicked
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            // Activate the stone's ability
            StoneAbilities.activateStoneAbility(type, player, world);
            
            // Apply cooldown
            player.getCooldownTracker().setCooldown(this, 200); // 10 seconds cooldown
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Adds information to the tooltip
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("The " + type.getDisplayName())
                .mergeStyle(getColorForType()));
        
        switch (type) {
            case SPACE:
                tooltip.add(new StringTextComponent("Controls space and teleportation")
                        .mergeStyle(TextFormatting.GRAY));
                break;
            case MIND:
                tooltip.add(new StringTextComponent("Enhances mental abilities")
                        .mergeStyle(TextFormatting.GRAY));
                break;
            case REALITY:
                tooltip.add(new StringTextComponent("Alters reality")
                        .mergeStyle(TextFormatting.GRAY));
                break;
            case POWER:
                tooltip.add(new StringTextComponent("Grants immense power")
                        .mergeStyle(TextFormatting.GRAY));
                break;
            case TIME:
                tooltip.add(new StringTextComponent("Manipulates time")
                        .mergeStyle(TextFormatting.GRAY));
                break;
            case SOUL:
                tooltip.add(new StringTextComponent("Controls the essence of life")
                        .mergeStyle(TextFormatting.GRAY));
                break;
        }
        
        tooltip.add(new StringTextComponent("Right-click to use stone power")
                .mergeStyle(TextFormatting.DARK_GRAY));
        
        super.addInformation(stack, world, tooltip, flag);
    }
    
    /**
     * Gets the color formatting for the stone type
     *
     * @return The color formatting
     */
    private TextFormatting getColorForType() {
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