package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.UUID;

public class AresSword extends SwordItem {
    private static final int COOLDOWN_TICKS = 20 * 10; // 10 seconds cooldown
    private static final int BLOODLUST_DURATION = 20 * 20; // 20 seconds
    private static final float BASE_DAMAGE = 10.0f; // Base damage
    private static final float DAMAGE_PER_KILL = 1.0f; // Damage bonus per kill
    private static final int MAX_BLOODLUST_LEVEL = 5; // Maximum bloodlust level
    
    // NBT tag keys
    private static final String TAG_KILL_COUNT = "KillCount";
    private static final String TAG_BLOODLUST_LEVEL = "BloodlustLevel";
    private static final String TAG_BLOODLUST_TIME = "BloodlustTime";
    
    // Attack modifiers
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    
    public AresSword(Properties properties) {
        super(ItemTier.NETHERITE, 9, -2.4f, properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (player.getCooldownTracker().hasCooldown(this)) {
            if (!world.isRemote) {
                player.sendMessage(new StringTextComponent("Ares' Sword needs time to recharge!")
                        .mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
            return ActionResult.resultFail(stack);
        }
        
        if (!world.isRemote) {
            if (player.isSneaking()) {
                // Blood Sacrifice - Sacrifice health for temporary increased damage and speed
                performBloodSacrifice(world, player, stack);
            } else {
                // War Cry - AOE attack and intimidation
                performWarCry(world, player);
            }
            
            // Apply cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    private void performBloodSacrifice(World world, PlayerEntity player, ItemStack stack) {
        // Sacrifice health for power (20% of current health)
        float healthToSacrifice = player.getHealth() * 0.2f;
        
        // Don't let the player kill themselves
        if (player.getHealth() - healthToSacrifice <= 2.0f) {
            healthToSacrifice = player.getHealth() - 2.0f;
            if (healthToSacrifice <= 0) {
                player.sendMessage(new StringTextComponent("Your health is too low for a blood sacrifice!")
                        .mergeStyle(TextFormatting.RED), player.getUniqueID());
                return;
            }
        }
        
        // Apply damage to player
        player.attackEntityFrom(DamageSource.MAGIC, healthToSacrifice);
        
        // Get or create NBT
        CompoundNBT nbt = stack.getOrCreateTag();
        
        // Calculate bloodlust level based on health sacrificed
        int currentBloodlustLevel = nbt.getInt(TAG_BLOODLUST_LEVEL);
        int newBloodlustLevel = Math.min(currentBloodlustLevel + 1, MAX_BLOODLUST_LEVEL);
        
        // Update bloodlust level and time
        nbt.putInt(TAG_BLOODLUST_LEVEL, newBloodlustLevel);
        nbt.putLong(TAG_BLOODLUST_TIME, world.getGameTime() + BLOODLUST_DURATION);
        
        // Apply combat effects
        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, BLOODLUST_DURATION, newBloodlustLevel - 1));
        player.addPotionEffect(new EffectInstance(Effects.SPEED, BLOODLUST_DURATION, 1));
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, BLOODLUST_DURATION, 0));
        player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, BLOODLUST_DURATION, 0));
        
        // Visual effects
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnParticle(ParticleTypes.FLAME, 
                    player.getPosX(), player.getPosY() + 1, player.getPosZ(), 
                    50, 0.5, 1.0, 0.5, 0.1);
            
            // Blood particles
            ((ServerWorld) world).spawnParticle(ParticleTypes.CRIMSON_SPORE, 
                    player.getPosX(), player.getPosY() + 1, player.getPosZ(), 
                    30, 0.5, 1.0, 0.5, 0.05);
        }
        
        // Sound effects
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_WITHER_HURT, SoundCategory.PLAYERS, 1.0F, 0.5F);
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.8F);
        
        // Message
        player.sendMessage(new StringTextComponent("You sacrifice " + String.format("%.1f", healthToSacrifice) + 
                " health to Ares and reach Bloodlust level " + newBloodlustLevel + "!")
                .mergeStyle(TextFormatting.DARK_RED), player.getUniqueID());
    }
    
    private void performWarCry(World world, PlayerEntity player) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Create shockwave
            double radius = 8.0;
            List<LivingEntity> entities = GodPowerHelper.getEntitiesInRadius(
                    world, player.getPositionVec(), radius, 
                    LivingEntity.class, player, null);
            
            int entitiesHit = 0;
            
            for (LivingEntity entity : entities) {
                // Calculate damage based on distance
                double distance = entity.getDistanceSq(player);
                float damage = (float) (BASE_DAMAGE * (1.0 - Math.sqrt(distance) / radius));
                
                // Apply damage
                entity.attackEntityFrom(GodPowerHelper.ARES_DAMAGE, damage);
                
                // Knockback
                Vector3d knockbackDir = entity.getPositionVec().subtract(player.getPositionVec()).normalize();
                entity.addVelocity(
                        knockbackDir.x * 0.5,
                        0.2,
                        knockbackDir.z * 0.5
                );
                entity.velocityChanged = true;
                
                // Apply fear effect (slowness + weakness)
                entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20 * 10, 1));
                entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 20 * 10, 1));
                
                // Fire effect to some entities (30% chance)
                if (world.rand.nextFloat() < 0.3f) {
                    entity.setFire(5);
                }
                
                entitiesHit++;
            }
            
            // Visual effects - ring of fire particles expanding outward
            for (int i = 0; i < 360; i += 5) {
                double angle = Math.toRadians(i);
                double x = player.getPosX() + Math.cos(angle) * radius;
                double z = player.getPosZ() + Math.sin(angle) * radius;
                
                serverWorld.spawnParticle(ParticleTypes.FLAME, 
                        x, player.getPosY() + 0.1, z, 
                        1, 0, 0.1, 0, 0.05);
                
                // Additional particle lines connecting to the center
                for (double d = 1; d < radius; d += radius / 8) {
                    double lx = player.getPosX() + Math.cos(angle) * d;
                    double lz = player.getPosZ() + Math.sin(angle) * d;
                    
                    serverWorld.spawnParticle(ParticleTypes.SMOKE, 
                            lx, player.getPosY() + 0.5, lz, 
                            1, 0, 0.05, 0, 0.01);
                }
            }
            
            // Sound effects
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                    SoundEvents.ENTITY_RAVAGER_ROAR, SoundCategory.PLAYERS, 1.0F, 0.8F);
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.7F, 1.2F);
            
            // Message
            player.sendMessage(new StringTextComponent("Your war cry strikes fear into " + entitiesHit + " enemies!")
                    .mergeStyle(TextFormatting.RED), player.getUniqueID());
        }
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getEntityWorld();
        
        // Add fire effect on hit (50% chance)
        if (world.rand.nextFloat() < 0.5f) {
            target.setFire(4);
        }
        
        // Special particle effect on hit
        if (world instanceof ServerWorld && !world.isRemote) {
            ((ServerWorld) world).spawnParticle(ParticleTypes.LAVA, 
                    target.getPosX(), target.getPosY() + target.getHeight() / 2, target.getPosZ(), 
                    10, 0.3, 0.3, 0.3, 0.1);
        }
        
        // Check if target died from this hit
        if (target.getHealth() <= 0) {
            // Get or create NBT
            CompoundNBT nbt = stack.getOrCreateTag();
            
            // Update kill count
            int killCount = nbt.getInt(TAG_KILL_COUNT) + 1;
            nbt.putInt(TAG_KILL_COUNT, killCount);
            
            // Update bloodlust if it's active
            long bloodlustTime = nbt.getLong(TAG_BLOODLUST_TIME);
            if (bloodlustTime > world.getGameTime()) {
                // Kills during bloodlust restore some health
                if (attacker instanceof PlayerEntity) {
                    GodPowerHelper.healEntity((PlayerEntity) attacker, 2.0f);
                }
                
                // Special particle effect for kill during bloodlust
                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, 
                            target.getPosX(), target.getPosY() + target.getHeight() / 2, target.getPosZ(), 
                            20, 0.5, 0.5, 0.5, 0.1);
                }
            }
            
            // Send message about kill milestone every 5 kills
            if (killCount % 5 == 0 && attacker instanceof PlayerEntity) {
                ((PlayerEntity) attacker).sendMessage(
                        new StringTextComponent("Your blade has claimed " + killCount + " souls for Ares!")
                                .mergeStyle(TextFormatting.DARK_RED), attacker.getUniqueID());
            }
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        
        if (!world.isRemote && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            CompoundNBT nbt = stack.getOrCreateTag();
            
            // Check bloodlust status
            long bloodlustTime = nbt.getLong(TAG_BLOODLUST_TIME);
            int bloodlustLevel = nbt.getInt(TAG_BLOODLUST_LEVEL);
            
            // Handle bloodlust expiration
            if (bloodlustTime > 0 && bloodlustTime <= world.getGameTime() && bloodlustLevel > 0) {
                // Reset bloodlust
                nbt.putInt(TAG_BLOODLUST_LEVEL, 0);
                
                // Only notify if player is holding the sword
                if (isSelected) {
                    player.sendMessage(new StringTextComponent("The bloodlust fades from Ares' Sword.")
                            .mergeStyle(TextFormatting.GRAY), player.getUniqueID());
                }
            }
            
            // Update attack damage modifier based on kills if player is holding the sword
            if (isSelected || player.getHeldItemOffhand() == stack) {
                updateDamageModifier(stack, player);
            }
        }
    }
    
    private void updateDamageModifier(ItemStack stack, PlayerEntity player) {
        CompoundNBT nbt = stack.getOrCreateTag();
        int killCount = nbt.getInt(TAG_KILL_COUNT);
        
        // Calculate bonus damage from kills
        float bonusDamage = Math.min(killCount * DAMAGE_PER_KILL, 10.0f); // Cap at +10 damage
        
        // Add bloodlust bonus if active
        int bloodlustLevel = nbt.getInt(TAG_BLOODLUST_LEVEL);
        bonusDamage += bloodlustLevel * 2; // +2 damage per bloodlust level
        
        // Create modifier
        AttributeModifier modifier = new AttributeModifier(ATTACK_DAMAGE_MODIFIER, 
                "Ares' Sword bonus", bonusDamage, AttributeModifier.Operation.ADDITION);
        
        // Apply to player
        if (!player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(modifier)) {
            player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(ATTACK_DAMAGE_MODIFIER);
            player.getAttribute(Attributes.ATTACK_DAMAGE).applyNonPersistentModifier(modifier);
        }
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt != null) {
            long bloodlustTime = nbt.getLong(TAG_BLOODLUST_TIME);
            return bloodlustTime > 0 || true; // Always show enchantment glint
        }
        return true;
    }
}