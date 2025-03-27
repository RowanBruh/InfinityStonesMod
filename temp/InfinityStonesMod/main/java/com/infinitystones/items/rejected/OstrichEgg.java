package com.infinitystones.items.rejected;

import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

/**
 * Ostrich Egg - based on the rejected Ostrich mob from Minecraft Earth.
 * In this implementation, it spawns a special fast horse that represents an ostrich.
 */
public class OstrichEgg extends Item {

    public OstrichEgg() {
        super(new Item.Properties()
                .group(ModItemGroups.ROWAN_INDUSTRIES)
                .maxStackSize(16));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        ItemStack stack = context.getItem();
        
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }
        
        // Check if there's space to spawn a horse
        if (world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2))) {
            // Create an "ostrich" (a specially configured horse)
            HorseEntity horse = EntityType.HORSE.create(world);
            if (horse != null) {
                // Position the horse
                horse.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                
                // Configure the horse to be like an ostrich (fast, high jump, can't use horse armor)
                horse.setCustomName(new StringTextComponent("Ostrich"));
                horse.setCustomNameVisible(true);
                
                // Set attributes to make it more ostrich-like
                horse.getAttribute(net.minecraft.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(0.4D); // Much faster than normal horses
                horse.getAttribute(net.minecraft.entity.ai.attributes.Attributes.JUMP_STRENGTH).setBaseValue(1.0D); // High jump
                
                // Make it tamed and set the player as owner
                horse.setTamedBy(player);
                horse.setHorseTamed(true);
                
                // Give it a unique appearance
                horse.setHorseVariant(1); // White horse
                
                // Add potion effects for unique appearance
                horse.addPotionEffect(new EffectInstance(Effects.SPEED, Integer.MAX_VALUE, 1, false, false));
                
                // Spawn the "ostrich" in the world
                world.addEntity(horse);
                
                // Play a hatching sound
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS,
                        1.0F, 1.0F);
                
                // Spawn particles
                for (int i = 0; i < 20; i++) {
                    double d0 = pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 1.5;
                    double d1 = pos.getY() + 1.2 + (world.rand.nextDouble() - 0.5) * 1.5;
                    double d2 = pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 1.5;
                    world.addParticle(ParticleTypes.EGG_CRACK, d0, d1, d2, 0, 0, 0);
                }
                
                // Consume the egg in non-creative mode
                if (player != null && !player.isCreative()) {
                    stack.shrink(1);
                }
                
                player.sendStatusMessage(new StringTextComponent("An Ostrich has hatched! You can ride it without a saddle."), true);
                
                return ActionResultType.SUCCESS;
            }
        }
        
        return ActionResultType.PASS;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            player.sendStatusMessage(new StringTextComponent("Place the Ostrich Egg on the ground to hatch it."), true);
        }
        
        return ActionResult.resultPass(stack);
    }
}