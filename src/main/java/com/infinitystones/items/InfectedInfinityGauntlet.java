package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfectedInfinityStones.InfectedStoneItem;
import com.infinitystones.items.InfinityStones.StoneType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Infected Infinity Gauntlet - a chaotic, nano-tech corrupted version of the gauntlet
 * that provides enhanced but unstable powers.
 */
public class InfectedInfinityGauntlet extends Item {
    private static final String ACTIVE_TAG = "Active";
    private static final Random random = new Random();
    
    public InfectedInfinityGauntlet() {
        super(new Item.Properties()
                .group(InfinityStonesMod.ROWAN_INDUSTRIES)
                .maxStackSize(1)
                .setNoRepair()
                .rarity(Rarity.EPIC));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        CompoundNBT tag = stack.getOrCreateTag();
        
        // Get installed stones
        Set<StoneType> installedStones = getInstalledStones(stack);
        
        if (installedStones.isEmpty()) {
            if (!world.isRemote) {
                player.sendStatusMessage(new StringTextComponent("No Infected Infinity Stones installed")
                        .mergeStyle(TextFormatting.RED), true);
            }
            return ActionResult.resultFail(stack);
        }
        
        if (!world.isRemote) {
            // First apply infection effect
            applyInfectionEffect(world, player);
            
            // Then trigger chaotic destruction
            triggerChaoticBlast(world, player, installedStones);
            
            // Set a long cooldown due to power
            player.getCooldownTracker().setCooldown(this, 20 * 45); // 45 second cooldown
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    /**
     * Gets the set of stone types currently installed in the gauntlet
     */
    public Set<StoneType> getInstalledStones(ItemStack stack) {
        Set<StoneType> stones = EnumSet.noneOf(StoneType.class);
        
        IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (handler == null) return stones;
        
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stoneStack = handler.getStackInSlot(i);
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfectedStoneItem) {
                InfectedStoneItem stoneItem = (InfectedStoneItem) stoneStack.getItem();
                stones.add(stoneItem.getStoneType());
            }
        }
        
        return stones;
    }
    
