package com.infinitystones.items.skiddzie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class ChaosStoneItem extends Item {
    
    private static final int COOLDOWN_TICKS = 200; // 10 seconds
    private static final int CHAOS_RADIUS = 16;
    private static final Random RANDOM = new Random();
    
    private static final int EFFECT_DURATION = 200; // 10 seconds
    
    public ChaosStoneItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            // Apply chaos effects
            applyChaosEffects((ServerWorld) world, player);
            
            // Set cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            // Play sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    private void applyChaosEffects(ServerWorld world, PlayerEntity player) {
        BlockPos playerPos = player.getPosition();
        
        // Create chaos particles in a sphere around the player
        for (int i = 0; i < 100; i++) {
            double angle1 = RANDOM.nextDouble() * Math.PI * 2;
            double angle2 = RANDOM.nextDouble() * Math.PI - Math.PI / 2;
            double radius = RANDOM.nextDouble() * CHAOS_RADIUS;
            
            double x = playerPos.getX() + radius * MathHelper.cos((float) angle2) * MathHelper.cos((float) angle1);
            double y = playerPos.getY() + 1.0 + radius * MathHelper.sin((float) angle2);
            double z = playerPos.getZ() + radius * MathHelper.cos((float) angle2) * MathHelper.sin((float) angle1);
            
            world.spawnParticle(ParticleTypes.PORTAL, x, y, z, 1, 0, 0, 0, 0);
        }
        
        // Get all entities within the chaos radius
        List<Entity> entities = world.getEntitiesWithinAABB(
                Entity.class, 
                new AxisAlignedBB(
                        playerPos.getX() - CHAOS_RADIUS, 
                        playerPos.getY() - CHAOS_RADIUS, 
                        playerPos.getZ() - CHAOS_RADIUS,
                        playerPos.getX() + CHAOS_RADIUS, 
                        playerPos.getY() + CHAOS_RADIUS, 
                        playerPos.getZ() + CHAOS_RADIUS
                ));
        
        // Apply random effects to entities
        for (Entity entity : entities) {
            if (entity instanceof PlayerEntity && entity != player) {
                PlayerEntity targetPlayer = (PlayerEntity) entity;
                
                // Choose a random effect
                switch (RANDOM.nextInt(6)) {
                    case 0:
                        targetPlayer.addPotionEffect(new EffectInstance(Effects.LEVITATION, EFFECT_DURATION, 1));
                        break;
                    case 1:
                        targetPlayer.addPotionEffect(new EffectInstance(Effects.BLINDNESS, EFFECT_DURATION, 0));
                        break;
                    case 2:
                        targetPlayer.addPotionEffect(new EffectInstance(Effects.WEAKNESS, EFFECT_DURATION, 1));
                        break;
                    case 3:
                        targetPlayer.addPotionEffect(new EffectInstance(Effects.NAUSEA, EFFECT_DURATION, 0));
                        break;
                    case 4:
                        // Random teleport within 8 blocks
                        double randX = targetPlayer.getPosX() + (RANDOM.nextDouble() - 0.5) * 16;
                        double randY = targetPlayer.getPosY() + (RANDOM.nextDouble() - 0.5) * 8;
                        double randZ = targetPlayer.getPosZ() + (RANDOM.nextDouble() - 0.5) * 16;
                        targetPlayer.setPositionAndUpdate(randX, randY, randZ);
                        break;
                    case 5:
                        // Random velocity
                        targetPlayer.setMotion(
                                (RANDOM.nextDouble() - 0.5) * 2,
                                RANDOM.nextDouble() * 1.5,
                                (RANDOM.nextDouble() - 0.5) * 2
                        );
                        break;
                }
            }
        }
        
        // Apply a random effect to the user (but less severe)
        switch (RANDOM.nextInt(5)) {
            case 0:
                player.addPotionEffect(new EffectInstance(Effects.SPEED, EFFECT_DURATION, 1));
                break;
            case 1:
                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, EFFECT_DURATION, 1));
                break;
            case 2:
                player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, EFFECT_DURATION, 0));
                break;
            case 3:
                player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, EFFECT_DURATION, 0));
                break;
            case 4:
                player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, EFFECT_DURATION, 0));
                break;
        }
    }
}