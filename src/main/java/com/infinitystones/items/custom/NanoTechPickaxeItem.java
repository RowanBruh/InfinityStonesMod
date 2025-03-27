package com.infinitystones.items.custom;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A high-tech pickaxe with enhanced mining abilities.
 * The infected variant mines faster but may cause negative effects to the wielder.
 */
public class NanoTechPickaxeItem extends PickaxeItem {
    private final boolean isInfected;
    
    public NanoTechPickaxeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties, boolean isInfected) {
        super(tier, attackDamage, attackSpeed, properties);
        this.isInfected = isInfected;
    }
    
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        if (baseSpeed > 1.0F) {
            // Regular nano tech pickaxe gets a 30% mining speed boost
            if (!isInfected) {
                return baseSpeed * 1.3F;
            } 
            // Infected version gets a 60% boost
            else {
                return baseSpeed * 1.6F;
            }
        }
        return baseSpeed;
    }
    
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide) {
            // Regular nano tech pickaxe gives a short haste boost
            if (!isInfected) {
                miner.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 100, 0));
            } 
            // Infected version gives longer haste boost but may harm user
            else {
                miner.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 160, 1));
                
                // 10% chance to apply mining fatigue after the effect
                if (miner.getRandom().nextFloat() < 0.1f) {
                    miner.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0));
                }
            }
            
            // Reduce durability damage for non-infected tools by 20%
            if (!isInfected && miner.getRandom().nextFloat() < 0.2f) {
                stack.hurtAndBreak(0, miner, (entity) -> {
                    entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
                return true;
            }
        }
        
        return super.mineBlock(stack, level, state, pos, miner);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        if (isInfected) {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_nano_pickaxe.desc")
                .withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_warning")
                .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.nano_pickaxe.desc")
                .withStyle(ChatFormatting.AQUA));
        }
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Add enchantment glint effect to infected tools
        return isInfected || super.isFoil(stack);
    }
}