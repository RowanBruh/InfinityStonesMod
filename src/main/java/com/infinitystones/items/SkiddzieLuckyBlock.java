package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Special Lucky Block inspired by SkiddziePlays
 */
public class SkiddzieLuckyBlock extends Block {
    private static final Random random = new Random();
    
    public SkiddzieLuckyBlock() {
        super(Block.Properties.create(Material.IRON)
                .hardnessAndResistance(1.0f, 5.0f)
                .sound(SoundType.METAL)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2)
                .setLightLevel(state -> 7)); // Light level of 7
    }
    
    /**
     * Defines what happens when the block is broken
     */
    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote) {
            triggerLuckyEffect(worldIn, pos, player);
        }
        
        super.onBlockHarvested(worldIn, pos, state, player);
    }
    
    /**
     * Trigger a random lucky effect
     */
    private void triggerLuckyEffect(World world, BlockPos pos, PlayerEntity player) {
        ServerWorld serverWorld = (ServerWorld) world;
        
        // Play sound
        world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        // Add particle effect
        serverWorld.spawnParticle(
                ParticleTypes.EXPLOSION, 
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                1, 0, 0, 0, 0);
        
        // Choose a random effect (1-20)
        int effect = random.nextInt(20) + 1;
        
        switch (effect) {
            case 1:
                // Spawn a random Infinity Stone
                spawnRandomInfinityStone(world, pos);
                break;
            case 2:
                // Lightning strike
                LightningBoltEntity lightning = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
                lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
                world.addEntity(lightning);
                break;
            case 3:
                // Give player random potion effects
                player.addPotionEffect(new net.minecraft.potion.EffectInstance(net.minecraft.potion.Effects.SPEED, 600, 2));
                player.addPotionEffect(new net.minecraft.potion.EffectInstance(net.minecraft.potion.Effects.JUMP_BOOST, 600, 2));
                player.addPotionEffect(new net.minecraft.potion.EffectInstance(net.minecraft.potion.Effects.RESISTANCE, 600, 1));
                player.sendMessage(
                        new StringTextComponent("The Lucky Block granted you special powers!").mergeStyle(TextFormatting.GREEN),
                        player.getUniqueID());
                break;
            case 4:
                // Spawn hostile mobs
                for (int i = 0; i < 3; i++) {
                    ZombieEntity zombie = new ZombieEntity(world);
                    zombie.setPosition(
                            pos.getX() + random.nextDouble() * 2 - 1,
                            pos.getY(),
                            pos.getZ() + random.nextDouble() * 2 - 1);
                    world.addEntity(zombie);
                }
                player.sendMessage(
                        new StringTextComponent("Uh oh! The Lucky Block was cursed!").mergeStyle(TextFormatting.RED),
                        player.getUniqueID());
                break;
            case 5:
                // Spawn creeper trap
                CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
                creeper.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                world.addEntity(creeper);
                break;
            case 6:
                // Give rare crafting materials
                dropItem(world, pos, new ItemStack(ModItems.ULTIMATE_INGOT.get(), random.nextInt(3) + 1));
                dropItem(world, pos, new ItemStack(ModItems.COSMIC_PEARL.get(), random.nextInt(2) + 1));
                player.sendMessage(
                        new StringTextComponent("The Lucky Block drops valuable materials!").mergeStyle(TextFormatting.AQUA),
                        player.getUniqueID());
                break;
            case 7:
                // Spawn a bunch of common boxes
                for (int i = 0; i < 5; i++) {
                    dropItem(world, pos, new ItemStack(ModItems.COMMON_BOX.get(), 1));
                }
                player.sendMessage(
                        new StringTextComponent("Mystery boxes appear!").mergeStyle(TextFormatting.WHITE),
                        player.getUniqueID());
                break;
            case 8:
                // Spawn a rare/epic box
                if (random.nextBoolean()) {
                    dropItem(world, pos, new ItemStack(ModItems.RARE_BOX.get(), 1));
                } else {
                    dropItem(world, pos, new ItemStack(ModItems.EPIC_BOX.get(), 1));
                }
                player.sendMessage(
                        new StringTextComponent("A special box materializes!").mergeStyle(TextFormatting.BLUE),
                        player.getUniqueID());
                break;
            case 9:
                // Teleport the player randomly
                for (int i = 0; i < 16; i++) {
                    int x = pos.getX() + (random.nextInt(64) - 32);
                    int z = pos.getZ() + (random.nextInt(64) - 32);
                    int y = world.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE, x, z);
                    
                    // Check if the position is safe
                    if (!world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() &&
                        !world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial().blocksMovement()) {
                        player.setPositionAndUpdate(x + 0.5, y + 1, z + 0.5);
                        player.sendMessage(
                                new StringTextComponent("The Lucky Block teleported you to a random location!").mergeStyle(TextFormatting.LIGHT_PURPLE),
                                player.getUniqueID());
                        break;
                    }
                }
                break;
            case 10:
                // Jackpot - spawn a legendary box
                dropItem(world, pos, new ItemStack(ModItems.LEGENDARY_BOX.get(), 1));
                player.sendMessage(
                        new StringTextComponent("JACKPOT! You found a Legendary Box!").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD),
                        player.getUniqueID());
                break;
            case 11:
                // Give player SkiddziePlays special items
                dropItem(world, pos, new ItemStack(ModItems.ULTRA_GRAPPLING_HOOK.get(), 1));
                player.sendMessage(
                        new StringTextComponent("The Lucky Block grants you a special tool!").mergeStyle(TextFormatting.AQUA),
                        player.getUniqueID());
                break;
            case 12:
                // Give player Gravity Hammer
                dropItem(world, pos, new ItemStack(ModItems.GRAVITY_HAMMER.get(), 1));
                player.sendMessage(
                        new StringTextComponent("The Lucky Block grants you a powerful weapon!").mergeStyle(TextFormatting.DARK_PURPLE),
                        player.getUniqueID());
                break;
            case 13:
                // Give player Lightning Rod
                dropItem(world, pos, new ItemStack(ModItems.LIGHTNING_ROD.get(), 1));
                player.sendMessage(
                        new StringTextComponent("The power to control lightning is now yours!").mergeStyle(TextFormatting.YELLOW),
                        player.getUniqueID());
                break;
            case 14:
                // More lucky blocks!
                for (int i = 0; i < 3; i++) {
                    dropItem(world, pos, new ItemStack(ModItems.SKIDZIE_LUCKY_BLOCK_ITEM.get(), 1));
                }
                player.sendMessage(
                        new StringTextComponent("More Lucky Blocks appear!").mergeStyle(TextFormatting.GREEN),
                        player.getUniqueID());
                break;
            case 15:
                // Explosion trap (non-destructive)
                world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                        2.0F, false, net.minecraft.world.Explosion.Mode.NONE);
                player.sendMessage(
                        new StringTextComponent("BOOM! The Lucky Block was trapped!").mergeStyle(TextFormatting.RED),
                        player.getUniqueID());
                break;
            case 16:
                // Insane Craft weapon
                int weapon = random.nextInt(3);
                ItemStack weaponStack = null;
                switch (weapon) {
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
                    dropItem(world, pos, weaponStack);
                    player.sendMessage(
                            new StringTextComponent("An Insane Craft weapon appears!").mergeStyle(TextFormatting.GOLD),
                            player.getUniqueID());
                }
                break;
            case 17:
                // Random piece of Infinity Armor
                int armor = random.nextInt(4);
                ItemStack armorStack = null;
                switch (armor) {
                    case 0:
                        armorStack = new ItemStack(ModItems.INFINITY_HELMET.get());
                        break;
                    case 1:
                        armorStack = new ItemStack(ModItems.INFINITY_CHESTPLATE.get());
                        break;
                    case 2:
                        armorStack = new ItemStack(ModItems.INFINITY_LEGGINGS.get());
                        break;
                    case 3:
                        armorStack = new ItemStack(ModItems.INFINITY_BOOTS.get());
                        break;
                }
                if (armorStack != null) {
                    dropItem(world, pos, armorStack);
                    player.sendMessage(
                            new StringTextComponent("A piece of Infinity Armor materializes!").mergeStyle(TextFormatting.DARK_PURPLE),
                            player.getUniqueID());
                }
                break;
            case 18:
                // Rain of experience orbs
                for (int i = 0; i < 20; i++) {
                    player.giveExperiencePoints(50); // Give total of 1000 XP
                }
                serverWorld.spawnParticle(
                        ParticleTypes.TOTEM_OF_UNDYING, 
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        50, 1.0, 1.0, 1.0, 0.5);
                player.sendMessage(
                        new StringTextComponent("Knowledge flows into your mind!").mergeStyle(TextFormatting.GREEN),
                        player.getUniqueID());
                break;
            case 19:
                // Super rare - drop Infinity Gauntlet
                dropItem(world, pos, new ItemStack(ModItems.INFINITY_GAUNTLET.get(), 1));
                serverWorld.spawnParticle(
                        ParticleTypes.FLASH, 
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 0);
                player.sendMessage(
                        new StringTextComponent("INCREDIBLE! The Infinity Gauntlet is yours!").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD),
                        player.getUniqueID());
                break;
            case 20:
                // Ultra rare - Cosmic Box
                dropItem(world, pos, new ItemStack(ModItems.COSMIC_BOX.get(), 1));
                serverWorld.spawnParticle(
                        ParticleTypes.END_ROD, 
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        100, 1.0, 1.0, 1.0, 0.05);
                player.sendMessage(
                        new StringTextComponent("COSMIC POWER! A Cosmic Box appears!").mergeStyle(TextFormatting.AQUA, TextFormatting.BOLD),
                        player.getUniqueID());
                break;
        }
    }
    
    /**
     * Helper method to spawn a random infinity stone
     */
    private void spawnRandomInfinityStone(World world, BlockPos pos) {
        int stoneChoice = random.nextInt(6);
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
            dropItem(world, pos, stoneStack);
        }
    }
    
    /**
     * Helper method to drop an item at a location
     */
    private void dropItem(World world, BlockPos pos, ItemStack stack) {
        double offsetX = random.nextDouble() * 0.8 + 0.1;
        double offsetY = random.nextDouble() * 0.8 + 0.1;
        double offsetZ = random.nextDouble() * 0.8 + 0.1;
        
        ItemEntity itemEntity = new ItemEntity(
                world, 
                pos.getX() + offsetX, 
                pos.getY() + offsetY, 
                pos.getZ() + offsetZ, 
                stack);
        
        itemEntity.setMotion(
                random.nextGaussian() * 0.05, 
                random.nextGaussian() * 0.05 + 0.2, 
                random.nextGaussian() * 0.05);
        
        world.addEntity(itemEntity);
    }
    
    /**
     * Item class for the Lucky Block
     */
    public static class SkiddzieLuckyBlockItem extends BlockItem {
        public SkiddzieLuckyBlockItem(Block block) {
            super(block, new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .rarity(Rarity.EPIC));
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("SkiddziePlays Lucky Block").mergeStyle(TextFormatting.GOLD));
            tooltip.add(new StringTextComponent("Break it to receive random rewards or punishments!").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent("May contain Infinity Stones or special items").mergeStyle(TextFormatting.AQUA));
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
}