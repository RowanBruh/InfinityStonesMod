package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class HermesWingedBoots extends Item {
    private static final int COOLDOWN_TICKS = 20 * 3; // 3 seconds cooldown
    private static final int FLIGHT_DURATION = 20 * 15; // 15 seconds of flight
    private static final int SUPER_SPEED_DURATION = 20 * 10; // 10 seconds of super speed
    
    // NBT tag keys
    private static final String TAG_FLIGHT_ACTIVE = "FlightActive";
    private static final String TAG_FLIGHT_END_TIME = "FlightEndTime";
    private static final String TAG_SPEED_ACTIVE = "SpeedActive";
    private static final String TAG_SPEED_END_TIME = "SpeedEndTime";
    
    public HermesWingedBoots(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (player.getCooldownTracker().hasCooldown(this)) {
            if (!world.isRemote) {
                player.sendMessage(new StringTextComponent("Hermes' Winged Boots need time to recharge!")
                        .mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
            return ActionResult.resultFail(stack);
        }
        
        if (!world.isRemote) {
            if (player.isSneaking()) {
                // Swift Travel - Grant super speed
                activateSuperSpeed(world, player, stack);
            } else {
                // Wings of Hermes - Grant temporary flight
                activateFlight(world, player, stack);
            }
            
            // Apply cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    private void activateFlight(World world, PlayerEntity player, ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        
        // Set flight active status and end time
        nbt.putBoolean(TAG_FLIGHT_ACTIVE, true);
        nbt.putLong(TAG_FLIGHT_END_TIME, world.getGameTime() + FLIGHT_DURATION);
        
        // Grant flight capability
        player.abilities.allowFlying = true;
        player.abilities.isFlying = true;
        player.sendPlayerAbilities();
        
        // Add additional jump boost effect for better control
        player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, FLIGHT_DURATION, 2));
        player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, FLIGHT_DURATION + 100, 0));
        
        // Visual effects
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, 
                    player.getPosX(), player.getPosY(), player.getPosZ(), 
                    30, 0.5, 0.2, 0.5, 0.1);
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1.0F, 1.0F);
        
        // Message
        player.sendMessage(new StringTextComponent("The wings of Hermes grant you flight for 15 seconds!")
                .mergeStyle(TextFormatting.AQUA), player.getUniqueID());
    }
    
    private void activateSuperSpeed(World world, PlayerEntity player, ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        
        // Set speed active status and end time
        nbt.putBoolean(TAG_SPEED_ACTIVE, true);
        nbt.putLong(TAG_SPEED_END_TIME, world.getGameTime() + SUPER_SPEED_DURATION);
        
        // Grant speed effect
        player.addPotionEffect(new EffectInstance(Effects.SPEED, SUPER_SPEED_DURATION, 5)); // Level 6 speed
        player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, SUPER_SPEED_DURATION, 1));
        player.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, SUPER_SPEED_DURATION, 0)); // Water speed
        
        // Visual effects
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, 
                    player.getPosX(), player.getPosY(), player.getPosZ(), 
                    20, 0.3, 0.1, 0.3, 0.05);
            
            // Trailing particles along the ground
            BlockPos playerPos = player.getPosition();
            for (int i = -5; i <= 5; i++) {
                ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, 
                        playerPos.getX() - i, playerPos.getY(), playerPos.getZ(), 
                        5, 0.2, 0.1, 0.2, 0.01);
            }
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 1.0F, 1.5F);
        
        // Message
        player.sendMessage(new StringTextComponent("Hermes grants you supernatural speed for 10 seconds!")
                .mergeStyle(TextFormatting.AQUA), player.getUniqueID());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, world, entity, itemSlot, isSelected);
        
        if (!world.isRemote && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            CompoundNBT nbt = stack.getOrCreateTag();
            
            // Handle flight ability
            boolean flightActive = nbt.getBoolean(TAG_FLIGHT_ACTIVE);
            long flightEndTime = nbt.getLong(TAG_FLIGHT_END_TIME);
            
            if (flightActive) {
                // Create wing particle effects
                if (world instanceof ServerWorld && world.getGameTime() % 4 == 0) {
                    createWingParticles((ServerWorld) world, player);
                }
                
                // Check if flight time is over
                if (flightEndTime <= world.getGameTime()) {
                    // End flight
                    nbt.putBoolean(TAG_FLIGHT_ACTIVE, false);
                    
                    // Remove flight unless in creative mode
                    if (!player.isCreative()) {
                        player.abilities.allowFlying = false;
                        player.abilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                    
                    // Message
                    player.sendMessage(new StringTextComponent("The wings of Hermes fade away.")
                            .mergeStyle(TextFormatting.GRAY), player.getUniqueID());
                    
                    // Sound effect
                    world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                            SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 0.5F, 0.5F);
                }
            }
            
            // Handle super speed
            boolean speedActive = nbt.getBoolean(TAG_SPEED_ACTIVE);
            long speedEndTime = nbt.getLong(TAG_SPEED_END_TIME);
            
            if (speedActive) {
                // Create speed particle effects
                if (world instanceof ServerWorld && world.getGameTime() % 2 == 0) {
                    createSpeedParticles((ServerWorld) world, player);
                }
                
                // Check if super speed time is over
                if (speedEndTime <= world.getGameTime()) {
                    // End super speed
                    nbt.putBoolean(TAG_SPEED_ACTIVE, false);
                    
                    // Message
                    player.sendMessage(new StringTextComponent("Your supernatural speed fades away.")
                            .mergeStyle(TextFormatting.GRAY), player.getUniqueID());
                }
            }
        }
    }
    
    private void createWingParticles(ServerWorld world, PlayerEntity player) {
        Random rand = world.rand;
        
        // Wing shapes on the player's back
        float wingWidth = 0.8f;
        float wingHeight = 0.6f;
        
        // Calculate facing direction
        double yaw = Math.toRadians(player.rotationYaw);
        Vector3d forward = new Vector3d(-Math.sin(yaw), 0, Math.cos(yaw));
        Vector3d right = new Vector3d(Math.cos(yaw), 0, Math.sin(yaw));
        
        // Left wing
        for (int i = 0; i < 3; i++) {
            double offsetX = -right.x * wingWidth * (i / 3.0);
            double offsetY = player.getHeight() * 0.8 - (i * 0.1);
            double offsetZ = -right.z * wingWidth * (i / 3.0);
            
            Vector3d pos = player.getPositionVec().add(
                    offsetX - forward.x * 0.2, 
                    offsetY, 
                    offsetZ - forward.z * 0.2);
            
            world.spawnParticle(ParticleTypes.CLOUD, 
                    pos.x, pos.y, pos.z, 
                    1, 0.05, 0.05, 0.05, 0);
        }
        
        // Right wing
        for (int i = 0; i < 3; i++) {
            double offsetX = right.x * wingWidth * (i / 3.0);
            double offsetY = player.getHeight() * 0.8 - (i * 0.1);
            double offsetZ = right.z * wingWidth * (i / 3.0);
            
            Vector3d pos = player.getPositionVec().add(
                    offsetX - forward.x * 0.2, 
                    offsetY, 
                    offsetZ - forward.z * 0.2);
            
            world.spawnParticle(ParticleTypes.CLOUD, 
                    pos.x, pos.y, pos.z, 
                    1, 0.05, 0.05, 0.05, 0);
        }
    }
    
    private void createSpeedParticles(ServerWorld world, PlayerEntity player) {
        // Create trail of particles behind the player
        Vector3d lookVec = player.getLookVec();
        Vector3d motion = player.getMotion();
        
        // Only create particles if player is moving
        if (Math.abs(motion.x) > 0.1 || Math.abs(motion.z) > 0.1) {
            for (int i = 1; i <= 3; i++) {
                Vector3d pos = player.getPositionVec().add(
                        -motion.normalize().x * i * 0.5, 
                        0.1, 
                        -motion.normalize().z * i * 0.5);
                
                world.spawnParticle(ParticleTypes.CLOUD, 
                        pos.x, pos.y, pos.z, 
                        1, 0.1, 0.1, 0.1, 0);
            }
        }
        
        // Particles around feet when landing or jumping
        if (player.isOnGround() && (player.prevPosY > player.getPosY() || player.isSprinting())) {
            world.spawnParticle(ParticleTypes.CLOUD, 
                    player.getPosX(), player.getPosY() + 0.1, player.getPosZ(), 
                    3, 0.2, 0.0, 0.2, 0.01);
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        tooltip.add(new StringTextComponent("Right-click: Wings of Hermes (Temporary Flight)").mergeStyle(TextFormatting.GOLD));
        tooltip.add(new StringTextComponent("Shift+Right-click: Swift Travel (Super Speed)").mergeStyle(TextFormatting.GOLD));
        tooltip.add(new StringTextComponent("").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent("\"Swift as thought, the boots of the messenger god.\"").mergeStyle(TextFormatting.ITALIC, TextFormatting.AQUA));
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; // Always show enchantment glint
    }
}