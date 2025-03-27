package com.infinitystones.items;

import com.infinitystones.tabs.ModItemGroups;
import com.infinitystones.util.StoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Represents an Infected version of an Infinity Stone with higher power but negative side effects
 */
public class InfectedInfinityStone extends Item {
    
    private static final Random RANDOM = new Random();
    private final StoneType type;
    
    /**
     * Constructor for the Infected Infinity Stone
     *
     * @param type The type of stone
     */
    public InfectedInfinityStone(StoneType type) {
        super(new Item.Properties()
                .group(ModItemGroups.ROWAN_INDUSTRIES)
                .maxStackSize(1)
                .rarity(net.minecraft.item.Rarity.EPIC));
        this.type = type;
    }
    
    /**
     * Gets the type of stone
     *
     * @return The stone type
     */
    public StoneType getType() {
        return type;
    }
    
    /**
     * Called when the item is right-clicked
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            // Activate the stone's ability with enhanced power
            activateInfectedStoneAbility(player, world);
            
            // Apply negative side effects
            applyNegativeSideEffects(player);
            
            // Apply cooldown
            player.getCooldownTracker().setCooldown(this, 100); // 5 seconds cooldown (faster than normal stones)
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Activates the infected stone ability with enhanced power
     */
    private void activateInfectedStoneAbility(PlayerEntity player, World world) {
        // First activate the normal stone ability
        StoneAbilities.activateStoneAbility(type, player, world);
        
        // Then add additional boosted effects based on stone type
        switch (type) {
            case SPACE:
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 400, 3)); // Faster movement
                break;
            case MIND:
                player.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 200, 0)); // Invisibility
                break;
            case REALITY:
                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 400, 3)); // Higher jumps
                break;
            case POWER:
                player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 400, 4)); // More strength
                break;
            case TIME:
                player.addPotionEffect(new EffectInstance(Effects.HASTE, 400, 3)); // Faster mining
                break;
            case SOUL:
                player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 400, 4)); // More absorption
                break;
        }
        
        player.sendMessage(new StringTextComponent("§5INFECTED " + type.getDisplayName().toUpperCase() + " POWER ACTIVATED!"), 
                player.getUniqueID());
    }
    
    /**
     * Applies negative side effects from using the infected stone
     */
    private void applyNegativeSideEffects(PlayerEntity player) {
        // 70% chance of minor negative effect
        if (RANDOM.nextFloat() < 0.7f) {
            // Apply a random minor negative effect
            switch (RANDOM.nextInt(3)) {
                case 0:
                    player.addPotionEffect(new EffectInstance(Effects.HUNGER, 200, 1));
                    player.sendMessage(new StringTextComponent("§8Side effect: You feel hungry"), player.getUniqueID());
                    break;
                case 1:
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 0));
                    player.sendMessage(new StringTextComponent("§8Side effect: Your movement slows briefly"), player.getUniqueID());
                    break;
                case 2:
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 200, 0));
                    player.sendMessage(new StringTextComponent("§8Side effect: Your arms feel heavy"), player.getUniqueID());
                    break;
            }
        }
        
        // 25% chance of major negative effect
        if (RANDOM.nextFloat() < 0.25f) {
            // Apply a random major negative effect
            switch (RANDOM.nextInt(3)) {
                case 0:
                    player.addPotionEffect(new EffectInstance(Effects.POISON, 200, 1));
                    player.sendMessage(new StringTextComponent("§4Severe side effect: The infection spreads through your body"), 
                            player.getUniqueID());
                    break;
                case 1:
                    player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 300, 1));
                    player.sendMessage(new StringTextComponent("§4Severe side effect: Your strength is sapped"), 
                            player.getUniqueID());
                    break;
                case 2:
                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100, 0));
                    player.sendMessage(new StringTextComponent("§4Severe side effect: Your vision blurs"), 
                            player.getUniqueID());
                    break;
            }
        }
        
        // 5% chance of critical negative effect
        if (RANDOM.nextFloat() < 0.05f) {
            player.addPotionEffect(new EffectInstance(Effects.WITHER, 100, 1));
            player.sendMessage(new StringTextComponent("§4§lCRITICAL: The infection is attacking your life force!"), 
                    player.getUniqueID());
        }
    }
    
    /**
     * Adds information to the tooltip
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("Infected " + type.getDisplayName())
                .mergeStyle(TextFormatting.DARK_PURPLE));
        
        tooltip.add(new StringTextComponent("Enhanced stone with unpredictable side effects")
                .mergeStyle(TextFormatting.GRAY));
        
        tooltip.add(new StringTextComponent("§cWARNING: Use at your own risk!")
                .mergeStyle(TextFormatting.RED));
        
        tooltip.add(new StringTextComponent("Right-click to use infected power")
                .mergeStyle(TextFormatting.DARK_GRAY));
        
        super.addInformation(stack, world, tooltip, flag);
    }
}