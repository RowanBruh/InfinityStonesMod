package com.infinitystones.items;

import java.util.List;
import java.util.function.Supplier;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import com.infinitystones.util.StoneAbilities;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InfinityStones {
    public enum StoneType {
        SPACE("space_stone", TextFormatting.BLUE),
        MIND("mind_stone", TextFormatting.YELLOW),
        REALITY("reality_stone", TextFormatting.RED),
        POWER("power_stone", TextFormatting.PURPLE),
        TIME("time_stone", TextFormatting.GREEN),
        SOUL("soul_stone", TextFormatting.GOLD);
        
        private final String registryName;
        private final TextFormatting color;
        
        StoneType(String registryName, TextFormatting color) {
            this.registryName = registryName;
            this.color = color;
        }
        
        public String getRegistryName() {
            return registryName;
        }
        
        public TextFormatting getColor() {
            return color;
        }
    }
    
    public static class InfinityStoneItem extends Item {
        private final StoneType type;
        
        public InfinityStoneItem(StoneType type) {
            super(new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .maxStackSize(1)
                    .setNoRepair());
            this.type = type;
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            
            if (!worldIn.isRemote) {
                // Execute the stone's ability
                switch (type) {
                    case SPACE:
                        StoneAbilities.activateSpaceStone(playerIn, worldIn);
                        break;
                    case MIND:
                        StoneAbilities.activateMindStone(playerIn, worldIn);
                        break;
                    case REALITY:
                        StoneAbilities.activateRealityStone(playerIn, worldIn);
                        break;
                    case POWER:
                        StoneAbilities.activatePowerStone(playerIn, worldIn);
                        break;
                    case TIME:
                        StoneAbilities.activateTimeStone(playerIn, worldIn);
                        break;
                    case SOUL:
                        StoneAbilities.activateSoulStone(playerIn, worldIn);
                        break;
                }
                
                // Add cooldown
                playerIn.getCooldownTracker().setCooldown(this, 100);
            }
            
            return ActionResult.resultSuccess(stack);
        }
        
        @Override
        public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            if (entityIn instanceof PlayerEntity && !worldIn.isRemote) {
                PlayerEntity player = (PlayerEntity) entityIn;
                
                // Apply passive effects based on stone type
                if (type == StoneType.POWER) {
                    // Passive strength when held
                    if (isSelected) {
                        StoneAbilities.applyPowerStonePassive(player);
                    }
                } else if (type == StoneType.SOUL) {
                    // Passive regeneration when held
                    if (isSelected) {
                        StoneAbilities.applySoulStonePassive(player);
                    }
                }
            }
            
            super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Infinity Stone: " + type.name()).mergeStyle(type.getColor()));
            
            switch (type) {
                case SPACE:
                    tooltip.add(new StringTextComponent("Right-click to teleport in the direction you're looking"));
                    tooltip.add(new StringTextComponent("Range: " + 
                            ModConfig.COMMON_CONFIG.spaceStonePowerRadius.get() + " blocks"));
                    break;
                case MIND:
                    tooltip.add(new StringTextComponent("Right-click to control nearby mobs"));
                    tooltip.add(new StringTextComponent("Duration: " + 
                            ModConfig.COMMON_CONFIG.mindStoneControlDuration.get() + " seconds"));
                    break;
                case REALITY:
                    tooltip.add(new StringTextComponent("Right-click to alter reality and create random blocks"));
                    tooltip.add(new StringTextComponent("Power: " + 
                            ModConfig.COMMON_CONFIG.realityStonePowerMultiplier.get() + "x multiplier"));
                    break;
                case POWER:
                    tooltip.add(new StringTextComponent("Right-click to unleash destructive energy"));
                    tooltip.add(new StringTextComponent("Damage: " + 
                            ModConfig.COMMON_CONFIG.powerStoneDamageMultiplier.get() + "x multiplier"));
                    tooltip.add(new StringTextComponent("Passive: Strength boost while held"));
                    break;
                case TIME:
                    tooltip.add(new StringTextComponent("Right-click to slow down time around you"));
                    tooltip.add(new StringTextComponent("Duration: " + 
                            ModConfig.COMMON_CONFIG.timeStoneEffectDuration.get() + " seconds"));
                    break;
                case SOUL:
                    tooltip.add(new StringTextComponent("Right-click to steal life from nearby entities"));
                    tooltip.add(new StringTextComponent("Life Steal: " + 
                            ModConfig.COMMON_CONFIG.soulStoneLifeStealAmount.get() + " hearts"));
                    tooltip.add(new StringTextComponent("Passive: Slow regeneration while held"));
                    break;
            }
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
        
        public StoneType getStoneType() {
            return type;
        }
    }
}
