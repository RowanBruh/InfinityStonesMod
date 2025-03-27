package com.infinitystones.items.rejected;

import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Rascal Bag - based on the rejected Rascal mob from the Minecraft mob vote.
 * In this implementation, it's a bag of rewards that simulates the hide-and-seek nature of the Rascal.
 */
public class RascalBag extends Item {

    private static final Random RANDOM = new Random();

    public RascalBag() {
        super(new Item.Properties()
                .group(ModItemGroups.ROWAN_INDUSTRIES)
                .maxStackSize(16));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return ActionResult.resultSuccess(stack);
        }
        
        // Generate a random reward
        ItemStack reward = generateRandomReward();
        
        // Give the reward to the player
        if (!player.inventory.addItemStackToInventory(reward)) {
            // If inventory is full, drop the item
            player.dropItem(reward, false);
        }
        
        // Play a fun sound
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,
                0.5F, 0.8F + RANDOM.nextFloat() * 0.4F);
        
        // Spawn particles
        if (world.isRemote) {
            for (int i = 0; i < 16; i++) {
                world.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        player.getPosX() + (RANDOM.nextDouble() - 0.5D) * 1.5D,
                        player.getPosY() + 0.5D + (RANDOM.nextDouble() - 0.5D) * 1.5D,
                        player.getPosZ() + (RANDOM.nextDouble() - 0.5D) * 1.5D,
                        0, 0, 0
                );
            }
        }
        
        // Send message
        player.sendStatusMessage(new StringTextComponent("You found a " + 
                TextFormatting.GOLD + reward.getDisplayName().getString() + 
                TextFormatting.RESET + " in the Rascal Bag!"), false);
        
        // Consume one bag in non-creative mode
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Generates a random enchanted tool or other rewards
     */
    private ItemStack generateRandomReward() {
        ItemStack reward;
        
        // Decide reward type
        int rewardType = RANDOM.nextInt(100);
        
        if (rewardType < 40) {
            // 40% chance for an enchanted tool
            reward = getRandomEnchantedTool();
        } else if (rewardType < 70) {
            // 30% chance for resources
            reward = getRandomResources();
        } else if (rewardType < 90) {
            // 20% chance for a potion
            reward = getRandomPotion();
        } else {
            // 10% chance for something special
            reward = getSpecialReward();
        }
        
        return reward;
    }
    
    /**
     * Creates a random enchanted tool
     */
    private ItemStack getRandomEnchantedTool() {
        Item[] tools = {
                Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, 
                Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
                Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL,
                Items.FISHING_ROD
        };
        
        // Select a random tool
        Item toolItem = tools[RANDOM.nextInt(tools.length)];
        ItemStack tool = new ItemStack(toolItem);
        
        // Add random enchantment
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        
        // Choose appropriate enchantments based on the tool type
        if (toolItem == Items.FISHING_ROD) {
            enchantments.put(Enchantments.LUCK_OF_THE_SEA, 1 + RANDOM.nextInt(3));
        } else if (toolItem == Items.WOODEN_PICKAXE || toolItem == Items.STONE_PICKAXE || toolItem == Items.IRON_PICKAXE) {
            enchantments.put(Enchantments.EFFICIENCY, 1 + RANDOM.nextInt(3));
            if (RANDOM.nextBoolean()) {
                enchantments.put(Enchantments.UNBREAKING, 1 + RANDOM.nextInt(2));
            }
        } else {
            enchantments.put(Enchantments.EFFICIENCY, 1 + RANDOM.nextInt(3));
            if (RANDOM.nextBoolean()) {
                enchantments.put(Enchantments.UNBREAKING, 1);
            }
        }
        
        EnchantmentHelper.setEnchantments(enchantments, tool);
        return tool;
    }
    
    /**
     * Creates a random resource stack
     */
    private ItemStack getRandomResources() {
        Item[] resources = {
                Items.COAL, Items.IRON_INGOT, Items.GOLD_INGOT, Items.REDSTONE,
                Items.LAPIS_LAZULI, Items.DIAMOND, Items.EMERALD,
                Items.BREAD, Items.COOKED_BEEF, Items.GOLDEN_APPLE
        };
        
        Item resourceItem = resources[RANDOM.nextInt(resources.length)];
        int count = 1;
        
        // Adjust count based on rarity
        if (resourceItem == Items.COAL || resourceItem == Items.REDSTONE || resourceItem == Items.LAPIS_LAZULI) {
            count = 4 + RANDOM.nextInt(12); // 4-16
        } else if (resourceItem == Items.IRON_INGOT) {
            count = 1 + RANDOM.nextInt(5); // 1-6
        } else if (resourceItem == Items.GOLD_INGOT) {
            count = 1 + RANDOM.nextInt(4); // 1-5
        } else if (resourceItem == Items.BREAD || resourceItem == Items.COOKED_BEEF) {
            count = 2 + RANDOM.nextInt(6); // 2-8
        } else if (resourceItem == Items.DIAMOND || resourceItem == Items.EMERALD) {
            count = 1 + RANDOM.nextInt(2); // 1-3
        }
        
        return new ItemStack(resourceItem, count);
    }
    
    /**
     * Creates a random potion
     */
    private ItemStack getRandomPotion() {
        // For simplicity, just return vanilla potions
        // In a full implementation, this would return actual potion items with effects
        Item[] potions = {
                Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION
        };
        
        return new ItemStack(potions[RANDOM.nextInt(potions.length)]);
    }
    
    /**
     * Creates a special rare reward
     */
    private ItemStack getSpecialReward() {
        Item[] specialItems = {
                Items.DIAMOND_PICKAXE, Items.DIAMOND_SWORD, Items.GOLDEN_CARROT,
                Items.ENCHANTED_GOLDEN_APPLE, Items.EXPERIENCE_BOTTLE, Items.MUSIC_DISC_CAT
        };
        
        Item specialItem = specialItems[RANDOM.nextInt(specialItems.length)];
        ItemStack reward = new ItemStack(specialItem);
        
        // Add enchantments to tools/weapons
        if (specialItem == Items.DIAMOND_PICKAXE || specialItem == Items.DIAMOND_SWORD) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            enchantments.put(Enchantments.UNBREAKING, 3);
            
            if (specialItem == Items.DIAMOND_PICKAXE) {
                enchantments.put(Enchantments.EFFICIENCY, 4);
                if (RANDOM.nextBoolean()) {
                    enchantments.put(Enchantments.FORTUNE, 2);
                }
            } else {
                enchantments.put(Enchantments.SHARPNESS, 4);
                if (RANDOM.nextBoolean()) {
                    enchantments.put(Enchantments.LOOTING, 2);
                }
            }
            
            EnchantmentHelper.setEnchantments(enchantments, reward);
        } else if (specialItem == Items.EXPERIENCE_BOTTLE) {
            reward.setCount(4 + RANDOM.nextInt(8)); // 4-12 bottles
        }
        
        return reward;
    }
}