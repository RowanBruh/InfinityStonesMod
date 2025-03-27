package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Custom loot boxes inspired by SkiddziePlays
 */
public class SkiddzieCustomBoxes {
    private static final Random random = new Random();
    
    /**
     * The rarity level determines the quality of rewards
     */
    public enum BoxRarity {
        COMMON("common_box", TextFormatting.WHITE, "Common", Rarity.COMMON),
        RARE("rare_box", TextFormatting.BLUE, "Rare", Rarity.UNCOMMON),
        EPIC("epic_box", TextFormatting.DARK_PURPLE, "Epic", Rarity.RARE),
        LEGENDARY("legendary_box", TextFormatting.GOLD, "Legendary", Rarity.EPIC),
        COSMIC("cosmic_box", TextFormatting.AQUA, "Cosmic", Rarity.EPIC),
        INSANE("insane_box", TextFormatting.RED, "Insane", Rarity.EPIC);
        
        private final String registryName;
        private final TextFormatting textColor;
        private final String displayName;
        private final Rarity itemRarity;
        
        BoxRarity(String registryName, TextFormatting textColor, String displayName, Rarity itemRarity) {
            this.registryName = registryName;
            this.textColor = textColor;
            this.displayName = displayName;
            this.itemRarity = itemRarity;
        }
        
        public String getRegistryName() {
            return registryName;
        }
        
        public TextFormatting getTextColor() {
            return textColor;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Rarity getItemRarity() {
            return itemRarity;
        }
    }
    
    /**
     * Base class for all loot boxes
     */
    public static class CustomBoxItem extends Item {
        private final BoxRarity boxRarity;
        
        public CustomBoxItem(BoxRarity boxRarity) {
            this(boxRarity, InfinityStonesMod.INFINITY_GROUP);
        }
        
        public CustomBoxItem(BoxRarity boxRarity, net.minecraft.item.ItemGroup group) {
            super(new Item.Properties()
                    .group(group)
                    .maxStackSize(16)
                    .rarity(boxRarity.getItemRarity()));
            this.boxRarity = boxRarity;
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack itemStack = playerIn.getHeldItem(handIn);
            
            if (worldIn.isRemote) {
                return ActionResult.resultSuccess(itemStack);
            }
            
            // Play opening sound
            worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                    SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.PLAYERS, 1.0F, 1.0F);
            
            // Add particle effects
            if (worldIn instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) worldIn;
                
                // Different particles based on rarity
                switch (boxRarity) {
                    case COMMON:
                        serverWorld.spawnParticle(ParticleTypes.SMOKE, 
                                playerIn.getPosX(), playerIn.getPosY() + 1.0, playerIn.getPosZ(), 
                                30, 0.5, 0.5, 0.5, 0.1);
                        break;
                    case RARE:
                        serverWorld.spawnParticle(ParticleTypes.ENCHANT, 
                                playerIn.getPosX(), playerIn.getPosY() + 1.0, playerIn.getPosZ(), 
                                50, 0.5, 0.5, 0.5, 0.5);
                        break;
                    case EPIC:
                        serverWorld.spawnParticle(ParticleTypes.DRAGON_BREATH, 
                                playerIn.getPosX(), playerIn.getPosY() + 1.0, playerIn.getPosZ(), 
                                60, 0.5, 0.5, 0.5, 0.1);
                        break;
                    case LEGENDARY:
                    case COSMIC:
                    case INSANE:
                        serverWorld.spawnParticle(ParticleTypes.FLASH, 
                                playerIn.getPosX(), playerIn.getPosY() + 1.0, playerIn.getPosZ(), 
                                1, 0, 0, 0, 0);
                        serverWorld.spawnParticle(ParticleTypes.END_ROD, 
                                playerIn.getPosX(), playerIn.getPosY() + 1.0, playerIn.getPosZ(), 
                                100, 1.0, 1.0, 1.0, 0.2);
                        break;
                }
            }
            
            // If not in creative mode, consume the item
            if (!playerIn.abilities.isCreativeMode) {
                itemStack.shrink(1);
            }
            
            // Give rewards
            giveRewards(worldIn, playerIn);
            
            return ActionResult.resultSuccess(itemStack);
        }
        
