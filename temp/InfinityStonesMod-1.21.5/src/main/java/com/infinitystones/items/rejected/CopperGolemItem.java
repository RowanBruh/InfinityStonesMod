package com.infinitystones.items.rejected;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperGolemItem extends Item {
    
    public CopperGolemItem() {
        super(new Item.Properties()
                .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)
                .maxStackSize(1)
                .maxDamage(100)); // 100 uses before it breaks
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        
        if (player == null) return ActionResultType.PASS;
        
        // Only works when used on a copper block
        if (world.getBlockState(pos).getBlock() == Blocks.COPPER_BLOCK) {
            // Animation effect
            for (int i = 0; i < 20; i++) {
                world.addParticle(
                    ParticleTypes.CRIT,
                    pos.getX() + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.8D,
                    pos.getY() + 1.0D + (world.rand.nextDouble() - 0.5D) * 0.8D,
                    pos.getZ() + 0.5D + (world.rand.nextDouble() - 0.5D) * 0.8D,
                    0, 0, 0
                );
            }
            
            // Play sound
            world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            
            // Replace the copper block with a button
            world.setBlockState(pos, Blocks.COPPER_BLOCK.getDefaultState());
            world.setBlockState(pos.up(), Blocks.STONE_BUTTON.getDefaultState());
            
            // Damage the item
            if (!player.abilities.isCreativeMode) {
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
        // Random button pressing behavior when held
        if (selected && entity instanceof PlayerEntity && world.getGameTime() % 80 == 0 && world.rand.nextFloat() < 0.2F) {
            PlayerEntity player = (PlayerEntity) entity;
            BlockPos playerPos = player.getPosition();
            
            // Look for nearby buttons within 5 blocks
            for (int x = -5; x <= 5; x++) {
                for (int y = -5; y <= 5; y++) {
                    for (int z = -5; z <= 5; z++) {
                        BlockPos checkPos = playerPos.add(x, y, z);
                        if (world.getBlockState(checkPos).getBlock() == Blocks.STONE_BUTTON) {
                            // Play button click sound
                            world.playSound(null, checkPos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 
                                    SoundCategory.BLOCKS, 0.3F, 0.6F);
                            
                            // Add particles
                            world.addParticle(ParticleTypes.CRIT, 
                                    checkPos.getX() + 0.5D, 
                                    checkPos.getY() + 0.5D, 
                                    checkPos.getZ() + 0.5D, 
                                    0, 0, 0);
                            
                            // Only activate one button
                            return;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Special attack: lightning effect
        World world = attacker.world;
        if (!world.isRemote) {
            // Create lightning effect
            world.addParticle(ParticleTypes.FLASH, 
                    target.getPosX(), 
                    target.getPosY() + 1.0D, 
                    target.getPosZ(), 
                    0, 0, 0);
            
            // Play electric sound
            world.playSound(null, target.getPosition(), 
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 
                    SoundCategory.WEATHER, 0.5F, 1.0F);
            
            // Damage target
            target.attackEntityFrom(InfinityStonesMod.INFINITY_DAMAGE, 5.0F);
        }
        
        // Damage the item
        stack.damageItem(2, attacker, (entity) -> {
            entity.sendBreakAnimation(attacker.getActiveHand());
        });
        
        return true;
    }
}