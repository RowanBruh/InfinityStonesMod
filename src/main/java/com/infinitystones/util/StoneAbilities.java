package com.infinitystones.util;

import com.infinitystones.config.ModConfig;
import com.infinitystones.items.InfinityStones.StoneType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

/**
 * Handles the abilities for each Infinity Stone
 */
public class StoneAbilities {
    private static final Random random = new Random();
    
    /**
     * Activates the ability of the specified Stone Type
     * 
     * @param world The world instance
     * @param player The player using the stone
     * @param stoneType The type of stone being used
     * @return Whether the ability was successfully activated
     */
    public static boolean activateStoneAbility(World world, PlayerEntity player, StoneType stoneType) {
        if (world.isRemote) return false;
        
        switch (stoneType) {
            case SPACE:
                return activateSpaceStone(world, player);
            case MIND:
                return activateMindStone(world, player);
            case REALITY:
                return activateRealityStone(world, player);
            case POWER:
                return activatePowerStone(world, player);
            case TIME:
                return activateTimeStone(world, player);
            case SOUL:
                return activateSoulStone(world, player);
            default:
                return false;
        }
    }
    
    /**
     * Space Stone - Teleportation and Spatial Manipulation
     */
    private static boolean activateSpaceStone(World world, PlayerEntity player) {
        // Teleport player in the direction they're looking
        double distance = 32.0; // Max teleport distance
        double x = player.getPosX() + player.getLookVec().x * distance;
        double y = player.getPosY() + player.getLookVec().y * distance;
        double z = player.getPosZ() + player.getLookVec().z * distance;
        
        // Find a safe location to teleport to
        BlockPos targetPos = new BlockPos(x, y, z);
        boolean foundSafeSpot = false;
        
        // Check if target position is safe (not solid and has air above)
        for (int i = 0; i < 10; i++) {
            if (world.getBlockState(targetPos).getMaterial().blocksMovement()) {
                targetPos = targetPos.up();
            } else if (!world.getBlockState(targetPos.up()).getMaterial().blocksMovement()) {
                foundSafeSpot = true;
                break;
            }
        }
        
        if (foundSafeSpot) {
            // Teleport the player
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                serverPlayer.connection.setPlayerLocation(
                        targetPos.getX() + 0.5, 
                        targetPos.getY(), 
                        targetPos.getZ() + 0.5, 
                        player.rotationYaw, 
                        player.rotationPitch);
                
                // Play teleport sound
                world.playSound(null, player.getPrevPosX(), player.getPrevPosY(), player.getPrevPosZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                
                // Add particle effects
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                            ParticleTypes.PORTAL,
                            player.getPrevPosX(), player.getPrevPosY() + 1, player.getPrevPosZ(),
                            50, 0.5, 1.0, 0.5, 0.1);
                }
                
                player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 
                        ModConfig.COMMON_CONFIG.spaceStoneCooldown.get());
                
