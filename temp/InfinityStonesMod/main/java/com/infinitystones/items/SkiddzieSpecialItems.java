package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Special items inspired by SkiddziePlays videos
 */
public class SkiddzieSpecialItems {
    private static final Random random = new Random();
    
    /**
     * Ultra Grappling Hook - allows players to pull themselves toward the target location
     */
    public static class UltraGrapplingHook extends Item {
        private static final int MAX_RANGE = 50;
        
        public UltraGrapplingHook() {
            super(new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC));
        }
        
        @Override
        public ActionResultType onItemUse(ItemUseContext context) {
            World world = context.getWorld();
            PlayerEntity player = context.getPlayer();
            BlockPos pos = context.getPos();
            
            if (player == null) return ActionResultType.PASS;
            
            double distance = player.getPosition().distanceSq(pos);
            
            // Check if block is in range
            if (distance > MAX_RANGE * MAX_RANGE) {
                if (!world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Target too far away!").mergeStyle(TextFormatting.RED),
                            player.getUniqueID());
                }
                return ActionResultType.FAIL;
            }
            
            if (!world.isRemote) {
                // Calculate direction vector from player to target
                double dx = pos.getX() + 0.5 - player.getPosX();
                double dy = pos.getY() + 0.5 - player.getPosY();
                double dz = pos.getZ() + 0.5 - player.getPosZ();
                
                // Normalize and apply velocity
                double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
                double speed = 1.5; // Adjust speed as needed
                
                player.setMotion(
                        dx / length * speed,
                        dy / length * speed + 0.2, // Add a little upward boost
                        dz / length * speed);
                
                player.fallDistance = 0; // Cancel fall damage
                
                // Play sound
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 0.5F);
                
                // Add particle effect
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                            ParticleTypes.CRIT,
                            player.getPosX(), player.getPosY() + 1, player.getPosZ(),
                            30, 0.5, 0.5, 0.5, 0.1);
                }
                
                // Set cooldown
                player.getCooldownTracker().setCooldown(this, 40); // 2 second cooldown
            }
            
            return ActionResultType.SUCCESS;
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Ultra Grappling Hook").mergeStyle(TextFormatting.AQUA));
            tooltip.add(new StringTextComponent("Right-click on blocks to zip toward them!").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent("Range: " + MAX_RANGE + " blocks").mergeStyle(TextFormatting.YELLOW));
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
    
    /**
     * Gravity Hammer - pushes away all entities when used
     */
    public static class GravityHammer extends Item {
        private static final int RADIUS = 8;
        private static final float KNOCKBACK_POWER = 2.5F;
        
        public GravityHammer() {
            super(new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC));
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack itemStack = playerIn.getHeldItem(handIn);
            
            if (!worldIn.isRemote) {
                // Find all entities in radius
                List<Entity> entities = worldIn.getEntitiesWithinAABBExcludingEntity(
                        playerIn, 
                        new AxisAlignedBB(
                                playerIn.getPosX() - RADIUS, playerIn.getPosY() - RADIUS, playerIn.getPosZ() - RADIUS,
                                playerIn.getPosX() + RADIUS, playerIn.getPosY() + RADIUS, playerIn.getPosZ() + RADIUS));
                
                int affected = 0;
                
                // Push all entities away
                for (Entity entity : entities) {
                    double dx = entity.getPosX() - playerIn.getPosX();
                    double dz = entity.getPosZ() - playerIn.getPosZ();
                    
                    // Calculate horizontal distance
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance < 0.1) distance = 0.1; // Avoid division by zero
                    
                    // Calculate power based on distance (closer = more power)
                    double power = KNOCKBACK_POWER * (1.0 - (distance / RADIUS));
                    
                    // Apply knockback
                    entity.addVelocity(
                            (dx / distance) * power,
                            0.5, // Some upward force
                            (dz / distance) * power);
                    entity.velocityChanged = true;
                    
                    // Apply small damage to living entities
                    if (entity instanceof LivingEntity) {
                        entity.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), 2.0F);
                    }
                    
                    affected++;
                }
                
                // Visual and sound effects
                ((ServerWorld) worldIn).spawnParticle(
                        ParticleTypes.EXPLOSION, 
                        playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                        1, 0, 0, 0, 0);
                
                ((ServerWorld) worldIn).spawnParticle(
                        ParticleTypes.POOF, 
                        playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                        50, RADIUS, 0.5, RADIUS, 0.1);
                
                worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                
                // Player message
                if (affected > 0) {
                    playerIn.sendMessage(
                            new StringTextComponent("Gravity Hammer pushed away " + affected + " entities!").mergeStyle(TextFormatting.AQUA),
                            playerIn.getUniqueID());
                }
                
                // Set cooldown
                playerIn.getCooldownTracker().setCooldown(this, 100); // 5 seconds
            }
            
            return ActionResult.resultSuccess(itemStack);
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Gravity Hammer").mergeStyle(TextFormatting.AQUA));
            tooltip.add(new StringTextComponent("Right-click to push away all nearby entities").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent("Range: " + RADIUS + " blocks").mergeStyle(TextFormatting.YELLOW));
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
    
    /**
     * Lightning Rod - calls down lightning on a target location
     */
    public static class LightningRod extends Item {
        private static final int MAX_RANGE = 64;
        
        public LightningRod() {
            super(new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC));
        }
        
        @Override
        public ActionResultType onItemUse(ItemUseContext context) {
            World world = context.getWorld();
            PlayerEntity player = context.getPlayer();
            BlockPos pos = context.getPos();
            
            if (player == null) return ActionResultType.PASS;
            
            // Check if we're in a dimension where lightning can strike
            if (!world.getDimensionType().hasSkyLight()) {
                if (!world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Lightning cannot strike here!").mergeStyle(TextFormatting.RED),
                            player.getUniqueID());
                }
                return ActionResultType.FAIL;
            }
            
            double distance = player.getPosition().distanceSq(pos);
            
            // Check if block is in range
            if (distance > MAX_RANGE * MAX_RANGE) {
                if (!world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Target too far away!").mergeStyle(TextFormatting.RED),
                            player.getUniqueID());
                }
                return ActionResultType.FAIL;
            }
            
            if (!world.isRemote) {
                // Find the highest block at this position to strike with lightning
                BlockPos strikePos = world.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE, pos);
                
                // Create lightning bolt
                LightningBoltEntity lightning = new LightningBoltEntity(world.getEntityType(), world);
                lightning.moveForced(strikePos.getX() + 0.5, strikePos.getY(), strikePos.getZ() + 0.5);
                lightning.setCaster(player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null);
                world.addEntity(lightning);
                
                // Set cooldown
                player.getCooldownTracker().setCooldown(context.getItem().getItem(), 100); // 5 seconds
                
                // Play sound at player location
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            
            return ActionResultType.SUCCESS;
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent("Lightning Rod").mergeStyle(TextFormatting.AQUA));
            tooltip.add(new StringTextComponent("Right-click to call down lightning").mergeStyle(TextFormatting.GRAY));
            tooltip.add(new StringTextComponent("Range: " + MAX_RANGE + " blocks").mergeStyle(TextFormatting.YELLOW));
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
}