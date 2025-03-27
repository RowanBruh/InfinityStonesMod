package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityStones.StoneType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
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
 * Infected versions of the Infinity Stones - corrupted by nano tech
 */
public class InfectedInfinityStones {
    private static final Random random = new Random();
    
    /**
     * Base class for all infected infinity stones
     */
    public static class InfectedStoneItem extends Item {
        private final StoneType stoneType;
        
        public InfectedStoneItem(StoneType stoneType) {
            super(new Item.Properties()
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES)
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC));
            this.stoneType = stoneType;
        }
        
        public StoneType getStoneType() {
            return stoneType;
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
            ItemStack stack = player.getHeldItem(hand);
            
            if (world.isRemote) {
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
            
            // Apply a negative effect (infection) then standard stone effect
            applyInfectionEffect(player);
            
            // Also apply the standard abilities, but with some variation
            executeInfectedStoneAbility(world, player, stoneType);
            
            // Set a cooldown
            player.getCooldownTracker().setCooldown(this, 20 * 15); // 15 second cooldown
            
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        
        private void applyInfectionEffect(PlayerEntity player) {
            // Apply negative effects from infection
            player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 0));
            
            // Small chance of damage
            if (random.nextFloat() < 0.1f) {
                player.attackEntityFrom(NanoTechItems.NanoTechDamageSource.INFECTION, 1.0f);
            }
        }
        
        private void executeInfectedStoneAbility(World world, PlayerEntity player, StoneType stoneType) {
            // Enhanced but unstable versions of the regular infinity stone abilities
            switch (stoneType) {
                case SPACE:
                    // Corrupted teleportation - more range but can cause damage
                    int range = 64; // Double the range of normal stone
                    double x = player.getPosX() + (random.nextDouble() - 0.5) * range * 2;
                    double z = player.getPosZ() + (random.nextDouble() - 0.5) * range * 2;
                    double y = world.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE, (int) x, (int) z);
                    player.teleportKeepLoaded(x, y, z);
                    
                    // But can cause damage
                    if (random.nextFloat() < 0.25f) {
                        player.attackEntityFrom(NanoTechItems.NanoTechDamageSource.INFECTION, 2.0f);
                        player.sendStatusMessage(new StringTextComponent("The corrupted space stone destabilizes your atoms!")
                                .mergeStyle(TextFormatting.RED), true);
                    }
                    break;
                    
                case MIND:
                    // Enhanced XP but at a cost
                    player.addExperienceLevel(3); // More XP than normal stone
                    
                    // Apply confusion
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 1));
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 100, 1));
                    player.sendStatusMessage(new StringTextComponent("The corrupted mind stone grants knowledge, but clouds your mind.")
                            .mergeStyle(TextFormatting.RED), true);
                    break;
                    
                case REALITY:
                    // Enhanced item creation but with instability
                    // Create multiple items
                    for (int i = 0; i < 3; i++) {
                        ItemStack itemStack = getRandomValuableItem();
                        player.dropItem(itemStack, false);
                    }
                    
                    // But reality is unstable
                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 80, 0));
                    player.sendStatusMessage(new StringTextComponent("The corrupted reality stone bends existence to your will!")
                            .mergeStyle(TextFormatting.RED), true);
                    break;
                    
                case POWER:
                    // More power but less control
                    int powerDuration = 300; // 15 seconds
                    player.addPotionEffect(new EffectInstance(Effects.STRENGTH, powerDuration, 3)); // Higher strength level
                    player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, powerDuration, 1));
                    
                    // But also causes instability
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, powerDuration / 2, 0));
                    player.sendStatusMessage(new StringTextComponent("The corrupted power stone surges through you uncontrollably!")
                            .mergeStyle(TextFormatting.RED), true);
                    break;
                    
                case TIME:
                    // Enhanced time manipulation but with side effects
                    // Speed effect for player
                    player.addPotionEffect(new EffectInstance(Effects.SPEED, 400, 2)); // Faster and longer
                    
                    // But time distortion affects the user
                    if (random.nextFloat() < 0.5f) {
                        player.addPotionEffect(new EffectInstance(Effects.HUNGER, 300, 1));
                        player.sendStatusMessage(new StringTextComponent("The corrupted time stone accelerates your metabolism!")
                                .mergeStyle(TextFormatting.RED), true);
                    } else {
                        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));
                        player.sendStatusMessage(new StringTextComponent("The corrupted time stone ages your muscles temporarily!")
                                .mergeStyle(TextFormatting.RED), true);
                    }
                    break;
                    
                case SOUL:
                    // Enhanced life manipulation
                    player.heal(player.getMaxHealth()); // Full heal rather than partial
                    
                    // Multiple positive effects
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 300, 1));
                    player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 300, 2));
                    
                    // But drains life energy
                    player.addPotionEffect(new EffectInstance(Effects.HUNGER, 600, 1));
                    player.sendStatusMessage(new StringTextComponent("The corrupted soul stone heals your body but drains your essence!")
                            .mergeStyle(TextFormatting.RED), true);
                    break;
            }
        }
        
        private ItemStack getRandomValuableItem() {
            String[] possibleItems = {
                    "minecraft:diamond", "minecraft:emerald", "minecraft:gold_ingot", 
                    "minecraft:iron_ingot", "minecraft:netherite_scrap", "minecraft:blaze_rod",
                    "minecraft:ender_pearl", "minecraft:ghast_tear", "minecraft:golden_apple",
                    "minecraft:experience_bottle"
            };
            
            String selectedItem = possibleItems[random.nextInt(possibleItems.length)];
            return new ItemStack(InfinityStonesMod.findItem(selectedItem), 1 + random.nextInt(3));
        }
        
        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
            // Infected stones occasionally cause problems even when just carried
            if (!world.isRemote && entity instanceof PlayerEntity && random.nextFloat() < 0.001f) {
                // 0.1% chance per tick of applying a minor negative effect
                PlayerEntity player = (PlayerEntity) entity;
                player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 60, 0));
                
                if (isSelected) {
                    // Worse effects if holding it
                    player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 60, 0));
                }
            }
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Infected " + stoneType.getName() + " Stone").mergeStyle(TextFormatting.DARK_PURPLE));
            tooltip.add(new StringTextComponent("Corrupted by Nano Tech").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent(""));
            
            // Stone-specific descriptions
            switch (stoneType) {
                case SPACE:
                    tooltip.add(new StringTextComponent("Chaotic teleportation over vast distances").mergeStyle(TextFormatting.BLUE));
                    break;
                case MIND:
                    tooltip.add(new StringTextComponent("Grants knowledge at the cost of mental clarity").mergeStyle(TextFormatting.YELLOW));
                    break;
                case REALITY:
                    tooltip.add(new StringTextComponent("Creates valuable items but distorts perception").mergeStyle(TextFormatting.RED));
                    break;
                case POWER:
                    tooltip.add(new StringTextComponent("Grants immense strength with poor control").mergeStyle(TextFormatting.DARK_PURPLE));
                    break;
                case TIME:
                    tooltip.add(new StringTextComponent("Accelerates the user but with temporal side effects").mergeStyle(TextFormatting.GREEN));
                    break;
                case SOUL:
                    tooltip.add(new StringTextComponent("Complete healing that drains life energy").mergeStyle(TextFormatting.GOLD));
                    break;
            }
            
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new StringTextComponent("Warning: May cause infection symptoms").mergeStyle(TextFormatting.DARK_RED));
        }
    }
}