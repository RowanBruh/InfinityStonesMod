package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AresSword extends SwordItem {
    
    private static final int COOLDOWN_TICKS = 100; // 5 seconds cooldown
    private static final int RAGE_DURATION = 160; // 8 seconds of rage
    private static final int BLOODLUST_RADIUS = 5; // Bloodlust effect radius
    
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("b4b67a25-4e2a-4b99-8d7c-8a81e684a171");
    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("f2b0d9a2-3c76-4e37-9f2a-9d02711b9579");
    
    private final Random random = new Random();

    public AresSword(Item.Properties properties) {
        // Passing tier values directly instead of using a Tier implementation
        super(new WarTier(), 8, -2.4F, properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        
        // Check if player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultPass(itemStack);
        }
        
        // Activate rage mode
        if (!world.isRemote) {
            // Trigger bloodlust effect
            activateBloodlust(world, player);
            
            // Damage the item
            if (!player.abilities.isCreativeMode) {
                itemStack.damageItem(3, player, (p) -> p.sendBreakAnimation(hand));
            }
            
            // Add cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        }
        
        // Visual and sound effects
        player.swingArm(hand);
        world.playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK, 
                SoundCategory.PLAYERS, 0.8F, 1.0F);
        
        return ActionResult.resultConsume(itemStack);
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.world;
        
        if (!world.isRemote && attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            
            // 30% chance to set the target on fire
            if (random.nextFloat() < 0.3f) {
                target.setFire(3); // 3 seconds of fire
                
                // Visual effect to indicate fire damage
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                        ParticleTypes.FLAME,
                        target.getPosX(), target.getPosY() + target.getHeight() / 2, target.getPosZ(),
                        15, 0.3, 0.3, 0.3, 0.02);
                }
            }
            
            // 15% chance to apply bleeding effect (wither damage)
            if (random.nextFloat() < 0.15f) {
                target.addPotionEffect(new EffectInstance(Effects.WITHER, 60, 1));
                
                // Blood particle effect
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    for (int i = 0; i < 10; i++) {
                        double offsetX = random.nextDouble() * 0.6 - 0.3;
                        double offsetY = random.nextDouble() * 0.6 - 0.3;
                        double offsetZ = random.nextDouble() * 0.6 - 0.3;
                        
                        serverWorld.spawnParticle(
                            ParticleTypes.DAMAGE_INDICATOR,
                            target.getPosX() + offsetX, 
                            target.getPosY() + target.getHeight() / 2 + offsetY, 
                            target.getPosZ() + offsetZ,
                            1, 0, 0, 0, 0);
                    }
                }
            }
            
            // Small chance to apply combat frenzy to the attacker
            if (random.nextFloat() < 0.1f) {
                player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 60, 0));
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 60, 0));
            }
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        
        if (isSelected && entity instanceof PlayerEntity && world.isRemote && random.nextFloat() < 0.05f) {
            // Blood dripping effect when held
            PlayerEntity player = (PlayerEntity) entity;
            Vector3d look = player.getLookVec();
            
            double offsetX = random.nextDouble() * 0.4 - 0.2;
            double offsetY = random.nextDouble() * 0.4 - 0.2;
            double offsetZ = random.nextDouble() * 0.4 - 0.2;
            
            double posX = player.getPosX() + look.x * 0.8 + offsetX;
            double posY = player.getPosY() + player.getEyeHeight() - 0.3 + offsetY;
            double posZ = player.getPosZ() + look.z * 0.8 + offsetZ;
            
            world.addParticle(
                ParticleTypes.DRIPPING_LAVA,
                posX, posY, posZ,
                0, 0, 0);
        }
    }
    
    /**
     * Activates the bloodlust effect
     */
    private void activateBloodlust(World world, PlayerEntity player) {
        // Apply rage effects to the player
        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, RAGE_DURATION, 2)); // Strength III
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, RAGE_DURATION, 1)); // Resistance II
        player.addPotionEffect(new EffectInstance(Effects.SPEED, RAGE_DURATION, 1)); // Speed II
        
        // Create a battlefield aura that affects nearby entities (enemies get weakness, allies get strength)
        BlockPos playerPos = player.getPosition();
        AxisAlignedBB area = new AxisAlignedBB(
            playerPos.getX() - BLOODLUST_RADIUS, playerPos.getY() - BLOODLUST_RADIUS, playerPos.getZ() - BLOODLUST_RADIUS,
            playerPos.getX() + BLOODLUST_RADIUS, playerPos.getY() + BLOODLUST_RADIUS, playerPos.getZ() + BLOODLUST_RADIUS);
        
        List<LivingEntity> nearbyEntities = world.getEntitiesWithinAABB(LivingEntity.class, area, 
            entity -> entity != player && entity.isAlive());
        
        for (LivingEntity entity : nearbyEntities) {
            // Apply different effects based on whether entity is hostile to player
            if (entity instanceof PlayerEntity) {
                // Assume other players are allies
                entity.addPotionEffect(new EffectInstance(Effects.STRENGTH, RAGE_DURATION / 2, 0)); // Strength I
            } else {
                // Assume non-players are enemies
                entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, RAGE_DURATION, 0)); // Weakness I
                
                // 20% chance to scare enemies (make them flee)
                if (random.nextFloat() < 0.2f) {
                    entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, RAGE_DURATION / 2, 0)); // Make them slow
                }
            }
        }
        
        // Create visual and sound effects
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Blood surge particles
            serverWorld.spawnParticle(
                ParticleTypes.DAMAGE_INDICATOR,
                player.getPosX(), player.getPosY() + 1.0, player.getPosZ(),
                50, 2.0, 2.0, 2.0, 0.1);
            
            // Rage particles
            serverWorld.spawnParticle(
                ParticleTypes.ANGRY_VILLAGER,
                player.getPosX(), player.getPosY() + 2.0, player.getPosZ(),
                10, 0.5, 0.5, 0.5, 0.1);
            
            // Ground-shaking effect
            for (int i = 0; i < 20; i++) {
                double offsetX = random.nextDouble() * BLOODLUST_RADIUS * 2 - BLOODLUST_RADIUS;
                double offsetZ = random.nextDouble() * BLOODLUST_RADIUS * 2 - BLOODLUST_RADIUS;
                
                BlockPos effectPos = playerPos.add(offsetX, 0, offsetZ);
                serverWorld.spawnParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    effectPos.getX() + 0.5, effectPos.getY(), effectPos.getZ() + 0.5,
                    1, 0, 0.1, 0, 0);
            }
        }
        
        // Battle roar sound
        world.playSound(null, player.getPosition(), SoundEvents.ENTITY_RAVAGER_ROAR, 
                SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
    
    /**
     * Custom tier for the Ares sword
     */
    private static class WarTier implements net.minecraft.item.IItemTier {
        @Override
        public int getMaxUses() {
            return 1500;
        }

        @Override
        public float getEfficiency() {
            return 0; // Not used for weapons
        }

        @Override
        public float getAttackDamage() {
            return 8.0F;
        }

        @Override
        public int getHarvestLevel() {
            return 0; // Not used for weapons
        }

        @Override
        public int getEnchantability() {
            return 22;
        }

        @Override
        public net.minecraft.util.LazyValue<net.minecraft.item.crafting.Ingredient> getRepairMaterial() {
            return new net.minecraft.util.LazyValue<>(() -> net.minecraft.item.crafting.Ingredient.EMPTY);
        }
    }
}