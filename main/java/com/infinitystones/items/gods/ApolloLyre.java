package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class ApolloLyre extends Item {
    
    private static final int USE_DURATION = 72; // How long it can be used
    private static final int COOLDOWN_TICKS = 100; // 5 seconds cooldown
    private static final int HEAL_RANGE = 10; // Range for healing effect
    private static final int SOUND_DAMAGE_RANGE = 8; // Range for sound damage
    
    private final Random random = new Random();

    public ApolloLyre(Properties properties) {
        super(properties);
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW; // Using bow animation as it works well for holding
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        
        // Check if player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultPass(itemStack);
        }
        
        // Start using the item
        player.setActiveHand(hand);
        
        return ActionResult.resultConsume(itemStack);
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving) {
        if (!(entityLiving instanceof PlayerEntity)) {
            return stack;
        }
        
        PlayerEntity player = (PlayerEntity) entityLiving;
        
        // Add cooldown
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        
        // Different ability based on sneak state
        if (player.isSneaking()) {
            // Area damage to undead when sneaking
            if (!world.isRemote) {
                applySoundDamage(world, player);
                
                // Damage the item
                if (!player.abilities.isCreativeMode) {
                    stack.damageItem(2, player, (p) -> p.sendBreakAnimation(player.getActiveHand()));
                }
            }
        } else {
            // Healing music when not sneaking
            if (!world.isRemote) {
                applyHealingMusic(world, player);
                
                // Damage the item less for healing
                if (!player.abilities.isCreativeMode) {
                    stack.damageItem(1, player, (p) -> p.sendBreakAnimation(player.getActiveHand()));
                }
            }
        }
        
        return stack;
    }
    
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (player.world.isRemote && count % 6 == 0) { // Every 6 ticks (about 3 times per second)
            // Play note particles while using
            playNoteParticles(player);
            
            // Play sound effects while using
            if (count % 12 == 0) { // Every 12 ticks (about 1.5 times per second)
                player.world.playSound(
                    player.getPosX(), player.getPosY(), player.getPosZ(),
                    player.isSneaking() ? SoundEvents.BLOCK_NOTE_BLOCK_BASS : SoundEvents.BLOCK_NOTE_BLOCK_HARP,
                    SoundCategory.PLAYERS, 0.6F, getNoteFrequency(count),
                    false
                );
            }
        }
    }
    
    /**
     * Applies healing effect to nearby allies
     */
    private void applyHealingMusic(World world, PlayerEntity player) {
        // Define the affected area
        AxisAlignedBB healingArea = new AxisAlignedBB(
            player.getPosX() - HEAL_RANGE, player.getPosY() - 5, player.getPosZ() - HEAL_RANGE,
            player.getPosX() + HEAL_RANGE, player.getPosY() + 5, player.getPosZ() + HEAL_RANGE
        );
        
        // Get all living entities in range
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, healingArea, 
            entity -> entity.isAlive() && !(entity instanceof MonsterEntity));
        
        if (!entities.isEmpty()) {
            // For each entity, apply healing effects
            for (LivingEntity entity : entities) {
                // Calculate distance for potency
                double distance = entity.getDistanceSq(player);
                int potency = distance < 16 ? 1 : 0; // Higher potency when closer
                
                // Apply regeneration effect
                entity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, potency));
                
                // Clear negative effects
                entity.removePotionEffect(Effects.POISON);
                entity.removePotionEffect(Effects.WITHER);
                
                // Add visual effect
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                        ParticleTypes.NOTE,
                        entity.getPosX(), entity.getPosY() + entity.getHeight() + 0.5, entity.getPosZ(),
                        5, 0.3, 0.3, 0.3, 0);
                }
            }
            
            // Play music sound effect
            world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.PLAYERS, 
                    1.0F, 1.0F);
            world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.PLAYERS, 
                    1.0F, 1.0F);
        }
    }
    
    /**
     * Damages nearby undead mobs with sound
     */
    private void applySoundDamage(World world, PlayerEntity player) {
        // Define the affected area
        AxisAlignedBB damageArea = new AxisAlignedBB(
            player.getPosX() - SOUND_DAMAGE_RANGE, player.getPosY() - 5, player.getPosZ() - SOUND_DAMAGE_RANGE,
            player.getPosX() + SOUND_DAMAGE_RANGE, player.getPosY() + 5, player.getPosZ() + SOUND_DAMAGE_RANGE
        );
        
        // Get all monsters in range
        List<MonsterEntity> monsters = world.getEntitiesWithinAABB(MonsterEntity.class, damageArea, 
            entity -> entity.isAlive());
        
        if (!monsters.isEmpty()) {
            // For each monster, apply damage based on type
            for (MonsterEntity monster : monsters) {
                // Calculate distance for damage falloff
                double distance = monster.getDistanceSq(player);
                float damage = 8.0f * (1.0f - (float)Math.min(1.0, distance / (SOUND_DAMAGE_RANGE * SOUND_DAMAGE_RANGE)));
                
                // Undead take more damage
                boolean isUndead = GodPowerHelper.isEntityType(monster, "zombie") || 
                                   GodPowerHelper.isEntityType(monster, "skeleton") || 
                                   GodPowerHelper.isEntityType(monster, "wither");
                if (isUndead) {
                    damage *= 1.5f;
                }
                
                // Apply damage
                monster.attackEntityFrom(GodPowerHelper.causeGodPowerDamage(player, "apollo"), damage);
                
                // Stun effect (slow and weakness)
                monster.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 2));
                monster.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 1));
                
                // Add visual effect
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    for (int i = 0; i < 10; i++) {
                        serverWorld.spawnParticle(
                            ParticleTypes.NOTE,
                            monster.getPosX() + random.nextFloat() - 0.5, 
                            monster.getPosY() + monster.getHeight() / 2, 
                            monster.getPosZ() + random.nextFloat() - 0.5,
                            1, 0, 0, 0, 0);
                    }
                }
            }
            
            // Play damaging sound effect
            world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 
                    1.5F, 0.5F);
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 
                    1.0F, 1.5F);
        }
    }
    
    /**
     * Helper method to create note particles
     */
    private void playNoteParticles(LivingEntity player) {
        World world = player.world;
        
        // Different particle types based on sneak state
        for (int i = 0; i < 3; i++) {
            float offsetX = (random.nextFloat() - 0.5f) * 2;
            float offsetY = random.nextFloat() * 2;
            float offsetZ = (random.nextFloat() - 0.5f) * 2;
            
            world.addParticle(
                player.isSneaking() ? ParticleTypes.CRIT : ParticleTypes.NOTE,
                player.getPosX() + offsetX, 
                player.getPosY() + 1 + offsetY,
                player.getPosZ() + offsetZ,
                random.nextFloat() * 0.3, 0, 0);
        }
    }
    
    /**
     * Gets a musical frequency based on count
     */
    private float getNoteFrequency(int count) {
        // Generate different musical notes based on count
        // This creates a sequence of 5 different notes that repeat
        float[] notes = { 0.5f, 0.6f, 0.8f, 1.0f, 1.2f };
        return notes[(USE_DURATION - count) / 12 % notes.length];
    }
}