        /**
         * Provides rewards based on the box rarity
         */
        private void giveRewards(World world, PlayerEntity player) {
            // Announce opening
            player.sendMessage(
                    new StringTextComponent("Opening " + boxRarity.getDisplayName() + " Box...").mergeStyle(boxRarity.getTextColor()),
                    player.getUniqueID());
            
            // Different rewards based on rarity
            switch (boxRarity) {
                case COMMON:
                    giveCommonRewards(world, player);
                    break;
                case RARE:
                    giveRareRewards(world, player);
                    break;
                case EPIC:
                    giveEpicRewards(world, player);
                    break;
                case LEGENDARY:
                    giveLegendaryRewards(world, player);
                    break;
                case COSMIC:
                    giveCosmicRewards(world, player);
                    break;
                case INSANE:
                    giveInsaneRewards(world, player);
                    break;
            }
        }
        
        private void giveCommonRewards(World world, PlayerEntity player) {
            // 50% chance for each item
            if (random.nextBoolean()) {
                player.addItemStackToInventory(new ItemStack(ModItems.ENCHANTED_METAL.get(), random.nextInt(3) + 1));
            }
            
            if (random.nextBoolean()) {
                player.addItemStackToInventory(new ItemStack(ModItems.COSMIC_FRAGMENT.get(), random.nextInt(2) + 1));
            }
            
            // Small chance (10%) for rare box
            if (random.nextFloat() < 0.1f) {
                player.addItemStackToInventory(new ItemStack(ModItems.RARE_BOX.get(), 1));
                player.sendMessage(
                        new StringTextComponent("Lucky! You got a Rare Box!").mergeStyle(BoxRarity.RARE.getTextColor()),
                        player.getUniqueID());
            }
        }
        
        private void giveRareRewards(World world, PlayerEntity player) {
            // Always give some cosmic fragments
            player.addItemStackToInventory(new ItemStack(ModItems.COSMIC_FRAGMENT.get(), random.nextInt(4) + 2));
            
            // 75% chance for enchanted metal
            if (random.nextFloat() < 0.75f) {
                player.addItemStackToInventory(new ItemStack(ModItems.ENCHANTED_METAL.get(), random.nextInt(4) + 2));
            }
            
            // 50% chance for cosmic pearl
            if (random.nextBoolean()) {
                player.addItemStackToInventory(new ItemStack(ModItems.COSMIC_PEARL.get(), 1));
            }
            
            // Small chance (20%) for epic box
            if (random.nextFloat() < 0.2f) {
                player.addItemStackToInventory(new ItemStack(ModItems.EPIC_BOX.get(), 1));
                player.sendMessage(
                        new StringTextComponent("Amazing! You got an Epic Box!").mergeStyle(BoxRarity.EPIC.getTextColor()),
                        player.getUniqueID());
            }
        }
        
        private void giveEpicRewards(World world, PlayerEntity player) {
            // Always give some cosmic pearls
            player.addItemStackToInventory(new ItemStack(ModItems.COSMIC_PEARL.get(), random.nextInt(2) + 1));
            
            // 80% chance for ultimate ingot
            if (random.nextFloat() < 0.8f) {
                player.addItemStackToInventory(new ItemStack(ModItems.ULTIMATE_INGOT.get(), 1));
            }
            
            // 30% chance for legendary box
            if (random.nextFloat() < 0.3f) {
                player.addItemStackToInventory(new ItemStack(ModItems.LEGENDARY_BOX.get(), 1));
                player.sendMessage(
                        new StringTextComponent("Incredible! You got a Legendary Box!").mergeStyle(BoxRarity.LEGENDARY.getTextColor()),
                        player.getUniqueID());
            }
        }
        
