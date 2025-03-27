package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
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

public class AthenaSpear extends Item {
    
    private static final int COOLDOWN_TICKS = 60; // 3 seconds cooldown
    private static final int SHIELD_DURATION = 80; // 4 seconds shield
    private static final int WISDOM_XP_AMOUNT = 10; // XP granted by wisdom
    
    private final Random random = new Random();

    public AthenaSpear(Properties properties) {
        super(properties);
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Allow blocking for a long time
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        
        // Different action based on sneak state
        if (player.isSneaking()) {
            // Shield mode when sneaking
            player.setActiveHand(hand);
            return ActionResult.resultConsume(itemStack);
        } else {
            // Attack mode when not sneaking
            // Check if player is on cooldown
            if (player.getCooldownTracker().hasCooldown(this)) {
                return ActionResult.resultPass(itemStack);
            }
            
            // Add cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            if (!world.isRemote) {
                // Grant wisdom (XP) to the player
                grantWisdom(player);
                
                // Damage the item
                if (!player.abilities.isCreativeMode) {
                    itemStack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
                }
            }
            
            // Visual and sound effects
            player.swingArm(hand);
            
            return ActionResult.resultConsume(itemStack);
        }
    }
    
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if (entity instanceof PlayerEntity && count % 5 == 0) { // Every 5 ticks
            PlayerEntity player = (PlayerEntity) entity;
            World world = player.world;
            
            // Shield effect particles
            if (world.isRemote) {
                // Create shield visualization
                double radius = 2.0;
                double yOffset = 0.5;
                for (int i = 0; i < 3; i++) {
                    double angle = random.nextDouble() * Math.PI * 2;
                    double x = player.getPosX() + Math.cos(angle) * radius * random.nextDouble();
                    double y = player.getPosY() + yOffset + random.nextDouble() * 2 - 1;
                    double z = player.getPosZ() + Math.sin(angle) * radius * random.nextDouble();
                    
                    world.addParticle(
                        ParticleTypes.END_ROD,
                        x, y, z, 0, 0, 0);
                }
            }
            
            // Block projectiles and reflect them
            if (!world.isRemote && count % 10 == 0) { // Check every 10 ticks
                reflectProjectiles(world, player);
            }
        }
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity && !world.isRemote) {
            PlayerEntity player = (PlayerEntity) entity;
            
            // Grant brief resistance after using shield
            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, SHIELD_DURATION, 1));
            
            // Sound for shield deactivation
            world.playSound(null, player.getPosition(), SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 
                    0.8F, 1.2F);
            
            // Add cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS / 2); // Half the normal cooldown
        }
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Special effect when hitting enemies with the spear
        World world = attacker.world;
        
        if (!world.isRemote && attacker instanceof PlayerEntity) {
            // 20% chance to grant the target slowness (strategic disadvantage)
            if (random.nextFloat() < 0.2f) {
                target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 60, 1));
                
                // Visual effect to show the strategic debuff
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                        ParticleTypes.ENTITY_EFFECT,
                        target.getPosX(), target.getPosY() + target.getHeight() / 2, target.getPosZ(),
                        15, 0.2, 0.2, 0.2, 0.02);
                }
            }
            
            // Small chance to grant attacker brief strength (strategic advantage)
            if (random.nextFloat() < 0.1f) {
                attacker.addPotionEffect(new EffectInstance(Effects.STRENGTH, 60, 0));
            }
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    /**
     * Grants wisdom (XP) to the player
     */
    private void grantWisdom(PlayerEntity player) {
        // Add experience to the player
        player.giveExperiencePoints(WISDOM_XP_AMOUNT);
        
        // Play sound effect
        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 
                0.5F, 1.0F);
        
        // Show particles
        if (player.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) player.world;
            serverWorld.spawnParticle(
                ParticleTypes.ENCHANT,
                player.getPosX(), player.getPosY() + 1.0, player.getPosZ(),
                20, 0.5, 0.5, 0.5, 0.1);
        }
        
        // Briefly boost various mental attributes
        player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 200, 0));
        
        // 25% chance to grant brief haste (inspiration)
        if (random.nextFloat() < 0.25f) {
            player.addPotionEffect(new EffectInstance(Effects.HASTE, 100, 0));
        }
    }
    
    /**
     * Reflects projectiles near the player
     */
    private void reflectProjectiles(World world, PlayerEntity player) {
        // Define the area to check for projectiles
        double radius = 2.5;
        AxisAlignedBB area = new AxisAlignedBB(
            player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
            player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius);
        
        // Find all projectiles in the area
        List<ProjectileEntity> projectiles = world.getEntitiesWithinAABB(ProjectileEntity.class, area, 
            projectile -> projectile.getShooter() != player && !projectile.isOnGround());
        
        if (!projectiles.isEmpty()) {
            for (ProjectileEntity projectile : projectiles) {
                // Reflect velocity
                Vector3d motion = projectile.getMotion().scale(-1.0);
                projectile.setMotion(motion);
                
                // Make it owned by the player
                projectile.setShooter(player);
                
                // Add visual effect
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                        ParticleTypes.CRIT,
                        projectile.getPosX(), projectile.getPosY(), projectile.getPosZ(),
                        10, 0.1, 0.1, 0.1, 0.1);
                }
            }
            
            // Play sound effect
            world.playSound(null, player.getPosition(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 
                    1.0F, 1.5F);
        }
    }
}