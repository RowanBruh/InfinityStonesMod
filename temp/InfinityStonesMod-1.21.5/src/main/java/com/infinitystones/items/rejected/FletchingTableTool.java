package com.infinitystones.items.rejected;

import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Fletching Table Tool - what the Fletching Table could have been.
 * In this implementation, it allows the player to enhance arrows and create special arrows.
 */
public class FletchingTableTool extends Item {

    private static final Random RANDOM = new Random();

    public FletchingTableTool() {
        super(new Item.Properties()
                .group(ModItemGroups.ROWAN_INDUSTRIES)
                .maxStackSize(1)
                .maxDamage(64)); // 64 uses
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack toolStack = player.getHeldItem(hand);
        
        // Find arrows in the player's inventory
        boolean hasArrows = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
                hasArrows = true;
                break;
            }
        }
        
        if (!hasArrows) {
            if (!world.isRemote) {
                player.sendStatusMessage(new StringTextComponent(
                        "You need arrows in your inventory to use the Fletching Table Tool."), true);
            }
            return ActionResult.resultFail(toolStack);
        }
        
        if (!world.isRemote) {
            // Enhance some arrows in the player's inventory
            enhancePlayerArrows(player);
            
            // Play sound effect
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.BLOCK_BAMBOO_PLACE, SoundCategory.PLAYERS,
                    1.0F, 1.0F);
            
            // Damage the tool
            if (!player.isCreative()) {
                toolStack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
            }
        } else {
            // Client-side particle effects
            for (int i = 0; i < 10; i++) {
                world.addParticle(
                        ParticleTypes.CRIT,
                        player.getPosX() + (RANDOM.nextDouble() - 0.5D),
                        player.getPosY() + 1.0D + (RANDOM.nextDouble() - 0.5D),
                        player.getPosZ() + (RANDOM.nextDouble() - 0.5D),
                        0, 0, 0
                );
            }
        }
        
        return ActionResult.resultSuccess(toolStack);
    }
    
    /**
     * Enhance some arrows in the player's inventory
     */
    private void enhancePlayerArrows(PlayerEntity player) {
        World world = player.world;
        int arrowsToEnhance = 4 + RANDOM.nextInt(5); // Enhance 4-8 arrows
        int enhancedCount = 0;
        
        // Find all arrows in inventory
        for (int slot = 0; slot < player.inventory.getSizeInventory() && enhancedCount < arrowsToEnhance; slot++) {
            ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
            
            // Only process regular arrows
            if (!stackInSlot.isEmpty() && stackInSlot.getItem() == Items.ARROW) {
                // How many arrows to convert from this stack
                int convertCount = Math.min(stackInSlot.getCount(), arrowsToEnhance - enhancedCount);
                
                if (convertCount > 0) {
                    // Remove regular arrows
                    stackInSlot.shrink(convertCount);
                    
                    // Add special arrows
                    ItemStack specialArrows = createSpecialArrows(convertCount);
                    if (!player.inventory.addItemStackToInventory(specialArrows)) {
                        // Drop items if inventory is full
                        player.dropItem(specialArrows, false);
                    }
                    
                    enhancedCount += convertCount;
                }
            }
        }
        
        // Notify the player
        if (enhancedCount > 0) {
            player.sendStatusMessage(new StringTextComponent(
                    TextFormatting.GREEN + "Enhanced " + enhancedCount + " arrows with the Fletching Table Tool!"), false);
        } else {
            player.sendStatusMessage(new StringTextComponent(
                    TextFormatting.RED + "Failed to enhance any arrows. Make sure you have regular arrows."), false);
        }
    }
    
    /**
     * Create a stack of special arrows
     */
    private ItemStack createSpecialArrows(int count) {
        // Decide what type of special arrows to create
        int arrowType = RANDOM.nextInt(10);
        
        if (arrowType < 5) {
            // 50% chance for spectral arrows
            return new ItemStack(Items.SPECTRAL_ARROW, count);
        } else {
            // 50% chance for tipped arrows with random potion effects
            Potion[] potions = {
                    Potions.SWIFTNESS, Potions.STRENGTH, Potions.HEALING,
                    Potions.NIGHT_VISION, Potions.INVISIBILITY, Potions.LEAPING,
                    Potions.REGENERATION, Potions.FIRE_RESISTANCE, Potions.WATER_BREATHING
            };
            
            Potion selectedPotion = potions[RANDOM.nextInt(potions.length)];
            ItemStack tippedArrows = new ItemStack(Items.TIPPED_ARROW, count);
            PotionUtils.addPotionToItemStack(tippedArrows, selectedPotion);
            
            return tippedArrows;
        }
    }
}