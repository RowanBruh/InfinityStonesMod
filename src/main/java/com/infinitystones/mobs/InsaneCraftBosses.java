package com.infinitystones.mobs;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import com.infinitystones.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class InsaneCraftBosses {
    
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, InfinityStonesMod.MOD_ID);
    
    // Define entity types for our bosses
    public static final EntityType<ChaosGuardianEntity> CHAOS_GUARDIAN = EntityType.Builder
            .create(ChaosGuardianEntity::new, EntityClassification.MONSTER)
            .size(1.5f, 3.0f)
            .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "chaos_guardian").toString());
            
    public static final EntityType<CosmicTitanEntity> COSMIC_TITAN = EntityType.Builder
            .create(CosmicTitanEntity::new, EntityClassification.MONSTER)
            .size(2.0f, 4.0f)
            .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "cosmic_titan").toString());
            
    // Method to register all entity attributes
    public static void registerEntityAttributes() {
        // Register attributes for our boss entities
    }
    
    // The Chaos Guardian Boss - The Ultimate Chaos Entity
    public static class ChaosGuardianEntity extends MonsterEntity {
        private static final DataParameter<Integer> ATTACK_PHASE = EntityDataManager.createKey(ChaosGuardianEntity.class, DataSerializers.VARINT);
        private final ServerBossInfo bossInfo = new ServerBossInfo(
                new StringTextComponent("Chaos Guardian").mergeStyle(TextFormatting.DARK_PURPLE),
                BossInfo.Color.PURPLE, 
                BossInfo.Overlay.PROGRESS);
        private int attackCooldown = 0;
        private final Random random = new Random();
        
        public ChaosGuardianEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
            super(type, worldIn);
            this.experienceValue = 500;
        }
        
        public static AttributeModifierMap.MutableAttribute getAttributes() {
            double healthMultiplier = ModConfig.COMMON_CONFIG.insaneCraftBossHealth.get();
            double damageMultiplier = ModConfig.COMMON_CONFIG.insaneCraftBossDamage.get();
            
            return MonsterEntity.func_234295_eP_()
                    .createMutableAttribute(Attributes.MAX_HEALTH, 300.0D * healthMultiplier)
                    .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                    .createMutableAttribute(Attributes.ATTACK_DAMAGE, 20.0D * damageMultiplier)
                    .createMutableAttribute(Attributes.FOLLOW_RANGE, 40.0D)
                    .createMutableAttribute(Attributes.ARMOR, 15.0D)
                    .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
        }
        
        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new SwimGoal(this));
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
            this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 8.0F));
            this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
            
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        }
        
        @Override
        protected void registerData() {
            super.registerData();
            this.dataManager.register(ATTACK_PHASE, 0);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            // Spawn particles around the boss for a cool effect
            if (this.world.isRemote) {
                for (int i = 0; i < 2; i++) {
                    double x = this.getPosX() + (random.nextDouble() - 0.5) * 2;
                    double y = this.getPosY() + 1 + (random.nextDouble() - 0.5) * 2;
                    double z = this.getPosZ() + (random.nextDouble() - 0.5) * 2;
                    
                    this.world.addParticle(ParticleTypes.DRAGON_BREATH, 
                            x, y, z, 0, 0, 0);
                }
            }
            
            // Handle attack phases and special abilities
            if (!this.world.isRemote) {
                if (attackCooldown > 0) {
                    attackCooldown--;
                } else {
                    // Execute special attacks based on health percentage
                    float healthPercentage = this.getHealth() / this.getMaxHealth();
                    
                    if (healthPercentage < 0.3f) {
                        // Desperate phase - use powerful attacks more frequently
                        this.dataManager.set(ATTACK_PHASE, 3);
                        if (random.nextFloat() < 0.1f) {
                            performSpecialAttack();
                            attackCooldown = 30;
                        }
                    } else if (healthPercentage < 0.6f) {
                        // Medium phase - use medium strength attacks
                        this.dataManager.set(ATTACK_PHASE, 2);
                        if (random.nextFloat() < 0.05f) {
                            performSpecialAttack();
                            attackCooldown = 60;
                        }
                    } else {
                        // Initial phase - use basic attacks
                        this.dataManager.set(ATTACK_PHASE, 1);
                        if (random.nextFloat() < 0.03f) {
                            performSpecialAttack();
                            attackCooldown = 100;
                        }
                    }
                }
                
                // Update boss bar
                this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
            }
        }
        
        private void performSpecialAttack() {
            int phase = this.dataManager.get(ATTACK_PHASE);
            switch (phase) {
                case 1:
                    // Basic lightning attack
                    performLightningAttack(10.0f, 5);
                    break;
                case 2:
                    // Medium teleport and explosion attack
                    performTeleportAndExplode(15.0f);
                    break;
                case 3:
                    // Desperate soul drain attack
                    performSoulDrainAttack(10.0f, 20.0f);
                    break;
            }
        }
        
        private void performLightningAttack(float damage, int count) {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 25.0D) {
                    player.attackEntityFrom(DamageSource.LIGHTNING_BOLT, damage);
                    player.setFire(3);
                }
            }
        }
        
        private void performTeleportAndExplode(float explosionPower) {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            // Store original position
            BlockPos originalPos = this.getPosition();
            
            // Find and teleport to a random nearby player
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 50.0D) {
                    double x = player.getPosX() + (random.nextDouble() - 0.5) * 5;
                    double y = player.getPosY();
                    double z = player.getPosZ() + (random.nextDouble() - 0.5) * 5;
                    
                    this.attemptTeleport(x, y, z, true);
                    break;
                }
            }
            
            // Create explosion at both the original position and new position
            world.createExplosion(this, originalPos.getX(), originalPos.getY(), originalPos.getZ(), 
                    explosionPower, false, net.minecraft.world.Explosion.Mode.NONE);
                    
            world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 
                    explosionPower, false, net.minecraft.world.Explosion.Mode.NONE);
        }
        
        private void performSoulDrainAttack(float drainRange, float damage) {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            // Drain life from nearby entities to heal self
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= drainRange * drainRange) {
                    player.attackEntityFrom(DamageSource.MAGIC, damage);
                    this.heal(damage / 2);
                    
                    // Visual effect for soul drain
                    ((net.minecraft.world.server.ServerWorld)world).spawnParticle(
                            ParticleTypes.SOUL, 
                            player.getPosX(), player.getPosY() + 1.0, player.getPosZ(), 
                            20, 0.5, 0.5, 0.5, 0.05);
                }
            }
        }
        
        @Override
        public void addTrackingPlayer(ServerPlayerEntity player) {
            super.addTrackingPlayer(player);
            this.bossInfo.addPlayer(player);
        }
        
        @Override
        public void removeTrackingPlayer(ServerPlayerEntity player) {
            super.removeTrackingPlayer(player);
            this.bossInfo.removePlayer(player);
        }
        
        @Override
        protected SoundEvent getAmbientSound() {
            return SoundEvents.ENTITY_ENDER_DRAGON_GROWL;
        }
        
        @Override
        protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
            return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
        }
        
        @Override
        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_ENDER_DRAGON_DEATH;
        }
        
        @Override
        public boolean isNonBoss() {
            return false;
        }
        
        @Override
        protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
            super.dropSpecialItems(source, looting, recentlyHitIn);
            
            // Drop special loot
            this.entityDropItem(new ItemStack(ModItems.CHAOS_SHARD.get(), 1 + random.nextInt(2 + looting)));
            this.entityDropItem(new ItemStack(ModItems.WITHERED_BONE.get(), 1 + random.nextInt(3)));
            
            // Chance to drop an Infinity Stone
            if (random.nextFloat() < 0.2f + (looting * 0.05f)) {
                switch (random.nextInt(6)) {
                    case 0:
                        this.entityDropItem(new ItemStack(ModItems.SPACE_STONE.get()));
                        break;
                    case 1:
                        this.entityDropItem(new ItemStack(ModItems.MIND_STONE.get()));
                        break;
                    case 2:
                        this.entityDropItem(new ItemStack(ModItems.REALITY_STONE.get()));
                        break;
                    case 3:
                        this.entityDropItem(new ItemStack(ModItems.POWER_STONE.get()));
                        break;
                    case 4:
                        this.entityDropItem(new ItemStack(ModItems.TIME_STONE.get()));
                        break;
                    case 5:
                        this.entityDropItem(new ItemStack(ModItems.SOUL_STONE.get()));
                        break;
                }
            }
        }
        
        @Override
        public void writeAdditional(CompoundNBT compound) {
            super.writeAdditional(compound);
            compound.putInt("AttackPhase", this.dataManager.get(ATTACK_PHASE));
        }
        
        @Override
        public void readAdditional(CompoundNBT compound) {
            super.readAdditional(compound);
            if (compound.contains("AttackPhase")) {
                this.dataManager.set(ATTACK_PHASE, compound.getInt("AttackPhase"));
            }
        }
    }
    
    // The Cosmic Titan Boss - The Ultimate Cosmic Entity
    public static class CosmicTitanEntity extends MonsterEntity {
        private static final DataParameter<Boolean> CHARGING = EntityDataManager.createKey(CosmicTitanEntity.class, DataSerializers.BOOLEAN);
        private final ServerBossInfo bossInfo = new ServerBossInfo(
                new StringTextComponent("Cosmic Titan").mergeStyle(TextFormatting.BLUE),
                BossInfo.Color.BLUE, 
                BossInfo.Overlay.PROGRESS);
        private int attackCooldown = 0;
        private final Random random = new Random();
        
        public CosmicTitanEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
            super(type, worldIn);
            this.experienceValue = 750;
        }
        
        public static AttributeModifierMap.MutableAttribute getAttributes() {
            double healthMultiplier = ModConfig.COMMON_CONFIG.insaneCraftBossHealth.get();
            double damageMultiplier = ModConfig.COMMON_CONFIG.insaneCraftBossDamage.get();
            
            return MonsterEntity.func_234295_eP_()
                    .createMutableAttribute(Attributes.MAX_HEALTH, 400.0D * healthMultiplier)
                    .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                    .createMutableAttribute(Attributes.ATTACK_DAMAGE, 25.0D * damageMultiplier)
                    .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                    .createMutableAttribute(Attributes.ARMOR, 20.0D)
                    .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
        }
        
        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new SwimGoal(this));
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
            this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 8.0F));
            this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
            
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        }
        
        @Override
        protected void registerData() {
            super.registerData();
            this.dataManager.register(CHARGING, false);
        }
        
        @Override
        public void tick() {
            super.tick();
            
            // Spawn particles around the boss for a cosmic effect
            if (this.world.isRemote) {
                for (int i = 0; i < 3; i++) {
                    double x = this.getPosX() + (random.nextDouble() - 0.5) * 2;
                    double y = this.getPosY() + 2 + (random.nextDouble() - 0.5) * 2;
                    double z = this.getPosZ() + (random.nextDouble() - 0.5) * 2;
                    
                    this.world.addParticle(ParticleTypes.PORTAL, 
                            x, y, z, 0, 0, 0);
                }
            }
            
            // Handle attack phases and special abilities
            if (!this.world.isRemote) {
                if (attackCooldown > 0) {
                    attackCooldown--;
                } else {
                    // Execute special attacks based on health percentage
                    float healthPercentage = this.getHealth() / this.getMaxHealth();
                    
                    if (healthPercentage < 0.3f) {
                        // Desperate phase - use powerful attacks more frequently
                        if (random.nextFloat() < 0.15f) {
                            performCosmicNova();
                            attackCooldown = 60;
                        }
                    } else if (healthPercentage < 0.6f) {
                        // Medium phase - use medium strength attacks
                        if (random.nextFloat() < 0.1f) {
                            boolean shouldCharge = random.nextBoolean();
                            if (shouldCharge) {
                                startCosmicCharge();
                            } else {
                                performGravityWell();
                            }
                            attackCooldown = 80;
                        }
                    } else {
                        // Initial phase - use basic attacks
                        if (random.nextFloat() < 0.05f) {
                            performCosmicBlast();
                            attackCooldown = 100;
                        }
                    }
                }
                
                // Check if boss is charging a big attack
                if (this.dataManager.get(CHARGING)) {
                    // Continue charging animation/effect
                    if (this.world instanceof net.minecraft.world.server.ServerWorld) {
                        ((net.minecraft.world.server.ServerWorld)this.world).spawnParticle(
                                ParticleTypes.END_ROD, 
                                this.getPosX(), this.getPosY() + 2.0, this.getPosZ(), 
                                20, 1.5, 1.5, 1.5, 0.1);
                    }
                    
                    // Slow movement during charge
                    this.setMotion(this.getMotion().mul(0.7D, 1.0D, 0.7D));
                    
                    // After 40 ticks (2 seconds), release the charge attack
                    if (attackCooldown <= 40) {
                        releaseCosmicCharge();
                        this.dataManager.set(CHARGING, false);
                    }
                }
                
                // Update boss bar
                this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
            }
        }
        
        private void performCosmicBlast() {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            // Target and damage nearby players with cosmic energy
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 36.0D) { // 6 block radius
                    player.attackEntityFrom(DamageSource.MAGIC, 10.0f);
                    
                    // Knockback effect
                    double knockbackStrength = 2.0;
                    double xRatio = player.getPosX() - this.getPosX();
                    double zRatio = player.getPosZ() - this.getPosZ();
                    double distanceSq = xRatio * xRatio + zRatio * zRatio;
                    
                    if (distanceSq > 0.001) {
                        player.addVelocity(
                                knockbackStrength * xRatio / distanceSq,
                                0.4,
                                knockbackStrength * zRatio / distanceSq);
                        player.velocityChanged = true;
                    }
                    
                    // Visual effect
                    if (world instanceof net.minecraft.world.server.ServerWorld) {
                        ((net.minecraft.world.server.ServerWorld)world).spawnParticle(
                                ParticleTypes.CRIT, 
                                player.getPosX(), player.getPosY() + 1.0, player.getPosZ(), 
                                20, 0.5, 0.5, 0.5, 0.1);
                    }
                }
            }
            
            // Play sound effect
            world.playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, 
                    this.getSoundCategory(), 1.0F, 1.0F);
        }
        
        private void startCosmicCharge() {
            this.dataManager.set(CHARGING, true);
            
            // Visual and sound effect to indicate charging
            if (this.world instanceof net.minecraft.world.server.ServerWorld) {
                ((net.minecraft.world.server.ServerWorld)this.world).spawnParticle(
                        ParticleTypes.FLASH, 
                        this.getPosX(), this.getPosY() + 2.0, this.getPosZ(), 
                        5, 0.1, 0.1, 0.1, 0);
            }
            
            this.world.playSound(null, this.getPosition(), SoundEvents.BLOCK_BEACON_POWER_SELECT, 
                    this.getSoundCategory(), 1.0F, 0.5F);
            
            // Send message to nearby players
            for (PlayerEntity player : this.world.getPlayers()) {
                if (player.getDistanceSq(this) <= 100.0D) {
                    player.sendMessage(
                            new StringTextComponent("Cosmic Titan is charging a powerful attack!").mergeStyle(TextFormatting.RED),
                            player.getUniqueID());
                }
            }
        }
        
        private void releaseCosmicCharge() {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            // Create a massive explosion effect
            float explosionPower = 3.0f;
            world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 
                    explosionPower, false, net.minecraft.world.Explosion.Mode.NONE);
            
            // Damage all players in a wide radius
            float damage = 20.0f;
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 100.0D) { // 10 block radius
                    player.attackEntityFrom(DamageSource.MAGIC, damage);
                    
                    // Apply slowness effect
                    player.addPotionEffect(new net.minecraft.potion.EffectInstance(
                            net.minecraft.potion.Effects.SLOWNESS, 200, 1));
                }
            }
            
            // Visual and sound effects
            if (world instanceof net.minecraft.world.server.ServerWorld) {
                ((net.minecraft.world.server.ServerWorld)world).spawnParticle(
                        ParticleTypes.EXPLOSION, 
                        this.getPosX(), this.getPosY() + 1.0, this.getPosZ(), 
                        10, 3.0, 3.0, 3.0, 0.1);
            }
            
            world.playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, 
                    this.getSoundCategory(), 1.0F, 1.0F);
        }
        
        private void performGravityWell() {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            // Pull all players toward the boss
            double pullStrength = 1.5;
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 100.0D && player.getDistanceSq(this) > 4.0D) {
                    double xRatio = this.getPosX() - player.getPosX();
                    double yRatio = this.getPosY() - player.getPosY();
                    double zRatio = this.getPosZ() - player.getPosZ();
                    double distanceSq = xRatio * xRatio + yRatio * yRatio + zRatio * zRatio;
                    
                    if (distanceSq > 0.001) {
                        player.addVelocity(
                                pullStrength * xRatio / distanceSq,
                                pullStrength * yRatio / distanceSq,
                                pullStrength * zRatio / distanceSq);
                        player.velocityChanged = true;
                    }
                    
                    // Damage players being pulled
                    player.attackEntityFrom(DamageSource.MAGIC, 5.0f);
                }
            }
            
            // Visual and sound effects
            if (world instanceof net.minecraft.world.server.ServerWorld) {
                ((net.minecraft.world.server.ServerWorld)world).spawnParticle(
                        ParticleTypes.REVERSE_PORTAL, 
                        this.getPosX(), this.getPosY() + 1.5, this.getPosZ(), 
                        50, 5.0, 5.0, 5.0, 0.1);
            }
            
            world.playSound(null, this.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, 
                    this.getSoundCategory(), 1.0F, 0.5F);
            
            // Message to players
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 100.0D) {
                    player.sendMessage(
                            new StringTextComponent("You feel the pull of cosmic gravity!").mergeStyle(TextFormatting.DARK_PURPLE),
                            player.getUniqueID());
                }
            }
        }
        
        private void performCosmicNova() {
            World world = this.getEntityWorld();
            if (world.isRemote) return;
            
            // Clear all nearby entities with massive damage
            float damage = 30.0f;
            for (LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, 
                    this.getBoundingBox().grow(8.0D, 8.0D, 8.0D))) {
                if (entity != this) {
                    entity.attackEntityFrom(DamageSource.MAGIC, damage);
                    
                    // Apply blindness and weakness
                    if (entity instanceof PlayerEntity) {
                        entity.addPotionEffect(new net.minecraft.potion.EffectInstance(
                                net.minecraft.potion.Effects.BLINDNESS, 160, 0));
                        entity.addPotionEffect(new net.minecraft.potion.EffectInstance(
                                net.minecraft.potion.Effects.WEAKNESS, 200, 1));
                    }
                }
            }
            
            // Visual and sound effects
            if (world instanceof net.minecraft.world.server.ServerWorld) {
                for (int i = 0; i < 5; i++) {
                    ((net.minecraft.world.server.ServerWorld)world).spawnParticle(
                            ParticleTypes.EXPLOSION_EMITTER, 
                            this.getPosX() + (random.nextDouble() - 0.5) * 5,
                            this.getPosY() + 1.0 + (random.nextDouble() - 0.5) * 5,
                            this.getPosZ() + (random.nextDouble() - 0.5) * 5, 
                            1, 0, 0, 0, 0);
                }
            }
            
            world.playSound(null, this.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, 
                    this.getSoundCategory(), 2.0F, 0.5F);
            
            // Message to players
            for (PlayerEntity player : world.getPlayers()) {
                if (player.getDistanceSq(this) <= 100.0D) {
                    player.sendMessage(
                            new StringTextComponent("Cosmic Titan unleashes cosmic nova!").mergeStyle(TextFormatting.DARK_RED),
                            player.getUniqueID());
                }
            }
        }
        
        @Override
        public void addTrackingPlayer(ServerPlayerEntity player) {
            super.addTrackingPlayer(player);
            this.bossInfo.addPlayer(player);
        }
        
        @Override
        public void removeTrackingPlayer(ServerPlayerEntity player) {
            super.removeTrackingPlayer(player);
            this.bossInfo.removePlayer(player);
        }
        
        @Override
        protected SoundEvent getAmbientSound() {
            return SoundEvents.ENTITY_WITHER_AMBIENT;
        }
        
        @Override
        protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
            return SoundEvents.ENTITY_WITHER_HURT;
        }
        
        @Override
        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_WITHER_DEATH;
        }
        
        @Override
        public boolean isNonBoss() {
            return false;
        }
        
        @Override
        protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
            super.dropSpecialItems(source, looting, recentlyHitIn);
            
            // Drop special loot
            this.entityDropItem(new ItemStack(ModItems.COSMIC_DUST.get(), 2 + random.nextInt(3 + looting)));
            this.entityDropItem(new ItemStack(ModItems.COSMIC_PEARL.get(), 1 + random.nextInt(2)));
            
            // Drop the Ultimate Ingot
            if (random.nextFloat() < 0.5f + (looting * 0.1f)) {
                this.entityDropItem(new ItemStack(ModItems.ULTIMATE_INGOT.get()));
            }
            
            // Chance to drop an Infinity Stone
            if (random.nextFloat() < 0.3f + (looting * 0.05f)) {
                switch (random.nextInt(6)) {
                    case 0:
                        this.entityDropItem(new ItemStack(ModItems.SPACE_STONE.get()));
                        break;
                    case 1:
                        this.entityDropItem(new ItemStack(ModItems.MIND_STONE.get()));
                        break;
                    case 2:
                        this.entityDropItem(new ItemStack(ModItems.REALITY_STONE.get()));
                        break;
                    case 3:
                        this.entityDropItem(new ItemStack(ModItems.POWER_STONE.get()));
                        break;
                    case 4:
                        this.entityDropItem(new ItemStack(ModItems.TIME_STONE.get()));
                        break;
                    case 5:
                        this.entityDropItem(new ItemStack(ModItems.SOUL_STONE.get()));
                        break;
                }
            }
            
            // Small chance to drop part of the Infinity Gauntlet
            if (random.nextFloat() < 0.1f + (looting * 0.05f)) {
                this.entityDropItem(new ItemStack(ModItems.INFINITY_GAUNTLET.get()));
            }
        }
        
        @Override
        public void writeAdditional(CompoundNBT compound) {
            super.writeAdditional(compound);
            compound.putBoolean("Charging", this.dataManager.get(CHARGING));
        }
        
        @Override
        public void readAdditional(CompoundNBT compound) {
            super.readAdditional(compound);
            if (compound.contains("Charging")) {
                this.dataManager.set(CHARGING, compound.getBoolean("Charging"));
            }
        }
    }
}