    /**
     * Apply infection effects from the gauntlet
     */
    private void applyInfectionEffect(World world, PlayerEntity player) {
        player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 0));
        
        // Small chance of damage
        if (random.nextFloat() < 0.2f) {
            player.attackEntityFrom(NanoTechItems.NanoTechDamageSource.INFECTION, 2.0f);
            player.sendStatusMessage(new StringTextComponent("The infected gauntlet corrupts your body!")
                    .mergeStyle(TextFormatting.DARK_RED), true);
        }
    }
    
    /**
     * Trigger a chaotic blast with effects based on the installed stones
     */
    private void triggerChaoticBlast(World world, PlayerEntity player, Set<StoneType> stones) {
        double radius = 12.0 + (stones.size() * 3);
        float damage = 4.0f + (stones.size() * 2);
        
        player.sendStatusMessage(new StringTextComponent("The infected gauntlet unleashes chaotic energy!")
                .mergeStyle(TextFormatting.DARK_PURPLE, TextFormatting.BOLD), true);
                
        // Visual effects
        if (!world.isRemote) {
            ((ServerWorld) world).spawnParticle(
                net.minecraft.particles.ParticleTypes.DRAGON_BREATH, 
                player.getPosX(), player.getPosY() + 1, player.getPosZ(), 
                30, // count
                radius / 3, 2, radius / 3, // spread
                0.1D // speed
            );
        }
        
        // Sound effect
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), 
                SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.PLAYERS, 1.0F, 0.5F);
        
        // Get entities in range
        List<Entity> nearbyEntities = world.getEntitiesWithinAABBExcludingEntity(player, 
                new AxisAlignedBB(
                    player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        // Apply effects to nearby entities
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                
                // Apply damage
                livingEntity.attackEntityFrom(DamageSource.MAGIC, damage);
                
                // Apply stone-specific effects
                applyStoneEffects(livingEntity, stones, player);
                
                // Calculate direction vector away from player
                Vector3d pushVector = entity.getPositionVec().subtract(player.getPositionVec()).normalize();
                
                // Apply knockback
                double knockbackFactor = 1.5 + (stones.size() * 0.5);
                entity.setMotion(entity.getMotion().add(
                    pushVector.x * knockbackFactor,
                    0.8,
                    pushVector.z * knockbackFactor
                ));
                entity.velocityChanged = true;
                
                // Visual indicators
                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).spawnParticle(
                        net.minecraft.particles.ParticleTypes.WITCH,
                        entity.getPosX(),
                        entity.getPosY() + entity.getHeight() / 2,
                        entity.getPosZ(),
                        10, 0.3, 0.3, 0.3, 0.1
                    );
                }
            }
        }
        
        // Apply random block effects if Reality Stone is present
        if (stones.contains(StoneType.REALITY)) {
            applyRealityDistortion(world, player, radius);
        }
        
        // Apply time effects if Time Stone is present
        if (stones.contains(StoneType.TIME)) {
            player.addPotionEffect(new EffectInstance(Effects.SPEED, 300, 2));
            
            // Slow nearby entities
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
                    ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 2));
                }
            }
        }
        
        // Apply cost to the player - more stones means more cost
        int exhaustionLevel = stones.size();
        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 300 + (exhaustionLevel * 100), exhaustionLevel - 1));
        player.addPotionEffect(new EffectInstance(Effects.HUNGER, 300 + (exhaustionLevel * 100), exhaustionLevel - 1));
    }
    
    /**
     * Apply stone-specific effects to targets
     */
    private void applyStoneEffects(LivingEntity target, Set<StoneType> stones, PlayerEntity player) {
        // Different effects based on which stones are installed
        if (stones.contains(StoneType.MIND)) {
            target.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 200, 0));
            target.addPotionEffect(new EffectInstance(Effects.CONFUSION, 200, 0));
        }
        
        if (stones.contains(StoneType.POWER)) {
            target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 300, 1));
            
            // Chance to ignite target
            if (random.nextFloat() < 0.3f) {
                target.setFire(5);
            }
        }
        
        if (stones.contains(StoneType.SOUL)) {
            // Soul Stone allows the wielder to gain health from targets
            if (target.getHealth() > 2) {
                float healthStolen = target.getHealth() * 0.25f;
                target.setHealth(target.getHealth() - healthStolen);
                player.heal(healthStolen);
            }
        }
        
        if (stones.contains(StoneType.SPACE)) {
            // Space Stone has a chance to randomly teleport the target
            if (random.nextFloat() < 0.3f && !target.isInvulnerable()) {
                double range = 20;
                double x = target.getPosX() + (random.nextDouble() * 2 - 1) * range;
                double z = target.getPosZ() + (random.nextDouble() * 2 - 1) * range;
                double y = target.world.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE, (int) x, (int) z);
                target.teleportKeepLoaded(x, y, z);
            }
        }
    }
    
    /**
     * Apply reality distortion effects to the world
     */
    private void applyRealityDistortion(World world, PlayerEntity player, double radius) {
        // Change some blocks temporarily in the area
        int blocksAffected = 0;
        int maxBlocks = 20 + random.nextInt(10);
        
        for (int x = (int) -radius; x <= radius && blocksAffected < maxBlocks; x += 2) {
            for (int y = -3; y <= 3 && blocksAffected < maxBlocks; y += 2) {
                for (int z = (int) -radius; z <= radius && blocksAffected < maxBlocks; z += 2) {
                    BlockPos pos = new BlockPos(player.getPosX() + x, player.getPosY() + y, player.getPosZ() + z);
                    
                    // Check if the block is something we can/should change
                    if (!world.isAirBlock(pos) && world.getBlockState(pos).getBlockHardness(world, pos) >= 0
                            && random.nextFloat() < 0.2f) {
                        // Spawn particles instead of actual block changes for simplicity
                        if (world instanceof ServerWorld) {
                            ((ServerWorld) world).spawnParticle(
                                net.minecraft.particles.ParticleTypes.PORTAL,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                10, 0.4, 0.4, 0.4, 0
                            );
                        }
                        blocksAffected++;
                    }
                }
            }
        }
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        
        if (!(entityIn instanceof PlayerEntity)) return;
        
        // Check if being held or worn in offhand
        boolean isEquipped = isSelected || 
                ((PlayerEntity)entityIn).getHeldItemOffhand() == stack;
        
        if (isEquipped) {
            // Passive effects from infection
            PlayerEntity player = (PlayerEntity) entityIn;
            
            // Apply minor infection effects occasionally
            if (!worldIn.isRemote && worldIn.getGameTime() % 200 == 0) {
                // Get number of stones to determine infection severity
                Set<StoneType> stones = getInstalledStones(stack);
                if (!stones.isEmpty()) {
                    // Minor effects just from holding the gauntlet
                    if (random.nextFloat() < 0.3f) {
                        player.addPotionEffect(new EffectInstance(Effects.HUNGER, 60, 0));
                    }
                    
                    // More stones cause more instability
                    if (stones.size() >= 3 && random.nextFloat() < 0.2f) {
                        player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 100, 0));
                        player.sendStatusMessage(new StringTextComponent("The infected gauntlet pulses with chaotic energy...")
                                .mergeStyle(TextFormatting.DARK_PURPLE), true);
                    }
                    
                    // Apply minor infection damage with many stones
                    if (stones.size() >= 5 && random.nextFloat() < 0.1f) {
                        player.attackEntityFrom(NanoTechItems.NanoTechDamageSource.INFECTION, 1.0f);
                    }
                }
            }
        }
    }
    
    /**
     * Capability provider for the gauntlet's stone slots
     */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new InfectedGauntletCapabilityProvider(stack);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        tooltip.add(new StringTextComponent("Infected Infinity Gauntlet").mergeStyle(TextFormatting.DARK_PURPLE, TextFormatting.BOLD));
        tooltip.add(new StringTextComponent("Corrupted by Nano Tech").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent(""));
        
        // Display installed stones
        Set<StoneType> stones = getInstalledStones(stack);
        
        if (stones.isEmpty()) {
            tooltip.add(new StringTextComponent("No Infected Infinity Stones installed").mergeStyle(TextFormatting.GRAY));
        } else {
            tooltip.add(new StringTextComponent("Installed Infected Stones:").mergeStyle(TextFormatting.DARK_PURPLE));
            for (StoneType stone : stones) {
                tooltip.add(new StringTextComponent(" - " + stone.getName()).mergeStyle(stone.getColor()));
            }
        }
        
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new StringTextComponent("Right-click: Unleash chaotic power").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent(""));
        tooltip.add(new StringTextComponent("WARNING: Unstable and dangerous").mergeStyle(TextFormatting.DARK_RED));
        tooltip.add(new StringTextComponent("May cause infection symptoms").mergeStyle(TextFormatting.RED));
    }
    
    /**
     * Infected Gauntlet Capability Provider
     */
    private static class InfectedGauntletCapabilityProvider extends InfinityGauntlet.GauntletCapabilityProvider {
        public InfectedGauntletCapabilityProvider(ItemStack stack) {
            super(stack);
        }
    }
}