package com.infinitystones.items.skiddzie;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MagicMirrorItem extends Item {
    
    private static final int COOLDOWN_TICKS = 60; // 3 seconds
    
    public MagicMirrorItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return ActionResult.resultSuccess(stack);
        }
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        
        // Check if sneak-clicking to set location
        if (player.isSneaking()) {
            // Save current location
            saveLocation(stack, player);
            player.sendMessage(new StringTextComponent("Location saved to mirror").mergeStyle(TextFormatting.AQUA), player.getUniqueID());
            
            // Play sound effect
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else {
            // Teleport to saved location
            if (hasSavedLocation(stack)) {
                // Get saved location
                BlockPos savedPos = getSavedLocation(stack);
                int dimension = getSavedDimension(stack);
                
                // Check if same dimension
                if (dimension == player.world.getDimensionKey().getLocation().hashCode()) {
                    // Teleport to saved location
                    teleportPlayer(serverPlayer, savedPos);
                } else {
                    player.sendMessage(new StringTextComponent("Mirror cannot teleport between dimensions").mergeStyle(TextFormatting.RED), player.getUniqueID());
                }
            } else {
                player.sendMessage(new StringTextComponent("No location has been saved to this mirror").mergeStyle(TextFormatting.RED), player.getUniqueID());
            }
        }
        
        // Set cooldown
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        
        return ActionResult.resultSuccess(stack);
    }
    
    private void saveLocation(ItemStack stack, PlayerEntity player) {
        CompoundNBT tag = stack.getOrCreateTag();
        
        tag.putInt("PosX", player.getPosition().getX());
        tag.putInt("PosY", player.getPosition().getY());
        tag.putInt("PosZ", player.getPosition().getZ());
        tag.putInt("Dimension", player.world.getDimensionKey().getLocation().hashCode());
    }
    
    private boolean hasSavedLocation(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains("PosX") && tag.contains("PosY") && tag.contains("PosZ");
    }
    
    private BlockPos getSavedLocation(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        int x = tag.getInt("PosX");
        int y = tag.getInt("PosY");
        int z = tag.getInt("PosZ");
        return new BlockPos(x, y, z);
    }
    
    private int getSavedDimension(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag.getInt("Dimension");
    }
    
    private void teleportPlayer(ServerPlayerEntity player, BlockPos pos) {
        ServerWorld world = player.getServerWorld();
        
        // Store original position for particles
        BlockPos originalPos = player.getPosition();
        
        // Teleport player
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        
        // Play teleport sound at both positions
        world.playSound(null, originalPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        
        // Spawn particles at both locations
        spawnTeleportParticles(world, originalPos);
        spawnTeleportParticles(world, pos);
        
        // Send success message
        player.sendMessage(new StringTextComponent("Teleported to saved location").mergeStyle(TextFormatting.GREEN), player.getUniqueID());
    }
    
    private void spawnTeleportParticles(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 30; i++) {
            double xOffset = world.rand.nextGaussian() * 0.5;
            double yOffset = world.rand.nextGaussian() * 0.5;
            double zOffset = world.rand.nextGaussian() * 0.5;
            
            world.spawnParticle(
                    ParticleTypes.PORTAL,
                    pos.getX() + 0.5 + xOffset,
                    pos.getY() + 1.0 + yOffset,
                    pos.getZ() + 0.5 + zOffset,
                    1, 0, 0, 0, 0);
        }
    }
}