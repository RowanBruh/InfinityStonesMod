package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Implements the Infected Nano Tech items from Insane Craft
 */
public class NanoTechItems {
    private static final Random random = new Random();
    
    /**
     * Nano Tech Core - crafting component and activator
     */
    public static class NanoTechCore extends Item {
        public NanoTechCore() {
            super(new Item.Properties()
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES)
                    .maxStackSize(16)
                    .rarity(Rarity.RARE));
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            
            if (!worldIn.isRemote) {
                // Gives nausea and slowness briefly - it's infected tech!
                playerIn.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 0));
                playerIn.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 0));
                
                // But also grants temporary strength
                playerIn.addPotionEffect(new EffectInstance(Effects.STRENGTH, 300, 1));
                
                // Play sound effect
                worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.5F, 0.8F);
                
                // Consume one core
                if (!playerIn.abilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            
            return ActionResult.resultSuccess(stack);
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("A corrupted nano-technology core").mergeStyle(TextFormatting.DARK_PURPLE));
            tooltip.add(new StringTextComponent("Right-click to gain temporary strength").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent("Warning: Side effects include nausea").mergeStyle(TextFormatting.RED));
        }
    }
    
    /**
     * Nano Tech Armor base class
     */
    public static class NanoTechArmorItem extends ArmorItem {
        public NanoTechArmorItem(EquipmentSlotType slot) {
            super(new NanoTechArmorMaterial(),
                  slot,
                  new Item.Properties()
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES)
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC));
        }
        
        @Override
        public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
            super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
            
            // Check if player is wearing armor
            if (entityIn instanceof PlayerEntity && !worldIn.isRemote) {
                PlayerEntity player = (PlayerEntity) entityIn;
                
                if (isWearingFullNanoTechSet(player)) {
                    // Full set bonus - apply every 5 seconds
                    if (worldIn.getGameTime() % 100 == 0) {
                        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 120, 1, false, false));
                        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 120, 0, false, false));
                        
                        // Infected tech occasionally causes damage
                        if (random.nextFloat() < 0.05f) {
                            player.attackEntityFrom(NanoTechDamageSource.INFECTION, 1.0f);
                            player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 80, 0));
                        }
                    }
                }
            }
        }
        
        private boolean isWearingFullNanoTechSet(PlayerEntity player) {
            ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
            ItemStack chest = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            ItemStack legs = player.getItemStackFromSlot(EquipmentSlotType.LEGS);
            ItemStack feet = player.getItemStackFromSlot(EquipmentSlotType.FEET);
            
            return helmet.getItem() instanceof NanoTechArmorItem &&
                   chest.getItem() instanceof NanoTechArmorItem &&
                   legs.getItem() instanceof NanoTechArmorItem &&
                   feet.getItem() instanceof NanoTechArmorItem;
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Infected Nano Tech Armor").mergeStyle(TextFormatting.DARK_PURPLE));
            tooltip.add(new StringTextComponent("Full set: Resistance II, Strength I").mergeStyle(TextFormatting.BLUE));
            tooltip.add(new StringTextComponent("Warning: May cause infection damage").mergeStyle(TextFormatting.RED));
        }
    }
    
    /**
     * Nano Tech custom sword
     */
    public static class NanoTechSword extends SwordItem {
        public NanoTechSword() {
            super(ItemTier.NETHERITE, 
                  8, // 8 damage + 4 base = 12 attack damage
                  -2.0f, // slightly faster attack speed
                  new Item.Properties()
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES)
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC));
        }
        
        @Override
        public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // Apply special infected damage effects
            if (random.nextFloat() < 0.3f) {
                target.addPotionEffect(new EffectInstance(Effects.POISON, 100, 1));
            }
            
            if (random.nextFloat() < 0.2f) {
                target.addPotionEffect(new EffectInstance(Effects.WITHER, 60, 0));
            }
            
            // Infected tech occasionally causes damage to user
            if (random.nextFloat() < 0.05f && attacker instanceof PlayerEntity) {
                attacker.attackEntityFrom(NanoTechDamageSource.INFECTION, 1.0f);
                attacker.addPotionEffect(new EffectInstance(Effects.NAUSEA, 60, 0));
            }
            
            return super.hitEntity(stack, target, attacker);
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Infected Nano Tech Sword").mergeStyle(TextFormatting.DARK_PURPLE));
            tooltip.add(new StringTextComponent("30% chance to poison enemies").mergeStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent("20% chance to wither enemies").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent("Warning: May cause infection damage").mergeStyle(TextFormatting.RED));
        }
    }
    
    /**
     * Custom armor material for Nano Tech
     */
    public static class NanoTechArmorMaterial implements IArmorMaterial {
        private static final int[] DURABILITY = new int[]{480, 560, 600, 420};
        private static final int[] PROTECTION = new int[]{4, 7, 9, 4};
        
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return DURABILITY[slotIn.getIndex()];
        }
        
        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return PROTECTION[slotIn.getIndex()];
        }
        
        @Override
        public int getEnchantability() {
            return 15;
        }
        
        @Override
        public net.minecraft.util.SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
        }
        
        @Override
        public net.minecraft.item.crafting.Ingredient getRepairMaterial() {
            return net.minecraft.item.crafting.Ingredient.fromItems(ModItems.NANO_TECH_CORE.get());
        }
        
        @Override
        public String getName() {
            return "infinitystones:nano_tech";
        }
        
        @Override
        public float getToughness() {
            return 3.0F;
        }
        
        @Override
        public float getKnockbackResistance() {
            return 0.1F;
        }
    }
    
    /**
     * Custom damage source for nano tech infection
     */
    public static class NanoTechDamageSource extends net.minecraft.util.DamageSource {
        public static final NanoTechDamageSource INFECTION = new NanoTechDamageSource();
        
        private NanoTechDamageSource() {
            super("nano_tech_infection");
            this.setDamageBypassesArmor();
        }
        
        @Override
        public ITextComponent getDeathMessage(LivingEntity entity) {
            return new StringTextComponent(entity.getName().getString() + " was consumed by nano tech infection");
        }
    }
}