package com.infinitystones.world.dimension;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class for handling teleportation between dimensions
 */
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID)
public class DimensionHelper {
    
    // Store last position for each player in the overworld to return them
    private static final Map<UUID, BlockPos> LAST_OVERWORLD_POS = new HashMap<>();
    
    /**
     * Teleport a player to a specific dimension
     * @param player The player to teleport
     * @param dimensionKey The target dimension
     * @return True if teleportation was successful
     */
    public static boolean teleportToDimension(PlayerEntity player, RegistryKey<World> dimensionKey) {
        if (player.world.isRemote) {
            return false; // Client side - do nothing
        }
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        MinecraftServer server = player.getServer();
        
        if (server == null) {
            return false;
        }
        
        ServerWorld targetWorld = server.getWorld(dimensionKey);
        
        if (targetWorld == null) {
            InfinityStonesMod.LOGGER.error("Target dimension " + dimensionKey.getLocation() + " does not exist!");
            return false;
        }
        
        // If going from overworld to a custom dimension, save the position
        if (player.world.getDimensionKey() == World.OVERWORLD) {
            LAST_OVERWORLD_POS.put(player.getUniqueID(), player.getPosition());
        }
        
        // Custom teleporter that doesn't create nether portals
        ITeleporter teleporter = new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld,
                                     float yaw, Function<Boolean, Entity> repositionEntity) {
                return repositionEntity.apply(false);
            }
        };
        
        // Teleport to the target dimension
        serverPlayer.changeDimension(targetWorld, teleporter);
        
        // Ensure a safe platform exists at spawn
        if (dimensionKey != World.OVERWORLD) {
            createSafePlatform(targetWorld, serverPlayer.getPosition(), getDimensionFloorBlock(dimensionKey));
        }
        
        return true;
    }
    
    /**
     * Return a player to the overworld
     * @param player The player to return
     * @return True if teleportation was successful
     */
    public static boolean returnToOverworld(PlayerEntity player) {
        if (player.world.isRemote) {
            return false;
        }
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        MinecraftServer server = player.getServer();
        
        if (server == null) {
            return false;
        }
        
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        
        // Custom teleporter that doesn't create nether portals
        ITeleporter teleporter = new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld,
                                     float yaw, Function<Boolean, Entity> repositionEntity) {
                return repositionEntity.apply(false);
            }
        };
        
        // Get saved position or default to spawn
        BlockPos returnPos = LAST_OVERWORLD_POS.getOrDefault(player.getUniqueID(), 
                overworld.getSpawnPoint());
        
        // Teleport to the overworld
        serverPlayer.changeDimension(overworld, teleporter);
        
        // Set the player at their saved position
        serverPlayer.setPositionAndUpdate(returnPos.getX(), returnPos.getY(), returnPos.getZ());
        
        return true;
    }
    
    /**
     * Create a safe platform for the player to stand on
     */
    private static void createSafePlatform(ServerWorld world, BlockPos centerPos, BlockState blockState) {
        int radius = 3;
        int height = 3;  // Clear space above
        
        // Create a platform and clear space above it
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Set platform
                BlockPos floorPos = new BlockPos(centerPos.getX() + x, centerPos.getY() - 1, centerPos.getZ() + z);
                world.setBlockState(floorPos, blockState);
                
                // Clear space above
                for (int y = 0; y < height; y++) {
                    BlockPos clearPos = new BlockPos(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                    world.setBlockState(clearPos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
    
    /**
     * Get an appropriate floor block for each dimension
     */
    private static BlockState getDimensionFloorBlock(RegistryKey<World> dimensionKey) {
        if (dimensionKey == ModDimensions.SOUL_DIMENSION) {
            return Blocks.SOUL_SOIL.getDefaultState();
        } else if (dimensionKey == ModDimensions.TIME_DIMENSION) {
            return Blocks.OBSIDIAN.getDefaultState();
        } else if (dimensionKey == ModDimensions.SPACE_DIMENSION) {
            return Blocks.END_STONE.getDefaultState();
        } else if (dimensionKey == ModDimensions.REALITY_DIMENSION) {
            return Blocks.RED_TERRACOTTA.getDefaultState();
        } else if (dimensionKey == ModDimensions.POWER_DIMENSION) {
            return Blocks.MAGMA_BLOCK.getDefaultState();
        } else if (dimensionKey == ModDimensions.MIND_DIMENSION) {
            return Blocks.YELLOW_CONCRETE.getDefaultState();
        }
        
        // Default
        return Blocks.STONE.getDefaultState();
    }
}