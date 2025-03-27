package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import com.infinitystones.util.CombinedStoneAbilities;
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
 * The Advanced Infinity Gauntlet with enhanced powers and modes
 */
public class AdvancedInfinityGauntlet extends Item {
    
    // Mode constants
    public static final String MODE_INDIVIDUAL = "individual";
    public static final String MODE_COMBINED = "combined";
    
    /**
     * Constructor for the Advanced Infinity Gauntlet
     */
    public AdvancedInfinityGauntlet() {
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
        
        if (player.isSneaking()) {
            // Shift+right-click toggles the mode
            if (!world.isRemote) {
                toggleMode(stack);
                String mode = getMode(stack);
                player.sendMessage(new StringTextComponent("Advanced Infinity Gauntlet mode: " + mode), player.getUniqueID());
            }
        } else {
            // Regular right-click activates the gauntlet
            if (!world.isRemote) {
                String mode = getMode(stack);
                
                // Check if we have all the stones
                Map<StoneType, Boolean> stones = getStonesInGauntlet(stack);
                int stoneCount = countStonesInGauntlet(stones);
                
                if (stoneCount == 0) {
                    player.sendMessage(new StringTextComponent("The Advanced Infinity Gauntlet has no stones equipped."), 
                            player.getUniqueID());
                    return ActionResult.resultSuccess(stack);
                }
                
                // Activate based on mode
                if (MODE_COMBINED.equals(mode)) {
                    // In combined mode, we need all 6 stones
                    if (stoneCount == 6) {
                        CombinedStoneAbilities.activateCombinedAbilities(player, world);
                        
                        // Apply cooldown
                        player.getCooldownTracker().setCooldown(this, 400); // 20 seconds cooldown
                    } else {
                        player.sendMessage(new StringTextComponent("Combined mode requires all 6 stones!"), 
                                player.getUniqueID());
                    }
                } else {
                    // Individual mode - just use the first stone
                    StoneType activeStone = getFirstStoneInGauntlet(stack);
                    if (activeStone != null) {
                        StoneAbilities.activateStoneAbility(activeStone, player, world);
                        
                        // Apply cooldown
                        player.getCooldownTracker().setCooldown(this, 100); // 5 seconds cooldown (faster than regular gauntlet)
                    }
                }
            } else {
                // Client-side GUI would go here in a full implementation
                System.out.println("Opening Advanced Infinity Gauntlet GUI");
            }
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Gets the current mode of the gauntlet
     *
     * @param stack The gauntlet item stack
     * @return The mode (individual or combined)
     */
    public String getMode(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (!nbt.contains("mode")) {
            nbt.putString("mode", MODE_INDIVIDUAL); // Default mode
        }
        return nbt.getString("mode");
    }
    
    /**
     * Toggles between individual and combined modes
     *
     * @param stack The gauntlet item stack
     */
    public void toggleMode(ItemStack stack) {
        String currentMode = getMode(stack);
        String newMode = MODE_INDIVIDUAL.equals(currentMode) ? MODE_COMBINED : MODE_INDIVIDUAL;
        stack.getOrCreateTag().putString("mode", newMode);
    }
    
    /**
     * Counts how many stones are in the gauntlet
     *
     * @param stones Map of stones to their presence
     * @return The number of stones
     */
    private int countStonesInGauntlet(Map<StoneType, Boolean> stones) {
        int count = 0;
        for (Boolean present : stones.values()) {
            if (present) {
                count++;
            }
        }
        return count;
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
        tooltip.add(new StringTextComponent("The Advanced Infinity Gauntlet")
                .mergeStyle(TextFormatting.GOLD));
        
        tooltip.add(new StringTextComponent("An upgraded gauntlet with enhanced abilities and modes")
                .mergeStyle(TextFormatting.GRAY));
        
        // Show current mode
        String mode = getMode(stack);
        if (MODE_COMBINED.equals(mode)) {
            tooltip.add(new StringTextComponent("Mode: COMBINED (use all stones at once)")
                    .mergeStyle(TextFormatting.RED));
        } else {
            tooltip.add(new StringTextComponent("Mode: INDIVIDUAL (use stones separately)")
                    .mergeStyle(TextFormatting.AQUA));
        }
        
        // Show equipped stones
        Map<StoneType, Boolean> stones = getStonesInGauntlet(stack);
        int stoneCount = countStonesInGauntlet(stones);
        
        tooltip.add(new StringTextComponent("Equipped Stones (" + stoneCount + "/6):").mergeStyle(TextFormatting.DARK_GRAY));
        
        for (StoneType type : StoneType.values()) {
            boolean present = stones.getOrDefault(type, false);
            
            if (present) {
                tooltip.add(new StringTextComponent(" ✓ " + type.getDisplayName()).mergeStyle(getColorForType(type)));
            } else {
                tooltip.add(new StringTextComponent(" ✗ " + type.getDisplayName()).mergeStyle(TextFormatting.DARK_GRAY));
            }
        }
        
        // Usage instructions
        if (stoneCount > 0) {
            tooltip.add(new StringTextComponent("Right-click to use the gauntlet power").mergeStyle(TextFormatting.GOLD));
        } else {
            tooltip.add(new StringTextComponent("Add Infinity Stones to unlock powers").mergeStyle(TextFormatting.DARK_GRAY));
        }
        
        tooltip.add(new StringTextComponent("Shift+right-click to toggle mode").mergeStyle(TextFormatting.DARK_GRAY));
        
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