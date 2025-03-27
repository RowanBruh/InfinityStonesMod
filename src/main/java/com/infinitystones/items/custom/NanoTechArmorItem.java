package com.infinitystones.items.custom;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * High-tech armor with enhanced protection and special abilities.
 * The infected variant provides better protection but may cause negative effects to the wearer.
 */
public class NanoTechArmorItem extends ArmorItem {
    private final boolean isInfected;
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
    };
    
    public NanoTechArmorItem(ArmorMaterial material, EquipmentSlot slot, Properties properties, boolean isInfected) {
        super(material, slot, properties);
        this.isInfected = isInfected;
    }
    
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            // Check for full set
            boolean hasFullSet = hasFullNanoTechSet(player);
            
            // Apply effects based on the specific armor piece
            switch (slot) {
                case HEAD:
                    applyHelmetEffects(player, hasFullSet);
                    break;
                case CHEST:
                    applyChestplateEffects(player, hasFullSet);
                    break;
                case LEGS:
                    applyLeggingsEffects(player, hasFullSet);
                    break;
                case FEET:
                    applyBootsEffects(player, hasFullSet);
                    break;
                default:
                    break;
            }
            
            // Apply negative effects for infected items
            if (isInfected && level.getGameTime() % 300 == 0) {
                applyInfectedNegativeEffects(player);
            }
            
            // Apply full set bonus if applicable
            if (hasFullSet) {
                applyFullSetBonus(player);
            }
        }
        
        super.onArmorTick(stack, level, player);
    }
    
    private void applyHelmetEffects(Player player, boolean hasFullSet) {
        // Base effects for regular and infected helmets
        if (player.isUnderWater() && player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getAirSupply() + 1);
        }
        
        // Helmet specific boost
        if (isInfected) {
            if (player.getRandom().nextFloat() < 0.2f) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
            }
        }
    }
    
    private void applyChestplateEffects(Player player, boolean hasFullSet) {
        // Chestplate specific boost
        if (isInfected) {
            if (player.getRandom().nextFloat() < 0.1f) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false));
            }
        }
    }
    
    private void applyLeggingsEffects(Player player, boolean hasFullSet) {
        // Leggings specific boost
        if (isInfected) {
            if (player.getRandom().nextFloat() < 0.1f) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false));
            }
        }
    }
    
    private void applyBootsEffects(Player player, boolean hasFullSet) {
        // Boots specific boost - reduced fall damage
        if (isInfected) {
            if (player.fallDistance > 3.0F) {
                player.fallDistance *= 0.8F;
            }
        } else {
            if (player.fallDistance > 3.0F) {
                player.fallDistance *= 0.9F;
            }
        }
    }
    
    private void applyInfectedNegativeEffects(Player player) {
        float chance = player.getRandom().nextFloat();
        if (chance < 0.05f) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        } else if (chance < 0.1f) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0));
        }
    }
    
    private void applyFullSetBonus(Player player) {
        // Full set bonus for regular nano tech armor
        if (!isInfected) {
            // Regular full set gives damage reduction and slight regeneration
            if (player.getHealth() < player.getMaxHealth() && player.getRandom().nextFloat() < 0.05f) {
                player.heal(1.0F);
            }
        } 
        // Full set bonus for infected nano tech armor
        else {
            // Infected full set gives stronger regeneration but with side effects
            if (player.getHealth() < player.getMaxHealth() && player.getRandom().nextFloat() < 0.1f) {
                player.heal(1.0F);
                // 20% chance of hunger when healing
                if (player.getRandom().nextFloat() < 0.2f) {
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 0));
                }
            }
        }
    }
    
    private boolean hasFullNanoTechSet(Player player) {
        boolean hasMatchingHelmet = false, hasMatchingChestplate = false, 
                hasMatchingLeggings = false, hasMatchingBoots = false;
        
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof NanoTechArmorItem) {
                NanoTechArmorItem nanoArmor = (NanoTechArmorItem) armor.getItem();
                // Only count as matching if the infection status is the same
                if (nanoArmor.isInfected() == this.isInfected) {
                    EquipmentSlot slot = ((ArmorItem)armor.getItem()).getSlot();
                    switch (slot) {
                        case HEAD:
                            hasMatchingHelmet = true;
                            break;
                        case CHEST:
                            hasMatchingChestplate = true;
                            break;
                        case LEGS:
                            hasMatchingLeggings = true;
                            break;
                        case FEET:
                            hasMatchingBoots = true;
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        
        return hasMatchingHelmet && hasMatchingChestplate && hasMatchingLeggings && hasMatchingBoots;
    }
    
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == this.slot) {
            Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            
            // Add base armor modifiers
            builder.putAll(super.getAttributeModifiers(slot, stack));
            
            UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
            
            // Add extra armor toughness for infected armor
            if (isInfected) {
                builder.put(Attributes.ARMOR_TOUGHNESS, 
                        new AttributeModifier(uuid, "Nano tech armor toughness bonus", 2.0, AttributeModifier.Operation.ADDITION));
                
                // Add knockback resistance based on the armor piece
                double knockbackResistance = 0.0;
                switch (slot) {
                    case HEAD:
                        knockbackResistance = 0.1;
                        break;
                    case CHEST:
                        knockbackResistance = 0.2;
                        break;
                    case LEGS:
                        knockbackResistance = 0.1;
                        break;
                    case FEET:
                        knockbackResistance = 0.1;
                        break;
                    default:
                        break;
                }
                
                if (knockbackResistance > 0.0) {
                    builder.put(Attributes.KNOCKBACK_RESISTANCE, 
                            new AttributeModifier(uuid, "Nano tech knockback resistance", knockbackResistance, 
                                    AttributeModifier.Operation.ADDITION));
                }
            }
            
            return builder.build();
        }
        
        return super.getAttributeModifiers(slot, stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
            TooltipFlag isAdvanced) {
        String slotKey = "";
        switch (slot) {
            case HEAD:
                slotKey = "helmet";
                break;
            case CHEST:
                slotKey = "chestplate";
                break;
            case LEGS:
                slotKey = "leggings";
                break;
            case FEET:
                slotKey = "boots";
                break;
            default:
                break;
        }
        
        if (isInfected) {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_nano_" + slotKey + ".desc")
                .withStyle(ChatFormatting.DARK_PURPLE));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.infected_warning")
                .withStyle(ChatFormatting.DARK_RED));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.infinitystones.nano_" + slotKey + ".desc")
                .withStyle(ChatFormatting.AQUA));
        }
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    public boolean isInfected() {
        return isInfected;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        // Add enchantment glint effect to infected armor
        return isInfected || super.isFoil(stack);
    }
}