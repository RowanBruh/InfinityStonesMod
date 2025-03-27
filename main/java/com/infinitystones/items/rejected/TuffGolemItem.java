package com.infinitystones.items.rejected;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class TuffGolemItem extends Item {

    private boolean isActive = false;
    private ItemStack heldItem = ItemStack.EMPTY;
    private int activationTimer = 0;

    public TuffGolemItem() {
        super(new Item.Properties()
                .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)
                .maxStackSize(1)
                .maxDamage(200)); // 200 uses before it breaks
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        
        if (player == null) return ActionResultType.PASS;
        
        // Only works when used on a tuff block
        Block targetBlock = world.getBlockState(pos).getBlock();
        if (targetBlock == Blocks.TUFF || targetBlock == Blocks.STONE) {
            // Animation effect
            for (int i = 0; i < 15; i++) {
                world.addParticle(
                    ParticleTypes.CLOUD,
                    pos.getX() + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.8D,
                    pos.getY() + 1.0D + (world.rand.nextDouble() - 0.5D) * 0.8D,
                    pos.getZ() + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.8D,
                    0, 0.1D, 0
                );
            }
            
            // Play sound
            world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_STEP, SoundCategory.BLOCKS, 0.8F, 0.5F);
            
            // Toggle active state
            isActive = !isActive;
            activationTimer = isActive ? 600 : 0; // 30 seconds if activated
            
            // Damage the item on activation
            if (isActive && !player.abilities.isCreativeMode) {
                ItemStack stack = context.getItem();
                stack.damageItem(1, player, (entity) -> {
                    entity.sendBreakAnimation(context.getHand());
                });
            }
            
            return ActionResultType.SUCCESS;
        }
        
        return ActionResultType.PASS;
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!isActive) return;
        
        // Check if the timer has expired
        if (activationTimer > 0) {
            activationTimer--;
        } else {
            // Deactivate when timer expires
            isActive = false;
            if (!heldItem.isEmpty() && entity instanceof PlayerEntity) {
                // Drop held item when deactivated
                dropHeldItem(world, (PlayerEntity) entity);
            }
            return;
        }
        
        // Only perform actions when selected and active
        if (selected && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            
            // Item collection behavior
            if (heldItem.isEmpty() && world.getGameTime() % 20 == 0) {
                // Look for nearby items
                BlockPos playerPos = player.getPosition();
                List<ItemEntity> nearbyItems = world.getEntitiesWithinAABB(
                    ItemEntity.class, 
                    new AxisAlignedBB(
                        playerPos.getX() - 3, playerPos.getY() - 1, playerPos.getZ() - 3,
                        playerPos.getX() + 3, playerPos.getY() + 3, playerPos.getZ() + 3
                    )
                );
                
                if (!nearbyItems.isEmpty()) {
                    // Pick up the closest item
                    ItemEntity itemEntity = nearbyItems.get(0);
                    heldItem = itemEntity.getItem().copy();
                    heldItem.setCount(1);
                    
                    // Reduce the stack count of the original item
                    ItemStack originalStack = itemEntity.getItem();
                    originalStack.shrink(1);
                    if (originalStack.isEmpty()) {
                        itemEntity.remove();
                    } else {
                        itemEntity.setItem(originalStack);
                    }
                    
                    // Play pickup sound
                    world.playSound(null, player.getPosition(), 
                            SoundEvents.ENTITY_ITEM_PICKUP, 
                            SoundCategory.PLAYERS, 0.5F, 1.0F);
                }
            }
            
            // Display effects when holding an item
            if (!heldItem.isEmpty() && world.getGameTime() % 40 == 0) {
                world.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    player.getPosX(), 
                    player.getPosY() + 1.0D, 
                    player.getPosZ(), 
                    0, 0.1D, 0
                );
            }
        }
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        // Glowing effect when active
        return isActive;
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Special attack: slowness effect
        if (attacker instanceof PlayerEntity) {
            target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 1)); // Slowness II for 5 seconds
            
            // Play stone sound
            attacker.world.playSound(null, target.getPosition(), 
                    SoundEvents.BLOCK_STONE_HIT, 
                    SoundCategory.PLAYERS, 1.0F, 0.8F);
            
            // Damage the item
            stack.damageItem(1, attacker, (entity) -> {
                entity.sendBreakAnimation(attacker.getActiveHand());
            });
        }
        
        return true;
    }
    
    // Helper method for dropping held item
    private void dropHeldItem(World world, PlayerEntity player) {
        if (!heldItem.isEmpty()) {
            boolean addedToInventory = player.inventory.addItemStackToInventory(heldItem);
            
            if (!addedToInventory) {
                // If inventory is full, drop it on the ground
                ItemEntity droppedItem = new ItemEntity(
                    world, 
                    player.getPosX(), 
                    player.getPosY(), 
                    player.getPosZ(), 
                    heldItem
                );
                world.addEntity(droppedItem);
            }
            
            // Play sound
            world.playSound(null, player.getPosition(), 
                    SoundEvents.ENTITY_ITEM_PICKUP, 
                    SoundCategory.PLAYERS, 0.5F, 0.8F);
            
            // Clear the held item
            heldItem = ItemStack.EMPTY;
        }
    }
    
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        // Drop held item when swing
        if (isActive && !heldItem.isEmpty() && entity instanceof PlayerEntity) {
            dropHeldItem(entity.world, (PlayerEntity) entity);
            return true;
        }
        return false;
    }
}