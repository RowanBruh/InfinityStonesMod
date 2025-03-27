package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Nano Tech Sword with special abilities
 */
public class NanoTechSword extends SwordItem {
    
    private static final Random RANDOM = new Random();
    
    // Custom item tier for Nano Tech Sword
    private static final IItemTier NANO_TECH_TIER = new ItemTier("nano_tech",
            4, // Harvest level
            2000, // Max uses
            12.0F, // Efficiency
            6.0F, // Attack damage
            20, // Enchantability
            () -> Ingredient.fromItems(ModItems.NANO_TECH_CORE.get()));
    
    /**
     * Constructor for the Nano Tech Sword
     */
    public NanoTechSword() {
        super(NANO_TECH_TIER, 3, -2.2F, 
                new Properties().group(ModItemGroups.ROWAN_INDUSTRIES).maxStackSize(1));
    }
    
    /**
     * Called when an entity is hit with the sword
     */
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Apply special effects on hit
        if (RANDOM.nextFloat() < 0.4f) { // 40% chance
            // Apply a random negative effect to the target
            applyRandomNegativeEffect(target);
        }
        
        // If the attacker is a player, give them a brief boost
        if (attacker instanceof PlayerEntity) {
            // Apply a small temporary buff to the attacker
            PlayerEntity player = (PlayerEntity) attacker;
            player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 60, 0));
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    /**
     * Applies a random negative effect to the target
     *
     * @param target The target entity
     */
    private void applyRandomNegativeEffect(LivingEntity target) {
        switch (RANDOM.nextInt(5)) {
            case 0:
                target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 1));
                break;
            case 1:
                target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 1));
                break;
            case 2:
                target.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 60, 0));
                break;
            case 3:
                target.addPotionEffect(new EffectInstance(Effects.POISON, 80, 0));
                break;
            case 4:
                // Levitation can be a fun combat effect, lifting enemies off the ground
                target.addPotionEffect(new EffectInstance(Effects.LEVITATION, 40, 0));
                break;
        }
    }
    
    /**
     * Adds information to the tooltip
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("Nano Tech Sword")
                .mergeStyle(TextFormatting.AQUA));
        
        tooltip.add(new StringTextComponent("A high-tech blade with molecular disruption capabilities")
                .mergeStyle(TextFormatting.GRAY));
        
        tooltip.add(new StringTextComponent("• 40% chance to apply a random debuff on hit")
                .mergeStyle(TextFormatting.GREEN));
        
        tooltip.add(new StringTextComponent("• Grants brief strength boost when hitting enemies")
                .mergeStyle(TextFormatting.GREEN));
        
        super.addInformation(stack, world, tooltip, flag);
    }
    
    /**
     * Custom item tier implementation
     */
    private static class ItemTier implements IItemTier {
        private final String name;
        private final int harvestLevel;
        private final int maxUses;
        private final float efficiency;
        private final float attackDamage;
        private final int enchantability;
        private final java.util.function.Supplier<Ingredient> repairMaterial;
        
        public ItemTier(String name, int harvestLevel, int maxUses, float efficiency, 
                       float attackDamage, int enchantability, 
                       java.util.function.Supplier<Ingredient> repairMaterial) {
            this.name = name;
            this.harvestLevel = harvestLevel;
            this.maxUses = maxUses;
            this.efficiency = efficiency;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
            this.repairMaterial = repairMaterial;
        }
        
        @Override
        public int getMaxUses() {
            return this.maxUses;
        }
        
        @Override
        public float getEfficiency() {
            return this.efficiency;
        }
        
        @Override
        public float getAttackDamage() {
            return this.attackDamage;
        }
        
        @Override
        public int getHarvestLevel() {
            return this.harvestLevel;
        }
        
        @Override
        public int getEnchantability() {
            return this.enchantability;
        }
        
        @Override
        public Ingredient getRepairMaterial() {
            return this.repairMaterial.get();
        }
    }
}