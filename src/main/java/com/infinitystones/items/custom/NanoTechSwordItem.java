package com.infinitystones.items.custom;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A high-tech sword with enhanced abilities.
 * The infected variant deals more damage but may cause negative effects to the wielder.
 */
public class NanoTechSwordItem extends SwordItem {
    private final boolean isInfected;
    
    public NanoTechSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties, boolean isInfected) {
        super(tier, attackDamage, attackSpeed, properties);
        this.isInfected = isInfected;
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Regular nano tech sword gives a short strength boost
        if (!isInfected) {
            attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0));
        } 
        // Infected version gives longer strength boost but may harm user
        else {
            attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1));
            
            // 15% chance to apply wither to the user
            if (attacker.getRandom().nextFloat() < 0.15f) {
                attacker.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
            }
        }
        
        return super.hurtEnemy(stack, target, attacker);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        if (isInfected) {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_nano_sword.desc")
                .withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_warning")
                .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.nano_sword.desc")
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