                return true;
            }
        }
        
        // If we couldn't find a safe spot or player isn't a server player
        player.sendMessage(
                new StringTextComponent("No safe location found to teleport to!").mergeStyle(TextFormatting.RED),
                player.getUniqueID());
        
        return false;
    }
    
    /**
     * Mind Stone - Mental Powers and Control
     */
    private static boolean activateMindStone(World world, PlayerEntity player) {
        // Mind control nearby entities
        int radius = 16; // Radius of effect
        int duration = 200; // Duration of effect in ticks (10 seconds)
        
        // Find all living entities within radius
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                new AxisAlignedBB(
                        player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                        player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        boolean affectedAny = false;
        
        for (LivingEntity entity : entities) {
            // Don't affect the player
            if (entity != player) {
                // Make hostile mobs attack each other
                if (entity instanceof net.minecraft.entity.MobEntity) {
                    net.minecraft.entity.MobEntity mob = (net.minecraft.entity.MobEntity) entity;
                    
                    // Find another random entity to attack
                    if (!entities.isEmpty() && entities.size() > 1) {
                        int randomIndex;
                        LivingEntity targetEntity;
                        
                        do {
                            randomIndex = random.nextInt(entities.size());
                            targetEntity = entities.get(randomIndex);
                        } while (targetEntity == player || targetEntity == entity);
                        
                        // Set the target
                        mob.setAttackTarget(targetEntity);
                        
                        // Add confusion effect
                        entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, 1));
                        
                        // Visual effect
                        if (world instanceof ServerWorld) {
                            ServerWorld serverWorld = (ServerWorld) world;
                            serverWorld.spawnParticle(
                                    ParticleTypes.ENCHANT,
                                    entity.getPosX(), entity.getPosY() + entity.getHeight() + 0.5, entity.getPosZ(),
                                    20, 0.5, 0.5, 0.5, 0.1);
                        }
                        
                        affectedAny = true;
                    }
                }
            }
        }
        
        if (affectedAny) {
            // Play mind control sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1.0F, 1.5F);
            
            // Add particle effect around player
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                serverWorld.spawnParticle(
                        ParticleTypes.END_ROD,
                        player.getPosX(), player.getPosY() + 1, player.getPosZ(),
                        100, 3.0, 2.0, 3.0, 0.1);
            }
            
            player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 
                    ModConfig.COMMON_CONFIG.mindStoneCooldown.get());
            
            return true;
        } else {
            player.sendMessage(
                    new StringTextComponent("No entities found to control!").mergeStyle(TextFormatting.YELLOW),
                    player.getUniqueID());
            
            return false;
        }
    }
    
    /**
     * Reality Stone - Alter Reality and Environment
     */
    private static boolean activateRealityStone(World world, PlayerEntity player) {
        // Transform the environment around the player
        int radius = 10; // Radius of effect
        BlockPos playerPos = player.getPosition();
        
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Record how many blocks we changed
            int changedBlocks = 0;
            
            // Transform blocks in the area
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        // Calculate distance from center
                        double distance = Math.sqrt(x*x + y*y + z*z);
                        
                        // Only affect blocks within the sphere
                        if (distance <= radius) {
                            BlockPos pos = playerPos.add(x, y, z);
                            
                            // Random chance to transform based on distance (more likely closer to player)
                            if (random.nextFloat() < (1.0 - (distance / radius)) * 0.2) {
                                // Don't transform air or bedrock
                                if (!world.isAirBlock(pos) && 
                                    world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                                    
                                    // Choose a transformation based on the existing block
                                    if (world.getBlockState(pos).getMaterial().isSolid()) {
                                        // Transform solid blocks to something interesting
                                        switch (random.nextInt(5)) {
                                            case 0:
                                                world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState());
                                                break;
                                            case 1:
                                                world.setBlockState(pos, Blocks.EMERALD_ORE.getDefaultState());
                                                break;
                                            case 2:
                                                world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
                                                break;
                                            case 3:
                                                world.setBlockState(pos, Blocks.LAPIS_BLOCK.getDefaultState());
                                                break;
                                            case 4:
                                                world.setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
                                                break;
                                        }
                                        changedBlocks++;
                                        
                                        // Add particle effect
                                        serverWorld.spawnParticle(
                                                ParticleTypes.CRIMSON_SPORE,
                                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                                5, 0.3, 0.3, 0.3, 0.0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (changedBlocks > 0) {
                // Play reality warping sound
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 1.0F, 1.0F);
                
                // Create a wave of particles
                for (int i = 0; i < 360; i += 10) {
                    double angle = Math.toRadians(i);
                    double x = player.getPosX() + Math.cos(angle) * radius;
                    double z = player.getPosZ() + Math.sin(angle) * radius;
                    
                    serverWorld.spawnParticle(
                            ParticleTypes.FLAME,
                            x, player.getPosY() + 1, z,
                            5, 0.1, 0.1, 0.1, 0.01);
                }
                
                player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 
                        ModConfig.COMMON_CONFIG.realityStoneCooldown.get());
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Power Stone - Destructive Energy and Force
     */
    private static boolean activatePowerStone(World world, PlayerEntity player) {
        // Create a powerful explosion and energy wave
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Create explosion (non-destructive)
            world.createExplosion(player, player.getPosX(), player.getPosY(), player.getPosZ(), 
                    2.0F, false, net.minecraft.world.Explosion.Mode.NONE);
            
            // Damage entities in a wide radius
            int radius = 15;
            float baseDamage = 15.0F;
            
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                    new AxisAlignedBB(
                            player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                            player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            boolean hitAny = false;
            
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    // Calculate damage based on distance (more damage closer to the player)
                    double distance = entity.getDistanceSq(player);
                    float damage = (float) (baseDamage * (1.0 - (Math.sqrt(distance) / radius)));
                    
                    // Apply damage
                    entity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
                    
                    // Apply knockback
                    double knockbackStrength = 3.0;
                    double xRatio = entity.getPosX() - player.getPosX();
                    double zRatio = entity.getPosZ() - player.getPosZ();
                    double distanceSq = xRatio * xRatio + zRatio * zRatio;
                    
                    if (distanceSq > 0.001) {
                        entity.addVelocity(
                                knockbackStrength * xRatio / distanceSq,
                                0.5,
                                knockbackStrength * zRatio / distanceSq);
                        entity.velocityChanged = true;
                    }
                    
                    hitAny = true;
                }
            }
            
            // Create energy wave particles
            for (int i = 0; i < 360; i += 5) {
                double angle = Math.toRadians(i);
                
                for (int r = 1; r <= radius; r += 2) {
                    double x = player.getPosX() + Math.cos(angle) * r;
                    double z = player.getPosZ() + Math.sin(angle) * r;
                    
                    serverWorld.spawnParticle(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            x, player.getPosY() + 0.5, z,
                            1, 0, 0, 0, 0);
                }
            }
            
            // Add lightning for dramatic effect
            for (int i = 0; i < 5; i++) {
                double x = player.getPosX() + (random.nextDouble() * radius * 2) - radius;
                double z = player.getPosZ() + (random.nextDouble() * radius * 2) - radius;
                
                LightningBoltEntity lightning = new LightningBoltEntity(
                        EntityType.LIGHTNING_BOLT, world);
                lightning.setPosition(x, player.getPosY(), z);
                world.addEntity(lightning);
            }
            
            player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 
                    ModConfig.COMMON_CONFIG.powerStoneCooldown.get());
            
            return hitAny || true; // Always return true even if we didn't hit anything
        }
        
        return false;
    }
    
    /**
     * Time Stone - Manipulate Time
     */
    private static boolean activateTimeStone(World world, PlayerEntity player) {
        // Slow down time for all entities except the player
        int radius = 20; // Radius of effect
        int duration = 200; // Duration of slowness in ticks (10 seconds)
        
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                new AxisAlignedBB(
                        player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                        player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        boolean affectedAny = false;
        
        for (LivingEntity entity : entities) {
            if (entity != player) {
                // Apply extreme slowness to simulate time stopping
                entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, 5));
                entity.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, duration, 5));
                entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, duration, 2));
                
                // Add particle effect
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    serverWorld.spawnParticle(
                            ParticleTypes.REVERSE_PORTAL,
                            entity.getPosX(), entity.getPosY() + entity.getHeight() / 2, entity.getPosZ(),
                            20, entity.getWidth() / 2, entity.getHeight() / 2, entity.getWidth() / 2, 0.02);
                }
                
                affectedAny = true;
            }
        }
        
        if (affectedAny) {
            // Give player beneficial effects
            player.addPotionEffect(new EffectInstance(Effects.SPEED, duration, 3));
            player.addPotionEffect(new EffectInstance(Effects.HASTE, duration, 2));
            
            // Play time stone sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0F, 0.5F);
            
            // Create time effect particles around player
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                
                // Create a swirling time vortex effect
                for (int i = 0; i < 10; i++) {
                    double angle = i * Math.PI * 2 / 10;
                    double radius2 = 3.0;
                    
                    for (int j = 0; j < 20; j++) {
                        double x = player.getPosX() + Math.cos(angle + j * 0.1) * radius2;
                        double y = player.getPosY() + j * 0.1;
                        double z = player.getPosZ() + Math.sin(angle + j * 0.1) * radius2;
                        
                        serverWorld.spawnParticle(
                                ParticleTypes.PORTAL,
                                x, y, z,
                                1, 0, 0, 0, 0);
                    }
                }
            }
            
            player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 
                    ModConfig.COMMON_CONFIG.timeStoneCooldown.get());
            
            return true;
        } else {
            player.sendMessage(
                    new StringTextComponent("No entities found to affect time for!").mergeStyle(TextFormatting.GREEN),
                    player.getUniqueID());
            
            return false;
        }
    }
    
    /**
     * Soul Stone - Control Over Life and Death
     */
    private static boolean activateSoulStone(World world, PlayerEntity player) {
        // Drain life from nearby entities and heal the player
        int radius = 12; // Radius of effect
        float drainAmount = 4.0f; // Amount of health to drain per entity
        
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                new AxisAlignedBB(
                        player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                        player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
        
        boolean drainedAny = false;
        float totalHealing = 0;
        
        for (LivingEntity entity : entities) {
            if (entity != player && !(entity instanceof PlayerEntity)) { // Don't affect other players
                // Drain health from the entity
                entity.attackEntityFrom(DamageSource.causePlayerDamage(player), drainAmount);
                
                // Add soul particle effect from entity to player
                if (world instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    
                    // Soul stream effect from entity to player
                    double startX = entity.getPosX();
                    double startY = entity.getPosY() + entity.getHeight() / 2;
                    double startZ = entity.getPosZ();
                    
                    double endX = player.getPosX();
                    double endY = player.getPosY() + 1.0;
                    double endZ = player.getPosZ();
                    
                    // Create stream of particles from entity to player
                    int particleCount = 15;
                    for (int i = 0; i < particleCount; i++) {
                        double progress = i / (double) particleCount;
                        double x = startX + (endX - startX) * progress;
                        double y = startY + (endY - startY) * progress;
                        double z = startZ + (endZ - startZ) * progress;
                        
                        serverWorld.spawnParticle(
                                ParticleTypes.SOUL,
                                x, y, z,
                                1, 0.1, 0.1, 0.1, 0.02);
                    }
                }
                
                totalHealing += drainAmount;
                drainedAny = true;
            }
        }
        
        if (drainedAny) {
            // Heal the player (cap at their max health)
            player.heal(Math.min(totalHealing, player.getMaxHealth() - player.getHealth()));
            
            // Apply additional beneficial effects
            player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 100, 1));
            player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 200, 1));
            
            // Play soul stone sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.5F, 1.2F);
            
            // Add particle effect around player
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                serverWorld.spawnParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        player.getPosX(), player.getPosY() + 1, player.getPosZ(),
                        50, 1.0, 1.0, 1.0, 0.05);
            }
            
            player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 
                    ModConfig.COMMON_CONFIG.soulStoneCooldown.get());
            
            return true;
        } else {
            player.sendMessage(
                    new StringTextComponent("No souls found to drain!").mergeStyle(TextFormatting.DARK_RED),
                    player.getUniqueID());
            
            return false;
        }
    }
    
    /**
     * When all stones are combined in the Infinity Gauntlet
     */
    public static boolean activateInfinityGauntlet(World world, PlayerEntity player) {
        if (world.isRemote) return false;
        
        // Create a massive power surge
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            
            // Send message to all players in the world
            for (PlayerEntity worldPlayer : world.getPlayers()) {
                worldPlayer.sendMessage(
                        new StringTextComponent("The power of the Infinity Gauntlet has been unleashed!").mergeStyle(TextFormatting.GOLD),
                        worldPlayer.getUniqueID());
            }
            
            // Create multiple explosions
            for (int i = 0; i < 10; i++) {
                double x = player.getPosX() + (random.nextDouble() * 30) - 15;
                double y = player.getPosY() + (random.nextDouble() * 10);
                double z = player.getPosZ() + (random.nextDouble() * 30) - 15;
                
                world.createExplosion(player, x, y, z, 3.0F, false, net.minecraft.world.Explosion.Mode.NONE);
            }
            
            // Add lightning strikes
            for (int i = 0; i < 15; i++) {
                double x = player.getPosX() + (random.nextDouble() * 50) - 25;
                double z = player.getPosZ() + (random.nextDouble() * 50) - 25;
                
                LightningBoltEntity lightning = new LightningBoltEntity(
                        EntityType.LIGHTNING_BOLT, world);
                lightning.setPosition(x, world.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE, (int)x, (int)z), z);
                world.addEntity(lightning);
            }
            
            // Affect all entities in a massive radius
            int radius = 50;
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class,
                    new AxisAlignedBB(
                            player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                            player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            // Randomly decide to "snap" or "empower"
            boolean snapMode = random.nextBoolean();
            
            if (snapMode) {
                // "Snap" mode - erase half of all entities
                int count = 0;
                for (LivingEntity entity : entities) {
                    if (entity != player && random.nextBoolean()) {
                        // Create dust-disintegration effect
                        serverWorld.spawnParticle(
                                ParticleTypes.ASH,
                                entity.getPosX(), entity.getPosY() + entity.getHeight() / 2, entity.getPosZ(),
                                100, entity.getWidth(), entity.getHeight() / 2, entity.getWidth(), 0.1);
                        
                        // Kill the entity
                        entity.attackEntityFrom(DamageSource.MAGIC, Float.MAX_VALUE);
                        count++;
                    }
                }
                
                if (count > 0) {
                    player.sendMessage(
                            new StringTextComponent("You snapped " + count + " entities out of existence!").mergeStyle(TextFormatting.YELLOW),
                            player.getUniqueID());
                }
            } else {
                // "Empower" mode - transform the world and creatures
                // Enhance player with god-like powers
                player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 6000, 4)); // 5 minutes
                player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 6000, 4));
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 6000, 2));
                player.addPotionEffect(new EffectInstance(Effects.SPEED, 6000, 2));
                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 6000, 2));
                player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 6000, 0));
                player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0));
                player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 6000, 0));
                
                // Transform the environment
                int transformRadius = 30;
                BlockPos playerPos = player.getPosition();
                
                for (int x = -transformRadius; x <= transformRadius; x++) {
                    for (int y = -transformRadius; y <= transformRadius; y++) {
                        for (int z = -transformRadius; z <= transformRadius; z++) {
                            double distanceSq = x*x + y*y + z*z;
                            
                            if (distanceSq <= transformRadius * transformRadius && random.nextFloat() < 0.1) {
                                BlockPos pos = playerPos.add(x, y, z);
                                
                                if (!world.isAirBlock(pos) && 
                                    world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                                    
                                    // Upgrade blocks to their valuable versions
                                    if (world.getBlockState(pos).getBlock() == Blocks.STONE) {
                                        world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState());
                                    } 
                                    else if (world.getBlockState(pos).getBlock() == Blocks.DIRT || 
                                             world.getBlockState(pos).getBlock() == Blocks.GRASS_BLOCK) {
                                        world.setBlockState(pos, Blocks.EMERALD_BLOCK.getDefaultState());
                                    }
                                    else if (world.getBlockState(pos).getBlock() == Blocks.SAND) {
                                        world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
                                    }
                                    
                                    // Add particle effect
                                    serverWorld.spawnParticle(
                                            ParticleTypes.END_ROD,
                                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                            3, 0.3, 0.3, 0.3, 0.0);
                                }
                            }
                        }
                    }
                }
                
                player.sendMessage(
                        new StringTextComponent("The Infinity Gauntlet grants you godlike power!").mergeStyle(TextFormatting.GOLD),
                        player.getUniqueID());
            }
            
            // Create massive particle effect
            for (int i = 0; i < 10; i++) {
                double angle = i * Math.PI * 2 / 10;
                
                for (double r = 1; r < 30; r += 0.5) {
                    double x = player.getPosX() + Math.cos(angle) * r;
                    double z = player.getPosZ() + Math.sin(angle) * r;
                    
                    serverWorld.spawnParticle(
                            ParticleTypes.END_ROD,
                            x, player.getPosY() + 0.5, z,
                            1, 0, 0, 0, 0);
                }
            }
            
            // Play massive sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 5.0F, 0.8F);
            
            // Set a long cooldown (5 minutes)
            player.getCooldownTracker().setCooldown(player.getHeldItemMainhand().getItem(), 6000);
            
            return true;
        }
        
        return false;
    }
}