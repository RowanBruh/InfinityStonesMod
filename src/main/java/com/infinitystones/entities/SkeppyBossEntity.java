package com.infinitystones.entities;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.bionic.BionicItems;
import com.infinitystones.items.skiddzie.SkiddzieItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class SkeppyBossEntity extends MonsterEntity {

    // Boss phases: Normal, Enraged, Final
    private static final DataParameter<Integer> BOSS_PHASE = 
            EntityDataManager.createKey(SkeppyBossEntity.class, DataSerializers.VARINT);

    // Cooldowns for special attacks
    private int specialAttackCooldown = 0;
    private int weaponSwitchCooldown = 0;
    private int teleportCooldown = 0;
    
    // Skeppy's custom skin location
    private static final ResourceLocation SKEPPY_TEXTURE = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "textures/entity/skeppy_boss.png");
    
    // Boss bar for server
    private final ServerBossInfo bossInfo = 
            new ServerBossInfo(new StringTextComponent("Skeppy").mergeStyle(TextFormatting.AQUA), 
                              BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS);

    public SkeppyBossEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 1000; // Lots of XP
        
        // Set equipment - will cycle through multiple weapons during battle
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(BionicItems.TNT_SWORD.get()));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BOSS_PHASE, 0); // Start in normal phase
    }

    public static AttributeModifierMap.MutableAttribute getAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 300.0D) // Boss has high health
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 15.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D) // Can see players from far away
                .createMutableAttribute(Attributes.ARMOR, 10.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 16.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void livingTick() {
        super.livingTick();
        
        // Handle cooldowns
        if (this.specialAttackCooldown > 0) this.specialAttackCooldown--;
        if (this.weaponSwitchCooldown > 0) this.weaponSwitchCooldown--;
        if (this.teleportCooldown > 0) this.teleportCooldown--;
        
        // Phase transitions based on health percentage
        float healthPercentage = this.getHealth() / this.getMaxHealth();
        
        // Transition to enraged phase at 60% health
        if (healthPercentage <= 0.6f && this.getBossPhase() == 0) {
            this.setBossPhase(1);
            // Give enraged effects
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20.0D);
            
            // Switch weapon to Chaos Stone
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(SkiddzieItems.CHAOS_STONE.get()));
            
            // Announce phase change
            if (!this.world.isRemote) {
                for (PlayerEntity player : this.world.getPlayers()) {
                    player.sendMessage(
                        new StringTextComponent("Skeppy becomes enraged!").mergeStyle(TextFormatting.RED),
                        player.getUniqueID());
                }
            }
        }
        
        // Transition to final phase at 30% health
        else if (healthPercentage <= 0.3f && this.getBossPhase() == 1) {
            this.setBossPhase(2);
            // Give final phase effects
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.55D);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(25.0D);
            
            // Switch weapon to Wormhole Generator
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(BionicItems.WORMHOLE_GENERATOR.get()));
            
            // Announce phase change
            if (!this.world.isRemote) {
                for (PlayerEntity player : this.world.getPlayers()) {
                    player.sendMessage(
                        new StringTextComponent("Skeppy enters his final form!").mergeStyle(TextFormatting.DARK_RED),
                        player.getUniqueID());
                }
            }
        }
        
        // Special attacks based on phases
        if (!this.world.isRemote && this.specialAttackCooldown <= 0 && this.isAlive()) {
            // Different attacks based on phase
            switch (this.getBossPhase()) {
                case 0: // Normal phase - basic attacks
                    this.specialAttackCooldown = 200; // 10 seconds
                    break;
                    
                case 1: // Enraged phase - more frequent attacks
                    this.specialAttackCooldown = 140; // 7 seconds
                    break;
                    
                case 2: // Final phase - very frequent attacks
                    this.specialAttackCooldown = 80; // 4 seconds
                    
                    // Teleport more frequently in final phase
                    if (this.teleportCooldown <= 0 && this.getAttackTarget() != null) {
                        this.teleportToEntity(this.getAttackTarget());
                        this.teleportCooldown = 60; // 3 seconds
                    }
                    break;
            }
        }
        
        // Update boss info
        if (!this.world.isRemote) {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }
    
    private void teleportToEntity(Entity entity) {
        // Teleport behind the player
        double x = entity.getPosX() - Math.sin(Math.toRadians(entity.rotationYaw)) * 2.0;
        double z = entity.getPosZ() + Math.cos(Math.toRadians(entity.rotationYaw)) * 2.0;
        
        this.setPositionAndUpdate(x, entity.getPosY(), z);
        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), 
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
    }
    
    // Boss phase getter and setter
    public int getBossPhase() {
        return this.dataManager.get(BOSS_PHASE);
    }
    
    public void setBossPhase(int phase) {
        this.dataManager.set(BOSS_PHASE, phase);
    }

    @Override
    public void setCustomName(ITextComponent name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        
        this.setBossPhase(compound.getInt("BossPhase"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("BossPhase", this.getBossPhase());
    }

    @Override
    public boolean isNonBoss() {
        return false; // This is a boss entity
    }

    @Override
    public void addTrackingPlayer(PlayerEntity player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(PlayerEntity player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.3F;
    }
}