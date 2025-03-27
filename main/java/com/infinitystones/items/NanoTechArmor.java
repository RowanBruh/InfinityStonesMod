package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Nano Tech Armor with special abilities
 */
@Mod.EventBusSubscriber
public class NanoTechArmor extends ArmorItem {
    
    /**
     * Enum for the different armor types
     */
    public enum ArmorType {
        HELMET(EquipmentSlotType.HEAD, "nano_tech_helmet", "Nano Tech Helmet"),
        CHESTPLATE(EquipmentSlotType.CHEST, "nano_tech_chestplate", "Nano Tech Chestplate"),
        LEGGINGS(EquipmentSlotType.LEGS, "nano_tech_leggings", "Nano Tech Leggings"),
        BOOTS(EquipmentSlotType.FEET, "nano_tech_boots", "Nano Tech Boots");
        
        private final EquipmentSlotType slot;
        private final String registryName;
        private final String displayName;
        
        ArmorType(EquipmentSlotType slot, String registryName, String displayName) {
            this.slot = slot;
            this.registryName = registryName;
            this.displayName = displayName;
        }
        
        public EquipmentSlotType getSlot() {
            return slot;
        }
        
        public String getRegistryName() {
            return registryName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Custom armor material for Nano Tech
    private static final IArmorMaterial NANO_TECH_MATERIAL = new ArmorMaterial("nano_tech",
            30, // Durability multiplier
            new int[]{3, 6, 8, 3}, // Damage reduction for each piece
            15, // Enchantability
            null, // Sound event
            3.0F, // Toughness
            0.1F); // Knockback resistance
    
    private final ArmorType armorType;
    
    /**
     * Constructor for Nano Tech Armor
     *
     * @param armorType The armor type
     */
    public NanoTechArmor(ArmorType armorType) {
        super(NANO_TECH_MATERIAL, armorType.getSlot(), 
                new Properties().group(ModItemGroups.ROWAN_INDUSTRIES).maxStackSize(1));
        this.armorType = armorType;
    }
    
    /**
     * Event handler for player tick events to apply armor effects
     */
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            
            // Check if the player has a full set
            if (hasFullNanoTechSet(player)) {
                // Apply full set bonus once per second
                if (player.ticksExisted % 20 == 0) {
                    applyFullSetEffects(player);
                }
            } else {
                // Apply partial set bonuses once per second
                if (player.ticksExisted % 20 == 0) {
                    applyPartialSetEffects(player);
                }
            }
        }
    }
    
    /**
     * Applies effects for wearing the full Nano Tech armor set
     *
     * @param player The player
     */
    private static void applyFullSetEffects(PlayerEntity player) {
        // Powerful effects for full set
        player.addPotionEffect(new EffectInstance(Effects.SPEED, 40, 1, false, false));
        player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 40, 1, false, false));
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 40, 1, false, false));
        player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40, 0, false, false));
        
        // Refill oxygen if under water
        if (player.getAir() < player.getMaxAir()) {
            player.setAir(player.getMaxAir());
        }
    }
    
    /**
     * Applies effects for wearing partial Nano Tech armor pieces
     *
     * @param player The player
     */
    private static void applyPartialSetEffects(PlayerEntity player) {
        // Only apply if the player has at least one piece
        if (hasAnyNanoTechPiece(player)) {
            // Check individual pieces and apply effects
            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                    ItemStack stack = player.getItemStackFromSlot(slot);
                    if (stack.getItem() instanceof NanoTechArmor) {
                        NanoTechArmor armor = (NanoTechArmor) stack.getItem();
                        
                        // Apply effects based on piece
                        switch (armor.armorType) {
                            case HELMET:
                                player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0, false, false));
                                break;
                            case CHESTPLATE:
                                player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 40, 0, false, false));
                                break;
                            case LEGGINGS:
                                player.addPotionEffect(new EffectInstance(Effects.SPEED, 40, 0, false, false));
                                break;
                            case BOOTS:
                                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 40, 0, false, false));
                                break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Checks if the player has a full set of Nano Tech armor
     *
     * @param player The player
     * @return True if the player has a full set, false otherwise
     */
    private static boolean hasFullNanoTechSet(PlayerEntity player) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (!(stack.getItem() instanceof NanoTechArmor)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Checks if the player has any Nano Tech armor piece
     *
     * @param player The player
     * @return True if the player has any piece, false otherwise
     */
    private static boolean hasAnyNanoTechPiece(PlayerEntity player) {
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (stack.getItem() instanceof NanoTechArmor) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Adds information to the tooltip
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent(armorType.getDisplayName())
                .mergeStyle(TextFormatting.AQUA));
        
        tooltip.add(new StringTextComponent("Advanced nano-technology armor with special abilities")
                .mergeStyle(TextFormatting.GRAY));
        
        // Add piece-specific descriptions
        switch (armorType) {
            case HELMET:
                tooltip.add(new StringTextComponent("• Provides Night Vision")
                        .mergeStyle(TextFormatting.GREEN));
                break;
            case CHESTPLATE:
                tooltip.add(new StringTextComponent("• Provides Damage Resistance")
                        .mergeStyle(TextFormatting.GREEN));
                break;
            case LEGGINGS:
                tooltip.add(new StringTextComponent("• Provides Speed")
                        .mergeStyle(TextFormatting.GREEN));
                break;
            case BOOTS:
                tooltip.add(new StringTextComponent("• Provides Jump Boost")
                        .mergeStyle(TextFormatting.GREEN));
                break;
        }
        
        tooltip.add(new StringTextComponent("Full Set Bonus: Enhanced movement, protection and underwater breathing")
                .mergeStyle(TextFormatting.GOLD));
        
        super.addInformation(stack, world, tooltip, flag);
    }
    
    /**
     * Custom armor material implementation
     */
    private static class ArmorMaterial implements IArmorMaterial {
        private final String name;
        private final int durabilityMultiplier;
        private final int[] damageReductionAmounts;
        private final int enchantability;
        private final net.minecraft.util.SoundEvent soundEvent;
        private final float toughness;
        private final float knockbackResistance;
        
        public ArmorMaterial(String name, int durabilityMultiplier, int[] damageReductionAmounts, 
                            int enchantability, net.minecraft.util.SoundEvent soundEvent, 
                            float toughness, float knockbackResistance) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.damageReductionAmounts = damageReductionAmounts;
            this.enchantability = enchantability;
            this.soundEvent = soundEvent != null ? soundEvent : net.minecraft.util.SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
        }
        
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return net.minecraft.item.ArmorMaterial.DIAMOND.getDurability(slotIn) * durabilityMultiplier / 10;
        }
        
        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return this.damageReductionAmounts[slotIn.getIndex()];
        }
        
        @Override
        public int getEnchantability() {
            return this.enchantability;
        }
        
        @Override
        public net.minecraft.util.SoundEvent getSoundEvent() {
            return this.soundEvent;
        }
        
        @Override
        public net.minecraft.item.crafting.Ingredient getRepairMaterial() {
            return net.minecraft.item.crafting.Ingredient.fromItems(ModItems.NANO_TECH_CORE.get());
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public float getToughness() {
            return this.toughness;
        }
        
        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }
}