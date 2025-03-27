package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import com.infinitystones.util.CombinedStoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
import java.util.Random;

/**
 * The Infected version of the Infinity Gauntlet with extreme power but severe side effects
 */
public class InfectedInfinityGauntlet extends Item {
    
    private static final Random RANDOM = new Random();
    
    /**
     * Constructor for the Infected Infinity Gauntlet
     */
    public InfectedInfinityGauntlet() {
        super(new Item.Properties()
                .group(ModItemGroups.ROWAN_INDUSTRIES)
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
            // Check if we have any stones
            Map<StoneType, Boolean> stones = getStonesInGauntlet(stack);
            int stoneCount = countStonesInGauntlet(stones);
            
            if (stoneCount == 0) {
                player.sendMessage(new StringTextComponent("§5The Infected Infinity Gauntlet has no stones equipped."), 
                        player.getUniqueID());
                return ActionResult.resultSuccess(stack);
            }
            
            // Activate the ultimate power - using a custom version of the combined abilities
            activateInfectedGauntletPower(player, world, stoneCount);
            
            // Apply cooldown - shorter for higher risk/reward
            player.getCooldownTracker().setCooldown(this, 300); // 15 seconds cooldown
        } else {
            // Client-side GUI would go here in a full implementation
            System.out.println("Opening Infected Infinity Gauntlet GUI");
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Activates the infected gauntlet power
     *
     * @param player The player
     * @param world The world
     * @param stoneCount The number of stones in the gauntlet
     */
    private void activateInfectedGauntletPower(PlayerEntity player, World world, int stoneCount) {
        // First, activate a stronger version of the combined abilities
        CombinedStoneAbilities.activateCombinedAbilities(player, world);
        
        // Add additional extreme effects based on stone count
        if (stoneCount >= 3) {
            // Add flight-like abilities
            player.addPotionEffect(new EffectInstance(Effects.LEVITATION, 100, 0));
            player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 300, 0));
        }
        
        if (stoneCount >= 5) {
            // Add temporary invulnerability
            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 4)); // 100% damage reduction
        }
        
        // If we have all 6 stones, add the most powerful effect
        if (stoneCount == 6) {
            player.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 200, 0));
            player.sendMessage(new StringTextComponent("§5§lULTIMATE POWER ACTIVATED!"), player.getUniqueID());
            player.sendMessage(new StringTextComponent("§5§lThe infection grows stronger with every use..."), player.getUniqueID());
        } else {
            player.sendMessage(new StringTextComponent("§5Infected power activated with " + stoneCount + "/6 stones."), 
                    player.getUniqueID());
        }
        
        // Now apply the negative side effects - more severe than individual stones
        applyInfectedGauntletSideEffects(player, stoneCount);
    }
    
    /**
     * Applies side effects from using the infected gauntlet
     *
     * @param player The player
     * @param stoneCount The number of stones in the gauntlet
     */
    private void applyInfectedGauntletSideEffects(PlayerEntity player, int stoneCount) {
        // Guaranteed minor negative effect
        player.addPotionEffect(new EffectInstance(Effects.HUNGER, 400, 2));
        player.sendMessage(new StringTextComponent("§8Side effect: You feel starving"), player.getUniqueID());
        
        // 80% chance of medium negative effect - higher than individual stones
        if (RANDOM.nextFloat() < 0.8f) {
            // Apply a random medium negative effect
            switch (RANDOM.nextInt(3)) {
                case 0:
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1));
                    player.sendMessage(new StringTextComponent("§8Side effect: Your movement is significantly slowed"), 
                            player.getUniqueID());
                    break;
                case 1:
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 300, 2));
                    player.sendMessage(new StringTextComponent("§8Side effect: Your arms feel extremely heavy"), 
                            player.getUniqueID());
                    break;
                case 2:
                    player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 300, 1));
                    player.sendMessage(new StringTextComponent("§8Side effect: Your strength is sapped"), 
                            player.getUniqueID());
                    break;
            }
        }
        
        // Chance of severe negative effect scales with stone count
        // 10% per stone (60% with all stones)
        float severeChance = 0.1f * stoneCount;
        if (RANDOM.nextFloat() < severeChance) {
            // Apply a random severe negative effect
            switch (RANDOM.nextInt(3)) {
                case 0:
                    player.addPotionEffect(new EffectInstance(Effects.POISON, 300, 2));
                    player.sendMessage(new StringTextComponent("§4Severe side effect: The infection spreads rapidly through your body"), 
                            player.getUniqueID());
                    break;
                case 1:
                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 200, 0));
                    player.sendMessage(new StringTextComponent("§4Severe side effect: Your vision is consumed by darkness"), 
                            player.getUniqueID());
                    break;
                case 2:
                    player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 300, 0));
                    player.sendMessage(new StringTextComponent("§4Severe side effect: Your mind spirals into chaos"), 
                            player.getUniqueID());
                    break;
            }
        }
        
        // Critical effect chance increases with stone count
        // 3% per stone (18% with all stones)
        float criticalChance = 0.03f * stoneCount;
        if (RANDOM.nextFloat() < criticalChance) {
            player.addPotionEffect(new EffectInstance(Effects.WITHER, 200, 2));
            player.sendMessage(new StringTextComponent("§4§lCRITICAL: The infection is consuming your life force!"), 
                    player.getUniqueID());
        }
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
     * Gets all stones in the gauntlet
     *
     * @param stack The gauntlet item stack
     * @return A map of stone types to their presence (true if present, false if not)
     */
    public Map<StoneType, Boolean> getStonesInGauntlet(ItemStack stack) {
        Map<StoneType, Boolean> stones = new HashMap<>();
        CompoundNBT nbt = stack.getOrCreateTag();
        
        for (StoneType type : StoneType.values()) {
            stones.put(type, nbt.getBoolean("infected_" + type.getId() + "_stone"));
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
        nbt.putBoolean("infected_" + type.getId() + "_stone", present);
    }
    
    /**
     * Adds information to the tooltip
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("Infected Infinity Gauntlet")
                .mergeStyle(TextFormatting.DARK_PURPLE));
        
        tooltip.add(new StringTextComponent("A corrupted gauntlet with immense but unpredictable power")
                .mergeStyle(TextFormatting.GRAY));
        
        // Show equipped stones
        Map<StoneType, Boolean> stones = getStonesInGauntlet(stack);
        int stoneCount = countStonesInGauntlet(stones);
        
        tooltip.add(new StringTextComponent("Equipped Infected Stones (" + stoneCount + "/6):").mergeStyle(TextFormatting.DARK_GRAY));
        
        for (StoneType type : StoneType.values()) {
            boolean present = stones.getOrDefault(type, false);
            
            if (present) {
                tooltip.add(new StringTextComponent(" ✓ Infected " + type.getDisplayName()).mergeStyle(TextFormatting.DARK_PURPLE));
            } else {
                tooltip.add(new StringTextComponent(" ✗ Infected " + type.getDisplayName()).mergeStyle(TextFormatting.DARK_GRAY));
            }
        }
        
        // Usage and warning
        if (stoneCount > 0) {
            tooltip.add(new StringTextComponent("Right-click to use the infected power").mergeStyle(TextFormatting.DARK_PURPLE));
            tooltip.add(new StringTextComponent("§c§lWARNING: §r§cExtreme side effects likely!").mergeStyle(TextFormatting.RED));
        } else {
            tooltip.add(new StringTextComponent("Add Infected Infinity Stones to unlock powers").mergeStyle(TextFormatting.DARK_GRAY));
        }
        
        super.addInformation(stack, world, tooltip, flag);
    }
}