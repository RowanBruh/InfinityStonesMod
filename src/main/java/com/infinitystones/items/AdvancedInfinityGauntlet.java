package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityStones.InfinityStoneItem;
import com.infinitystones.items.InfinityStones.StoneType;
import com.infinitystones.util.CombinedStoneAbilities;
import com.infinitystones.util.StoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Advanced Infinity Gauntlet that allows for combining the powers of multiple stones
 */
public class AdvancedInfinityGauntlet extends Item {
    private static final String STONES_TAG = "Stones";
    private static final String ACTIVE_MODE_TAG = "ActiveMode";
    
    // Modes for the gauntlet
    public enum GauntletMode {
        INDIVIDUAL, // Use each stone's abilities separately
        COMBINED    // Combine stone powers for enhanced effects
    }
    
    public AdvancedInfinityGauntlet() {
        super(new Item.Properties()
                .group(InfinityStonesMod.INFINITY_GROUP)
                .maxStackSize(1)
                .setNoRepair()
                .rarity(Rarity.EPIC));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        CompoundNBT tag = stack.getOrCreateTag();
        
        // Check if player is sneaking
        if (player.isSneaking()) {
            // Toggle gauntlet mode
            boolean isComboMode = tag.getBoolean(ACTIVE_MODE_TAG);
            tag.putBoolean(ACTIVE_MODE_TAG, !isComboMode);
            
            if (!world.isRemote) {
                String modeText = !isComboMode ? "Combined Powers Mode" : "Individual Stone Mode";
                TextFormatting color = !isComboMode ? TextFormatting.GOLD : TextFormatting.AQUA;
                player.sendStatusMessage(new StringTextComponent("Advanced Gauntlet: " + modeText)
                        .mergeStyle(color, TextFormatting.BOLD), true);
            }
            
            return ActionResult.resultSuccess(stack);
        }
        
        // Get gauntlet mode
        boolean isComboMode = tag.getBoolean(ACTIVE_MODE_TAG);
        
        // Get installed stones
        Set<StoneType> installedStones = getInstalledStones(stack);
        
        if (installedStones.isEmpty()) {
            if (!world.isRemote) {
                player.sendStatusMessage(new StringTextComponent("No Infinity Stones installed")
                        .mergeStyle(TextFormatting.RED), true);
            }
            return ActionResult.resultFail(stack);
        }
        
        if (isComboMode) {
            // Combined powers mode - use all stones at once
            if (!world.isRemote) {
                CombinedStoneAbilities.executeCombinedPower(world, player, installedStones, true);
            }
        } else {
            // Individual mode
            // Find the first stone and use its ability
            StoneType firstStone = installedStones.iterator().next();
            if (!world.isRemote) {
                StoneAbilities.executeStoneAbility(world, player, firstStone, true);
            }
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Gets the set of stone types currently installed in the gauntlet
     */
    public Set<StoneType> getInstalledStones(ItemStack stack) {
        Set<StoneType> stones = EnumSet.noneOf(StoneType.class);
        
        IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (handler == null) return stones;
        
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stoneStack = handler.getStackInSlot(i);
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStoneItem) {
                InfinityStoneItem stoneItem = (InfinityStoneItem) stoneStack.getItem();
                stones.add(stoneItem.getStoneType());
            }
        }
        
        return stones;
    }
    
    /**
     * Checks if the gauntlet has a specific stone installed
     */
    public boolean hasStone(ItemStack stack, StoneType stoneType) {
        return getInstalledStones(stack).contains(stoneType);
    }
    
    /**
     * Capability provider for the gauntlet's stone slots
     */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new AdvancedGauntletCapabilityProvider(stack);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        
        if (!(entityIn instanceof PlayerEntity)) return;
        
        // Check if being held or worn in offhand
        boolean isEquipped = isSelected || 
                ((PlayerEntity)entityIn).getHeldItemOffhand() == stack ||
                entityIn.getItemStackFromSlot(EquipmentSlotType.HEAD) == stack;
        
        if (isEquipped) {
            // Passive effects from stones could be applied here
            PlayerEntity player = (PlayerEntity) entityIn;
            
            // Apply effects based on installed stones
            Set<StoneType> installedStones = getInstalledStones(stack);
            for (StoneType stone : installedStones) {
                // Apply passive effects for each stone
                // This is simpler than individual gauntlet as we just care about effects
                switch (stone) {
                    case POWER:
                        // Improved attack damage
                        break;
                    case MIND:
                        // Enhanced XP gain
                        break;
                    case SPACE:
                        // Prevention of fall damage
                        player.fallDistance = 0.0F;
                        break;
                    case REALITY:
                        // Improved mining speed
                        break;
                    case TIME:
                        // Movement speed boost
                        break;
                    case SOUL:
                        // Health regeneration
                        if (worldIn.getGameTime() % 80 == 0 && player.getHealth() < player.getMaxHealth()) {
                            player.heal(0.5F);
                        }
                        break;
                }
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        tooltip.add(new StringTextComponent("Advanced Infinity Gauntlet").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD));
        tooltip.add(new StringTextComponent(""));
        
        // Display installed stones
        Set<StoneType> stones = getInstalledStones(stack);
        
        if (stones.isEmpty()) {
            tooltip.add(new StringTextComponent("No Infinity Stones installed").mergeStyle(TextFormatting.GRAY));
        } else {
            tooltip.add(new StringTextComponent("Installed Stones:").mergeStyle(TextFormatting.YELLOW));
            for (StoneType stone : stones) {
                tooltip.add(new StringTextComponent(" - " + stone.getName()).mergeStyle(stone.getColor()));
            }
        }
        
        tooltip.add(new StringTextComponent(""));
        
        // Show current mode
        CompoundNBT tag = stack.getOrCreateTag();
        boolean isComboMode = tag.getBoolean(ACTIVE_MODE_TAG);
        String modeText = isComboMode ? "Combined Powers Mode" : "Individual Stone Mode";
        TextFormatting color = isComboMode ? TextFormatting.GOLD : TextFormatting.AQUA;
        tooltip.add(new StringTextComponent("Mode: " + modeText).mergeStyle(color));
        
        // Usage instructions
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new StringTextComponent("Right-click: Use stone abilities").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent("Sneak + Right-click: Switch modes").mergeStyle(TextFormatting.GRAY));
    }
    
    /**
     * Advanced Gauntlet Capability Provider
     */
    private static class AdvancedGauntletCapabilityProvider extends InfinityGauntlet.GauntletCapabilityProvider {
        public AdvancedGauntletCapabilityProvider(ItemStack stack) {
            super(stack);
        }
    }
}