package com.infinitystones.util;

import java.util.List;
import java.util.Random;

import com.infinitystones.config.ModConfig;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class StoneAbilities {
    private static final Random random = new Random();
    private static final UUID POWER_STONE_STRENGTH_ID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");
    private static final UUID SOUL_STONE_HEALTH_ID = UUID.fromString("3940233D-32A5-4DDA-BD03-4F3C3317A40F");
    
    /**
     * Space Stone ability: Teleport the player in the direction they are looking
     */
    public static void activateSpaceStone(PlayerEntity player, World world) {
        // Calculate teleport destination based on where player is looking
        double distance = ModConfig.COMMON_CONFIG.spaceStonePowerRadius.get();
        
        Vector3d lookVec = player.getLookVec().normalize();
        Vector3d startVec = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        Vector3d endVec = startVec.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
        
        BlockRayTraceResult rayTrace = world.rayTraceBlocks(new RayTraceContext(
                startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
        
        if (rayTrace.getType() != BlockRayTraceResult.Type.MISS) {
            // If hit something, teleport in front of it
            Vector3d hitPos = rayTrace.getHitVec();
            Vector3d teleportPos = hitPos.subtract(lookVec.x * 0.5, 0, lookVec.z * 0.5);
            
            // Check for a safe spot to stand
            BlockPos blockPos = new BlockPos(teleportPos);
            if (isSafePos(world, blockPos)) {
                player.setPositionAndUpdate(teleportPos.x, teleportPos.y, teleportPos.z);
                
                // Sound and particle effects
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                
                if (!world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Space Stone activated!").mergeStyle(TextFormatting.BLUE),
                            player.getUniqueID());
                }
            } else if (!world.isRemote) {
                player.sendMessage(
                        new StringTextComponent("No safe location to teleport to!").mergeStyle(TextFormatting.RED),
                        player.getUniqueID());
            }
        } else {
            // If didn't hit anything, teleport to maximum distance
            Vector3d teleportPos = startVec.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
            
            // Check for a safe spot to stand
            BlockPos blockPos = new BlockPos(teleportPos);
            if (isSafePos(world, blockPos)) {
                player.setPositionAndUpdate(teleportPos.x, teleportPos.y, teleportPos.z);
                
                // Sound and particle effects
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                
                if (!world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Space Stone activated!").mergeStyle(TextFormatting.BLUE),
                            player.getUniqueID());
                }
            } else if (!world.isRemote) {
                player.sendMessage(
                        new StringTextComponent("No safe location to teleport to!").mergeStyle(TextFormatting.RED),
                        player.getUniqueID());
            }
        }
    }
    
    /**
     * Mind Stone ability: Control nearby mobs
     */
    public static void activateMindStone(PlayerEntity player, World world) {
        if (!world.isRemote) {
            int radius = 20;
            int duration = ModConfig.COMMON_CONFIG.mindStoneControlDuration.get() * 20; // Convert to ticks
            
            // Get all living entities in radius
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, 
                    new AxisAlignedBB(player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            int controlledCount = 0;
            
            // Apply effects to each entity
            for (LivingEntity entity : entities) {
                if (entity != player && entity instanceof MonsterEntity) {
                    // Make monster stop attacking player
                    if (entity.getAttackTarget() == player) {
                        entity.setAttackTarget(null);
                    }
                    
                    // Apply glowing effect to show control
                    entity.addPotionEffect(new EffectInstance(Effects.GLOWING, duration, 0));
                    
                    // Apply slowness to make them easier to control
                    entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, 1));
                    
                    // Make monsters attack each other
                    if (random.nextBoolean() && controlledCount < 5) {
                        // Find another monster to attack
                        for (LivingEntity target : entities) {
                            if (target != entity && target instanceof MonsterEntity) {
                                ((MonsterEntity) entity).setAttackTarget(target);
                                controlledCount++;
                                break;
                            }
                        }
                    }
                }
            }
            
            // Notify player
            player.sendMessage(
                    new StringTextComponent("Mind Stone controls the weak minded!").mergeStyle(TextFormatting.YELLOW),
                    player.getUniqueID());
            
            // Sound effect
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
    
    /**
     * Reality Stone ability: Transform blocks in the world
     */
    public static void activateRealityStone(PlayerEntity player, World world) {
        if (!world.isRemote) {
            double power = ModConfig.COMMON_CONFIG.realityStonePowerMultiplier.get();
            int radius = (int) (5 * power);
            
            BlockPos playerPos = player.getPosition();
            
            // Notify player
            player.sendMessage(
                    new StringTextComponent("Reality Stone bends the environment!").mergeStyle(TextFormatting.RED),
                    player.getUniqueID());
            
            // Sound effect
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 1.0f, 0.8f);
            
            // Transform blocks in radius
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x*x + y*y + z*z <= radius*radius) { // Sphere check
                            BlockPos pos = playerPos.add(x, y, z);
                            
                            if (random.nextFloat() < 0.1f) { // Only transform some blocks
                                BlockState currentState = world.getBlockState(pos);
                                
                                if (!currentState.isAir(world, pos) && 
                                        currentState.getBlockHardness(world, pos) >= 0 && 
                                        !currentState.getBlock().equals(Blocks.BEDROCK)) {
                                    
                                    // Transform to random decorative block
                                    BlockState newState;
                                    float rand = random.nextFloat();
                                    
                                    if (rand < 0.2f) {
                                        newState = Blocks.DIAMOND_BLOCK.getDefaultState();
                                    } else if (rand < 0.4f) {
                                        newState = Blocks.EMERALD_BLOCK.getDefaultState();
                                    } else if (rand < 0.6f) {
                                        newState = Blocks.GOLD_BLOCK.getDefaultState();
                                    } else if (rand < 0.8f) {
                                        newState = Blocks.REDSTONE_BLOCK.getDefaultState();
                                    } else {
                                        newState = Blocks.LAPIS_BLOCK.getDefaultState();
                                    }
                                    
                                    world.setBlockState(pos, newState);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Power Stone ability: Create an explosion and damage nearby entities
     */
    public static void activatePowerStone(PlayerEntity player, World world) {
        if (!world.isRemote) {
            double power = ModConfig.COMMON_CONFIG.powerStoneDamageMultiplier.get();
            float explosionPower = (float) (3.0f * Math.min(power, 10.0) / 10.0); // Cap at reasonable size
            
            // Create explosion
            if (ModConfig.COMMON_CONFIG.enableInsaneCraftIntegration.get()) {
                // More powerful explosion with Insane Craft integration
                world.createExplosion(player, player.getPosX(), player.getPosY(), player.getPosZ(), 
                        explosionPower * 1.5f, Explosion.Mode.BREAK);
            } else {
                world.createExplosion(player, player.getPosX(), player.getPosY(), player.getPosZ(), 
                        explosionPower, Explosion.Mode.BREAK);
            }
            
            // Damage nearby entities
            int radius = (int) (8 * power);
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, 
                    new AxisAlignedBB(player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    // Calculate damage based on distance
                    double distance = entity.getDistanceSq(player);
                    float damage = (float) ((radius - Math.sqrt(distance)) * power / 2.0);
                    
                    if (damage > 0) {
                        entity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
                    }
                }
            }
            
            // Notify player
            player.sendMessage(
                    new StringTextComponent("Power Stone unleashes destructive force!").mergeStyle(TextFormatting.DARK_PURPLE),
                    player.getUniqueID());
        }
    }
    
    /**
     * Time Stone ability: Slow down time for all entities except the player
     */
    public static void activateTimeStone(PlayerEntity player, World world) {
        if (!world.isRemote) {
            int duration = ModConfig.COMMON_CONFIG.timeStoneEffectDuration.get() * 20; // Convert to ticks
            int radius = 30;
            
            // Affect all living entities in radius
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, 
                    new AxisAlignedBB(player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    // Apply extreme slowness
                    entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, 4));
                    entity.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, duration, 4));
                }
            }
            
            // Apply haste and speed to player
            player.addPotionEffect(new EffectInstance(Effects.HASTE, duration, 2));
            player.addPotionEffect(new EffectInstance(Effects.SPEED, duration, 2));
            
            // Notify player
            player.sendMessage(
                    new StringTextComponent("Time Stone slows the world around you!").mergeStyle(TextFormatting.GREEN),
                    player.getUniqueID());
            
            // Sound effect
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 1.0f, 0.5f);
        }
    }
    
    /**
     * Soul Stone ability: Steal life from nearby entities
     */
    public static void activateSoulStone(PlayerEntity player, World world) {
        if (!world.isRemote) {
            int radius = 10;
            float lifeStealAmount = (float) ModConfig.COMMON_CONFIG.soulStoneLifeStealAmount.get();
            
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, 
                    new AxisAlignedBB(player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius,
                                    player.getPosX() + radius, player.getPosY() + radius, player.getPosZ() + radius));
            
            boolean stolenLife = false;
            
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    // Damage entity and heal player
                    entity.attackEntityFrom(DamageSource.MAGIC, lifeStealAmount);
                    player.heal(lifeStealAmount / 2);  // Half of damage dealt
                    stolenLife = true;
                    
                    // Visual effect - give weakness
                    entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 0));
                }
            }
            
            if (stolenLife) {
                // Notify player
                player.sendMessage(
                        new StringTextComponent("Soul Stone absorbs life essence!").mergeStyle(TextFormatting.GOLD),
                        player.getUniqueID());
                
                // Sound effect
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.5f, 1.2f);
            } else {
                player.sendMessage(
                        new StringTextComponent("No life essence to absorb nearby!").mergeStyle(TextFormatting.GOLD),
                        player.getUniqueID());
            }
        }
    }
    
    /**
     * Infinity Gauntlet ability (with all stones): Snap half of all entities out of existence
     */
    public static void activateInfinityGauntlet(PlayerEntity player, World world) {
        if (!world.isRemote && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            List<LivingEntity> allEntities = serverWorld.getEntitiesWithinAABB(LivingEntity.class, 
                    player.getBoundingBox().grow(500)); // Very large radius
            
            int count = 0;
            
            // The Snap - eliminate half of all life
            for (LivingEntity entity : allEntities) {
                // Don't affect players
                if (!(entity instanceof PlayerEntity) && random.nextBoolean()) {
                    entity.attackEntityFrom(DamageSource.MAGIC, Float.MAX_VALUE);
                    count++;
                    
                    // Particle effects at entity location
                    serverWorld.spawnParticle(net.minecraft.particles.ParticleTypes.LARGE_SMOKE, 
                            entity.getPosX(), entity.getPosY() + 0.5, entity.getPosZ(), 
                            10, 0.3, 0.5, 0.3, 0.05);
                }
            }
            
            // Broadcast message to all players
            for (ServerPlayerEntity serverPlayer : serverWorld.getPlayers()) {
                serverPlayer.sendMessage(
                        new StringTextComponent("§l" + player.getName().getString() + " snapped " + count + 
                                " beings out of existence§r").mergeStyle(TextFormatting.DARK_PURPLE),
                        player.getUniqueID());
            }
            
            // Major sound effect
            serverWorld.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.PLAYERS, 2.0f, 0.5f);
            
            // Damage player slightly from the exertion
            player.attackEntityFrom(DamageSource.MAGIC, 4.0f);
        }
    }
    
    /**
     * Apply passive effects for Power Stone when held
     */
    public static void applyPowerStonePassive(PlayerEntity player) {
        // Give strength buff when holding the Power Stone
        ModifiableAttributeInstance attribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute != null) {
            // Remove existing modifier if present
            attribute.removeModifier(POWER_STONE_STRENGTH_ID);
            
            // Add strength modifier
            attribute.applyNonPersistentModifier(new AttributeModifier(
                    POWER_STONE_STRENGTH_ID,
                    "Power Stone strength bonus", 
                    4.0, // +4 attack damage
                    AttributeModifier.Operation.ADDITION));
        }
    }
    
    /**
     * Apply passive effects for Soul Stone when held
     */
    public static void applySoulStonePassive(PlayerEntity player) {
        // Check if regeneration should be applied (every 5 seconds)
        if (player.ticksExisted % 100 == 0) {
            player.heal(1.0f);
        }
    }
    
    /**
     * Check if position is safe for teleportation
     */
    private static boolean isSafePos(World world, BlockPos pos) {
        // Check if there is space for the player (2 blocks high)
        return world.isAirBlock(pos) && world.isAirBlock(pos.up()) && 
               !world.isAirBlock(pos.down()); // Must have something solid below
    }
}