        private void giveLegendaryRewards(World world, PlayerEntity player) {
            // Always give ultimate ingots
            player.addItemStackToInventory(new ItemStack(ModItems.ULTIMATE_INGOT.get(), random.nextInt(2) + 1));
            
            // 70% chance for a random infinity stone
            if (random.nextFloat() < 0.7f) {
                // Choose a random stone
                ItemStack stoneStack = null;
                int stoneChoice = random.nextInt(6);
                
                switch (stoneChoice) {
                    case 0:
                        stoneStack = new ItemStack(ModItems.SPACE_STONE.get());
                        break;
                    case 1:
                        stoneStack = new ItemStack(ModItems.MIND_STONE.get());
                        break;
                    case 2:
                        stoneStack = new ItemStack(ModItems.REALITY_STONE.get());
                        break;
                    case 3:
                        stoneStack = new ItemStack(ModItems.POWER_STONE.get());
                        break;
                    case 4:
                        stoneStack = new ItemStack(ModItems.TIME_STONE.get());
                        break;
                    case 5:
                        stoneStack = new ItemStack(ModItems.SOUL_STONE.get());
                        break;
                }
                
                if (stoneStack != null) {
                    player.addItemStackToInventory(stoneStack);
                    player.sendMessage(
                            new StringTextComponent("Extraordinary! You found an Infinity Stone!").mergeStyle(TextFormatting.LIGHT_PURPLE),
                            player.getUniqueID());
                }
            }
            
            // 20% chance for cosmic box
            if (random.nextFloat() < 0.2f) {
                player.addItemStackToInventory(new ItemStack(ModItems.COSMIC_BOX.get(), 1));
                player.sendMessage(
                        new StringTextComponent("Unbelievable! You got a Cosmic Box!").mergeStyle(BoxRarity.COSMIC.getTextColor()),
                        player.getUniqueID());
            }
        }
        
        private void giveCosmicRewards(World world, PlayerEntity player) {
            // Always give good items
            player.addItemStackToInventory(new ItemStack(ModItems.ULTIMATE_INGOT.get(), random.nextInt(3) + 2));
            
            // 50% chance for a random infinity stone
            if (random.nextBoolean()) {
                // Choose a random stone
                ItemStack stoneStack = null;
                int stoneChoice = random.nextInt(6);
                
                switch (stoneChoice) {
                    case 0:
                        stoneStack = new ItemStack(ModItems.SPACE_STONE.get());
                        break;
                    case 1:
                        stoneStack = new ItemStack(ModItems.MIND_STONE.get());
                        break;
                    case 2:
                        stoneStack = new ItemStack(ModItems.REALITY_STONE.get());
                        break;
                    case 3:
                        stoneStack = new ItemStack(ModItems.POWER_STONE.get());
                        break;
                    case 4:
                        stoneStack = new ItemStack(ModItems.TIME_STONE.get());
                        break;
                    case 5:
                        stoneStack = new ItemStack(ModItems.SOUL_STONE.get());
                        break;
                }
                
                if (stoneStack != null) {
                    player.addItemStackToInventory(stoneStack);
                }
            }
            
            // 50% chance for an Insane Craft weapon
            if (random.nextBoolean()) {
                // Choose a random weapon
                ItemStack weaponStack = null;
                int weaponChoice = random.nextInt(3);
                
                switch (weaponChoice) {
                    case 0:
                        weaponStack = new ItemStack(ModItems.ROYAL_GUARDIAN_SWORD.get());
                        break;
                    case 1:
                        weaponStack = new ItemStack(ModItems.ULTIMATE_BOW.get());
                        break;
                    case 2:
                        weaponStack = new ItemStack(ModItems.THOR_HAMMER.get());
                        break;
                }
                
                if (weaponStack != null) {
                    player.addItemStackToInventory(weaponStack);
                    player.sendMessage(
                            new StringTextComponent("Cosmic power grants you a legendary weapon!").mergeStyle(TextFormatting.AQUA),
                            player.getUniqueID());
                }
            }
            
            // 10% chance for insane box
            if (random.nextFloat() < 0.1f) {
                player.addItemStackToInventory(new ItemStack(ModItems.INSANE_BOX.get(), 1));
                player.sendMessage(
                        new StringTextComponent("IMPOSSIBLE! You got an Insane Box!").mergeStyle(BoxRarity.INSANE.getTextColor()),
                        player.getUniqueID());
            }
        }
        
