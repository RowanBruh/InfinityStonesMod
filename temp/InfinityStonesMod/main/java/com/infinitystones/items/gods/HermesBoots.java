package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.effect.LightningBoltEntity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.UUID;

public class HermesBoots extends ArmorItem {
    
    private static final int COOLDOWN_TICKS = 80; // 4 seconds cooldown
    private static final int DASH_DURATION = 20; // 1 second dash
    private static final float DASH_VELOCITY = 2.0F; // Speed multiplier for dash
    private static final int FLIGHT_DURATION = 200; // 10 seconds of flight
    
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("9e50f801-7b4f-4ec5-a959-f20f547c9a8f");
    private static final String NBT_FLIGHT_ACTIVE = "FlightActive";
    private static final String NBT_FLIGHT_TIME = "FlightTime";
    
    private final Random random = new Random();

    public HermesBoots(Item.Properties properties) {
        super(new SwiftArmorMaterial(), EquipmentSlotType.FEET, properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Check if player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultPass(stack);
        }
        
        // If already wearing boots, provide dash
        ItemStack feetSlot = player.getItemStackFromSlot(EquipmentSlotType.FEET);
        if (!feetSlot.isEmpty() && feetSlot.getItem() instanceof HermesBoots) {
            // Activate dash
            dash(world, player);
            
            // Add cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            return ActionResult.resultConsume(stack);
        }
        
        // If not wearing boots, equip them
        if (feetSlot.isEmpty()) {
            player.setItemStackToSlot(EquipmentSlotType.FEET, stack.copy());
            stack.shrink(1);
            return ActionResult.resultConsume(stack);
        }
        
        return ActionResult.resultPass(stack);
    }
    
    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        // Apply permanent speed boost while wearing
        if (!world.isRemote) {
            // Basic speed effect
            if (!player.isPotionActive(Effects.SPEED)) {
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 10, 1, false, false));
            }
            
            // Check for flight status
            CompoundNBT nbt = stack.getOrCreateTag();
            if (nbt.getBoolean(NBT_FLIGHT_ACTIVE)) {
                int flightTime = nbt.getInt(NBT_FLIGHT_TIME);
                
                if (flightTime > 0) {
                    // Allow flight
                    player.abilities.allowFlying = true;
                    
                    // Decrement flight time only when flying
                    if (player.abilities.isFlying) {
                        nbt.putInt(NBT_FLIGHT_TIME, flightTime - 1);
                        
                        // Create wing trail particles
                        if (world instanceof ServerWorld && world.getGameTime() % 2 == 0) {
                            createWingTrails((ServerWorld) world, player);
                        }
                    }
                } else {
                    // Flight time expired
                    if (!player.isCreative() && !player.isSpectator()) {
                        player.abilities.allowFlying = false;
                        player.abilities.isFlying = false;
                        player.sendPlayerAbilities();
                    }
                    nbt.putBoolean(NBT_FLIGHT_ACTIVE, false);
                }
            }
            
            // Double jump ability - handled on client side via key handler
            
            // Fall damage prevention
            player.fallDistance = 0.0F;
        }
        
        // Visual effects on both sides
        if (world.isRemote && player.distanceWalkedModified > player.prevDistanceWalkedModified && player.onGround) {
            // Footstep particles when running
            if (random.nextFloat() < 0.2f) {
                world.addParticle(
                    ParticleTypes.CLOUD,
                    player.getPosX(), player.getPosY() + 0.1, player.getPosZ(),
                    0, 0, 0);
            }
        }
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(NBT_FLIGHT_ACTIVE);
    }
    
    /**
     * Activates the dash ability
     */
    private void dash(World world, PlayerEntity player) {
        // Look direction dash
        Vector3d lookVec = player.getLookVec();
        
        // Calculate dash velocity
        double motionX = lookVec.x * DASH_VELOCITY;
        double motionY = player.isOnGround() ? 0.4 : lookVec.y * DASH_VELOCITY; // Small upward boost if on ground
        double motionZ = lookVec.z * DASH_VELOCITY;
        
        // Apply dash
        player.setMotion(motionX, motionY, motionZ);
        
        // Apply brief speed effect
        player.addPotionEffect(new EffectInstance(Effects.SPEED, DASH_DURATION, 3, false, false));
        
        // Visual and sound effects
        world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PHANTOM_FLAP, 
                SoundCategory.PLAYERS, 1.0F, 1.5F);
        
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Speed trail particles
            double offsetX = -lookVec.x * 0.5;
            double offsetY = 0.1;
            double offsetZ = -lookVec.z * 0.5;
            
            serverWorld.spawnParticle(
                ParticleTypes.CLOUD,
                player.getPosX() + offsetX, player.getPosY() + offsetY, player.getPosZ() + offsetZ,
                20, 0.2, 0.1, 0.2, 0.05);
        }
    }
    
    /**
     * Activates temporary flight ability
     */
    public void activateFlight(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isRemote) {
            CompoundNBT nbt = stack.getOrCreateTag();
            nbt.putBoolean(NBT_FLIGHT_ACTIVE, true);
            nbt.putInt(NBT_FLIGHT_TIME, FLIGHT_DURATION);
            
            // Enable flight
            player.abilities.allowFlying = true;
            player.sendPlayerAbilities();
            
            // Visual and sound effects
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PHANTOM_AMBIENT, 
                    SoundCategory.PLAYERS, 1.0F, 1.5F);
            
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                
                // Wings appearing effect
                serverWorld.spawnParticle(
                    ParticleTypes.END_ROD,
                    player.getPosX(), player.getPosY() + 1.0, player.getPosZ(),
                    30, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }
    
    /**
     * Creates wing trail particles while flying
     */
    private void createWingTrails(ServerWorld world, PlayerEntity player) {
        double yOffset = 1.0;
        double wingSpan = 0.8;
        
        for (int side = -1; side <= 1; side += 2) { // -1 for left, 1 for right
            double offsetX = MathHelper.sin(player.renderYawOffset * 0.017453292F) * side * wingSpan;
            double offsetZ = -MathHelper.cos(player.renderYawOffset * 0.017453292F) * side * wingSpan;
            
            world.spawnParticle(
                ParticleTypes.CLOUD,
                player.getPosX() + offsetX, player.getPosY() + yOffset, player.getPosZ() + offsetZ,
                1, 0, -0.1, 0, 0.02);
        }
    }
    
    /**
     * Custom armor material for Hermes boots
     */
    private static class SwiftArmorMaterial implements ArmorMaterial {
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return 1250;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return 3;
        }

        @Override
        public int getEnchantability() {
            return 25;
        }

        @Override
        public net.minecraft.util.SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_GOLD;
        }

        @Override
        public net.minecraft.item.crafting.Ingredient getRepairMaterial() {
            return net.minecraft.item.crafting.Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return "hermes";
        }

        @Override
        public float getToughness() {
            return 2.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    }
}