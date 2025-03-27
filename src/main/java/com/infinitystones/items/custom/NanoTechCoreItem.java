package com.infinitystones.items.custom;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Represents a Nano Tech Core item, which is used to craft Nano Tech equipment.
 * The infected variant has better stats but can cause negative effects.
 */
public class NanoTechCoreItem extends Item {
    private final boolean isInfected;

    public NanoTechCoreItem(Properties properties, boolean isInfected) {
        super(properties);
        this.isInfected = isInfected;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        if (isInfected) {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_nano_core.desc")
                .withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.literal("").withStyle(ChatFormatting.DARK_RED));
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_warning")
                .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.nano_core.desc")
                .withStyle(ChatFormatting.AQUA));
        }
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    public boolean isInfected() {
        return isInfected;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Add enchantment glint effect to infected cores
        return isInfected || super.isFoil(stack);
    }
}