        private void giveInsaneRewards(World world, PlayerEntity player) {
            // This is the ultimate box, always give amazing rewards
            
            // Always give an infinity gauntlet
            player.addItemStackToInventory(new ItemStack(ModItems.INFINITY_GAUNTLET.get(), 1));
            
            // Always give multiple ultimate ingots
            player.addItemStackToInventory(new ItemStack(ModItems.ULTIMATE_INGOT.get(), random.nextInt(5) + 5));
            
            // Choose a random insane weapon (guaranteed)
            ItemStack weaponStack = null;
            int weaponChoice = random.nextInt(3);
            
            switch (weaponChoice) {
                case 0:
                    weaponStack = new ItemStack(ModItems.ROYAL_GUARDIAN_SWORD.get());
                    break;
                case 1:
                    weaponStack = new ItemStack(ModItems.ULTIMATE_BOW.get());
                    break;
                case 2:
                    weaponStack = new ItemStack(ModItems.THOR_HAMMER.get());
                    break;
            }
            
            if (weaponStack != null) {
                player.addItemStackToInventory(weaponStack);
            }
            
            // 50% chance for a full infinity armor set
            if (random.nextBoolean()) {
                player.addItemStackToInventory(new ItemStack(ModItems.INFINITY_HELMET.get(), 1));
                player.addItemStackToInventory(new ItemStack(ModItems.INFINITY_CHESTPLATE.get(), 1));
                player.addItemStackToInventory(new ItemStack(ModItems.INFINITY_LEGGINGS.get(), 1));
                player.addItemStackToInventory(new ItemStack(ModItems.INFINITY_BOOTS.get(), 1));
                
                player.sendMessage(
                        new StringTextComponent("The power of the Insane Box grants you a full Infinity Armor set!").mergeStyle(TextFormatting.GOLD),
                        player.getUniqueID());
            }
            
            // Give a random number (1-3) of infinity stones
            int numStones = random.nextInt(3) + 1;
            List<Integer> chosenStones = new java.util.ArrayList<>();
            
            for (int i = 0; i < numStones; i++) {
                int stoneChoice;
                do {
                    stoneChoice = random.nextInt(6);
                } while (chosenStones.contains(stoneChoice));
                
                chosenStones.add(stoneChoice);
                
                ItemStack stoneStack = null;
                switch (stoneChoice) {
                    case 0:
                        stoneStack = new ItemStack(ModItems.SPACE_STONE.get());
                        break;
                    case 1:
                        stoneStack = new ItemStack(ModItems.MIND_STONE.get());
                        break;
                    case 2:
                        stoneStack = new ItemStack(ModItems.REALITY_STONE.get());
                        break;
                    case 3:
                        stoneStack = new ItemStack(ModItems.POWER_STONE.get());
                        break;
                    case 4:
                        stoneStack = new ItemStack(ModItems.TIME_STONE.get());
                        break;
                    case 5:
                        stoneStack = new ItemStack(ModItems.SOUL_STONE.get());
                        break;
                }
                
                if (stoneStack != null) {
                    player.addItemStackToInventory(stoneStack);
                }
            }
            
            player.sendMessage(
                    new StringTextComponent("The Insane Box's power is beyond comprehension! Incredible rewards are yours!").mergeStyle(TextFormatting.RED, TextFormatting.BOLD),
                    player.getUniqueID());
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent(boxRarity.getDisplayName() + " Mystery Box").mergeStyle(boxRarity.getTextColor()));
            tooltip.add(new StringTextComponent("Right click to open").mergeStyle(TextFormatting.GRAY));
            
            switch (boxRarity) {
                case COMMON:
                    tooltip.add(new StringTextComponent("Contains basic resources").mergeStyle(TextFormatting.GRAY));
                    break;
                case RARE:
                    tooltip.add(new StringTextComponent("Contains valuable resources").mergeStyle(TextFormatting.GRAY));
                    break;
                case EPIC:
                    tooltip.add(new StringTextComponent("Contains powerful items").mergeStyle(TextFormatting.GRAY));
                    break;
                case LEGENDARY:
                    tooltip.add(new StringTextComponent("May contain Infinity Stones!").mergeStyle(TextFormatting.YELLOW));
                    break;
                case COSMIC:
                    tooltip.add(new StringTextComponent("Contains extremely rare loot").mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new StringTextComponent("Chance for Insane Craft weapons!").mergeStyle(TextFormatting.YELLOW));
                    break;
                case INSANE:
                    tooltip.add(new StringTextComponent("Contains the most powerful").mergeStyle(TextFormatting.RED));
                    tooltip.add(new StringTextComponent("items in existence!").mergeStyle(TextFormatting.RED));
                    break;
            }
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
        
        public BoxRarity getBoxRarity() {
            return boxRarity;
        }
    }
}