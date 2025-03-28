package com.infinitystones.items.skiddzie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GravityManipulatorItem extends Item {
    
    private static final int COOLDOWN_TICKS = 20; // 1 second
    private static final int GRAVITY_RADIUS = 12; // blocks
    
    private static final String NBT_MODE = "GravityMode";
    
    public enum GravityMode {
        NORMAL,
        ANTI_GRAVITY,
        LOW_GRAVITY,
        HIGH_GRAVITY
    }
    
    public GravityManipulatorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote && isSelected && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            
            // Only apply the continuous gravity effects when the item is actively being held
            if (player.getHeldItemMainhand() == stack || player.getHeldItemOffhand() == stack) {
                GravityMode mode = getCurrentMode(stack);
                
                if (mode != GravityMode.NORMAL && world.getGameTime() % 5 == 0) {
                    applyGravityEffects((ServerWorld) world, player, mode);
                }
            }
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return ActionResult.resultSuccess(stack);
        }
        
        // Cycle through gravity modes
        GravityMode currentMode = getCurrentMode(stack);
        GravityMode nextMode = cycleGravityMode(currentMode);
        
        // Update NBT data
        setCurrentMode(stack, nextMode);
        
        // Visual and sound effects
        createModeChangeEffects((ServerWorld) world, player, nextMode);
        
        // Notify player
        notifyModeChange(player, nextMode);
        
        // Set cooldown
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        
        return ActionResult.resultSuccess(stack);
    }
    
    private GravityMode getCurrentMode(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains(NBT_MODE)) {
            int modeOrdinal = tag.getInt(NBT_MODE);
            GravityMode[] modes = GravityMode.values();
            if (modeOrdinal >= 0 && modeOrdinal < modes.length) {
                return modes[modeOrdinal];
            }
        }
        return GravityMode.NORMAL;
    }
    
    private void setCurrentMode(ItemStack stack, GravityMode mode) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt(NBT_MODE, mode.ordinal());
    }
    
    private GravityMode cycleGravityMode(GravityMode currentMode) {
        switch (currentMode) {
            case NORMAL:
                return GravityMode.ANTI_GRAVITY;
            case ANTI_GRAVITY:
                return GravityMode.LOW_GRAVITY;
            case LOW_GRAVITY:
                return GravityMode.HIGH_GRAVITY;
            case HIGH_GRAVITY:
            default:
                return GravityMode.NORMAL;
        }
    }
    
    private void applyGravityEffects(ServerWorld world, PlayerEntity player, GravityMode mode) {
        BlockPos playerPos = player.getPosition();
        
        // Get all entities within the gravity field
        List<Entity> entities = world.getEntitiesWithinAABB(
                Entity.class,
                new AxisAlignedBB(
                        playerPos.getX() - GRAVITY_RADIUS,
                        playerPos.getY() - GRAVITY_RADIUS,
                        playerPos.getZ() - GRAVITY_RADIUS,
                        playerPos.getX() + GRAVITY_RADIUS,
                        playerPos.getY() + GRAVITY_RADIUS,
                        playerPos.getZ() + GRAVITY_RADIUS
                )).stream()
                .filter(e -> e != player) // Exclude the player using the item
                .filter(e -> e.getDistanceSq(player) <= GRAVITY_RADIUS * GRAVITY_RADIUS) // Ensure circular area
                .collect(Collectors.toList());
        
        // Apply gravity effects to entities based on the mode
        for (Entity entity : entities) {
            // Skip entities that can't be affected
            if (!canAffectEntity(entity)) continue;
            
            // Get current motion
            double motionX = entity.getMotion().x;
            double motionY = entity.getMotion().y;
            double motionZ = entity.getMotion().z;
            
            switch (mode) {
                case ANTI_GRAVITY:
                    // Reverse gravity - lift entities up
                    entity.setMotion(motionX, Math.max(0.1, motionY + 0.08), motionZ);
                    entity.fallDistance = 0;
                    break;
                    
                case LOW_GRAVITY:
                    // Low gravity - slow falling
                    if (motionY < 0) {
                        entity.setMotion(motionX, motionY * 0.4, motionZ);
                        entity.fallDistance = 0;
                    }
                    break;
                    
                case HIGH_GRAVITY:
                    // High gravity - faster falling and reduced jump height
                    if (motionY <= 0) {
                        entity.setMotion(motionX, motionY * 1.5, motionZ);
                    } else if (motionY > 0) {
                        entity.setMotion(motionX, motionY * 0.7, motionZ);
                    }
                    break;
            }
            
            // Calculate distance from player to entity for particle intensity
            double distance = Math.sqrt(entity.getDistanceSq(player));
            double normalizedDistance = 1.0 - (distance / GRAVITY_RADIUS);
            
            // Only spawn particles occasionally based on distance
            if (world.rand.nextDouble() < normalizedDistance * 0.1) {
                spawnGravityParticles(world, entity, mode);
            }
        }
    }
    
    private boolean canAffectEntity(Entity entity) {
        // Skip entities that shouldn't be affected
        return entity instanceof LivingEntity || entity instanceof ItemEntity;
    }
    
    private void createModeChangeEffects(ServerWorld world, PlayerEntity player, GravityMode mode) {
        BlockPos playerPos = player.getPosition();
        
        // Play sound effect
        world.playSound(null, playerPos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1.0F, getModePitch(mode));
        
        // Create particles around the player
        for (int i = 0; i < 50; i++) {
            double angle = world.rand.nextDouble() * Math.PI * 2;
            double radius = world.rand.nextDouble() * 2;
            double x = playerPos.getX() + Math.cos(angle) * radius;
            double y = playerPos.getY() + world.rand.nextDouble() * 2;
            double z = playerPos.getZ() + Math.sin(angle) * radius;
            
            world.spawnParticle(getModeParticle(mode), x, y, z, 1, 0, 0, 0, 0.05);
        }
    }
    
    private void spawnGravityParticles(ServerWorld world, Entity entity, GravityMode mode) {
        if (world.rand.nextInt(10) != 0) return; // Reduce particle frequency
        
        double x = entity.getPosX() + (world.rand.nextDouble() - 0.5) * entity.getWidth();
        double y = entity.getPosY() + (world.rand.nextDouble() - 0.5) * entity.getHeight();
        double z = entity.getPosZ() + (world.rand.nextDouble() - 0.5) * entity.getWidth();
        
        double motionY = 0;
        switch (mode) {
            case ANTI_GRAVITY:
                motionY = 0.1;
                break;
            case LOW_GRAVITY:
                motionY = 0.05;
                break;
            case HIGH_GRAVITY:
                motionY = -0.1;
                break;
        }
        
        world.spawnParticle(getModeParticle(mode), x, y, z, 1, 0, motionY, 0, 0.05);
    }
    
    private net.minecraft.particles.ParticleType getModeParticle(GravityMode mode) {
        switch (mode) {
            case ANTI_GRAVITY:
                return ParticleTypes.END_ROD;
            case LOW_GRAVITY:
                return ParticleTypes.CLOUD;
            case HIGH_GRAVITY:
                return ParticleTypes.SMOKE;
            default:
                return ParticleTypes.PORTAL;
        }
    }
    
    private float getModePitch(GravityMode mode) {
        switch (mode) {
            case ANTI_GRAVITY:
                return 2.0F;
            case LOW_GRAVITY:
                return 1.5F;
            case HIGH_GRAVITY:
                return 0.5F;
            default:
                return 1.0F;
        }
    }
    
    private void notifyModeChange(PlayerEntity player, GravityMode mode) {
        StringTextComponent message;
        switch (mode) {
            case NORMAL:
                message = new StringTextComponent("Gravity Manipulator: Normal Gravity");
                message.mergeStyle(TextFormatting.GRAY);
                break;
            case ANTI_GRAVITY:
                message = new StringTextComponent("Gravity Manipulator: Anti-Gravity");
                message.mergeStyle(TextFormatting.AQUA);
                break;
            case LOW_GRAVITY:
                message = new StringTextComponent("Gravity Manipulator: Low Gravity");
                message.mergeStyle(TextFormatting.GREEN);
                break;
            case HIGH_GRAVITY:
                message = new StringTextComponent("Gravity Manipulator: High Gravity");
                message.mergeStyle(TextFormatting.RED);
                break;
            default:
                return;
        }
        player.sendMessage(message, UUID.randomUUID());
    }
}