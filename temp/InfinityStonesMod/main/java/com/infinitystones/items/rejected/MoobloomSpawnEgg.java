package com.infinitystones.items.rejected;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MoobloomSpawnEgg extends Item {

    public MoobloomSpawnEgg() {
        super(new Item.Properties()
                .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)
                .maxStackSize(16));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        
        if (player == null) return ActionResultType.PASS;
        
        // Check if used on grass block
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() == Blocks.GRASS_BLOCK) {
            // Create special cow entity if in server world
            if (!world.isRemote) {
                ServerWorld serverWorld = (ServerWorld) world;
                
                // Spawn a cow entity
                CowEntity cow = EntityType.COW.create(serverWorld);
                if (cow != null) {
                    // Position the cow on top of the grass block
                    cow.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    
                    // Add custom NBT data to mark it as a Moobloom
                    CompoundNBT nbt = cow.getPersistentData();
                    nbt.putBoolean("IsMoobloom", true);
                    
                    // Set custom name
                    cow.setCustomName(net.minecraft.util.text.StringTextComponent.EMPTY
                            .appendSibling(new net.minecraft.util.text.StringTextComponent("Moobloom"))
                            .mergeStyle(net.minecraft.util.text.TextFormatting.YELLOW));
                    cow.setCustomNameVisible(true);
                    
                    // Add the entity to the world
                    serverWorld.addEntity(cow);
                }
            }
            
            // Visual and sound effects
            for (int i = 0; i < 20; i++) {
                world.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5 + (world.rand.nextDouble() - 0.5) * 2.0,
                    pos.getY() + 1.2 + (world.rand.nextDouble() - 0.5) * 0.5,
                    pos.getZ() + 0.5 + (world.rand.nextDouble() - 0.5) * 2.0,
                    0, 0.1, 0
                );
            }
            
            // Play spawn sound
            world.playSound(null, pos, SoundEvents.ENTITY_COW_AMBIENT, SoundCategory.NEUTRAL, 1.0F, 1.2F);
            
            // Plant flowers around the spawn location
            if (!world.isRemote) {
                plantFlowersAround(world, pos);
            }
            
            // Consume the egg item if not in creative mode
            if (!player.abilities.isCreativeMode) {
                context.getItem().shrink(1);
            }
            
            return ActionResultType.SUCCESS;
        }
        
        return ActionResultType.PASS;
    }
    
    private void plantFlowersAround(World world, BlockPos center) {
        // Plant random flowers in a small radius
        for (int i = 0; i < 5; i++) {
            int offsetX = world.rand.nextInt(5) - 2;
            int offsetZ = world.rand.nextInt(5) - 2;
            
            BlockPos flowerPos = center.add(offsetX, 1, offsetZ);
            BlockPos soilPos = flowerPos.down();
            
            // Only place flowers on grass blocks
            if (world.getBlockState(soilPos).getBlock() == Blocks.GRASS_BLOCK && 
                world.getBlockState(flowerPos).isAir()) {
                
                // Random flower selection
                BlockState flowerState;
                float randValue = world.rand.nextFloat();
                
                if (randValue < 0.3) {
                    flowerState = Blocks.DANDELION.getDefaultState();
                } else if (randValue < 0.6) {
                    flowerState = Blocks.POPPY.getDefaultState();
                } else if (randValue < 0.8) {
                    flowerState = Blocks.BLUE_ORCHID.getDefaultState();
                } else {
                    flowerState = Blocks.OXEYE_DAISY.getDefaultState();
                }
                
                world.setBlockState(flowerPos, flowerState);
            }
        }
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        // Check if used on a cow
        if (target instanceof CowEntity && !(target.getPersistentData().contains("IsMoobloom"))) {
            World world = target.world;
            
            // Convert cow to Moobloom
            if (!world.isRemote) {
                // Mark cow as Moobloom
                CompoundNBT nbt = target.getPersistentData();
                nbt.putBoolean("IsMoobloom", true);
                
                // Give it a special name
                target.setCustomName(net.minecraft.util.text.StringTextComponent.EMPTY
                        .appendSibling(new net.minecraft.util.text.StringTextComponent("Moobloom"))
                        .mergeStyle(net.minecraft.util.text.TextFormatting.YELLOW));
                target.setCustomNameVisible(true);
                
                // Make cow drop flowers occasionally (via persistent NBT)
                target.getPersistentData().putBoolean("DropsFlowers", true);
            }
            
            // Visual effects
            for (int i = 0; i < 20; i++) {
                world.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    target.getPosX() + (world.rand.nextDouble() - 0.5) * 1.0,
                    target.getPosY() + 0.5 + (world.rand.nextDouble() - 0.5) * 0.5,
                    target.getPosZ() + (world.rand.nextDouble() - 0.5) * 1.0,
                    0, 0.1, 0
                );
            }
            
            // Play transformation sound
            world.playSound(null, target.getPosition(), 
                    SoundEvents.ENTITY_COW_AMBIENT, 
                    SoundCategory.NEUTRAL, 1.0F, 1.5F);
            
            // Consume the egg if not in creative mode
            if (!player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
            
            return true;
        }
        
        return false;
    }
}