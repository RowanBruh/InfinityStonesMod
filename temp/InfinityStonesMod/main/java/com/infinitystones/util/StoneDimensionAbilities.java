package com.infinitystones.util;

import com.infinitystones.config.ModConfig;
import com.infinitystones.items.InfinityStones.StoneType;
import com.infinitystones.world.dimension.DimensionHelper;
import com.infinitystones.world.dimension.ModDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Extension of stone abilities to handle dimension travel
 */
public class StoneDimensionAbilities {
    
    /**
     * Checks if the player is in a stone dimension and returns them to the overworld
     * @param world The current world
     * @param player The player
     * @return True if player was in a stone dimension and teleported
     */
    public static boolean checkAndReturnFromDimension(World world, PlayerEntity player) {
        if (world.isRemote) return false;
        if (!(player instanceof ServerPlayerEntity)) return false;
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        RegistryKey<World> dimensionKey = world.getDimensionKey();
        
        // Check if player is in a stone dimension
        if (dimensionKey == ModDimensions.SPACE_DIMENSION ||
            dimensionKey == ModDimensions.MIND_DIMENSION ||
            dimensionKey == ModDimensions.REALITY_DIMENSION ||
            dimensionKey == ModDimensions.POWER_DIMENSION ||
            dimensionKey == ModDimensions.TIME_DIMENSION ||
            dimensionKey == ModDimensions.SOUL_DIMENSION) {
            
            // Teleport back to the overworld
            player.sendMessage(
                new StringTextComponent("Returning to overworld...").mergeStyle(TextFormatting.AQUA),
                player.getUniqueID());
                
            return DimensionHelper.returnToOverworld(player);
        }
        
        return false;
    }
    
    /**
     * Teleport a player to the appropriate infinity stone dimension
     * @param world The current world
     * @param player The player
     * @param stoneType The type of stone being used
     * @return True if teleportation was successful
     */
    public static boolean teleportToDimension(World world, PlayerEntity player, StoneType stoneType) {
        if (world.isRemote) return false;
        if (!(player instanceof ServerPlayerEntity)) return false;
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        RegistryKey<World> targetDimension = null;
        
        // Determine which dimension to teleport to
        switch (stoneType) {
            case SPACE:
                targetDimension = ModDimensions.SPACE_DIMENSION;
                break;
            case MIND:
                targetDimension = ModDimensions.MIND_DIMENSION;
                break;
            case REALITY:
                targetDimension = ModDimensions.REALITY_DIMENSION;
                break;
            case POWER:
                targetDimension = ModDimensions.POWER_DIMENSION;
                break;
            case TIME:
                targetDimension = ModDimensions.TIME_DIMENSION;
                break;
            case SOUL:
                targetDimension = ModDimensions.SOUL_DIMENSION;
                break;
            default:
                return false;
        }
        
        if (targetDimension != null) {
            // Play teleportation sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 0.5F);
            
            // Message the player
            player.sendMessage(
                new StringTextComponent("Entering " + stoneType.name() + " dimension...").mergeStyle(TextFormatting.GOLD),
                player.getUniqueID());
                
            // Set cooldown
            player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 200);
            
            // Teleport to dimension
            return DimensionHelper.teleportToDimension(player, targetDimension);
        }
        
        return false;
    }
}