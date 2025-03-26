package com.infinitystones.mobs;

import com.infinitystones.InfinityStonesMod;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InsaneCraftBosses {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITIES, InfinityStonesMod.MOD_ID);
    
    // Insane Craft Bosses (inspired by Insane Craft series)
    public static final RegistryObject<EntityType<ChaosGuardianEntity>> CHAOS_GUARDIAN = 
            ENTITY_TYPES.register("chaos_guardian", 
                    () -> EntityType.Builder.<ChaosGuardianEntity>create(
                            ChaosGuardianEntity::new, EntityClassification.MONSTER)
                            .size(2.0f, 3.0f)
                            .trackingRange(128)
                            .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "chaos_guardian").toString()));
    
    public static final RegistryObject<EntityType<UltimateBossEntity>> ULTIMATE_BOSS = 
            ENTITY_TYPES.register("ultimate_boss", 
                    () -> EntityType.Builder.<UltimateBossEntity>create(
                            UltimateBossEntity::new, EntityClassification.MONSTER)
                            .size(3.0f, 5.0f)
                            .trackingRange(128)
                            .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "ultimate_boss").toString()));
    
    public static final RegistryObject<EntityType<CrazyWitherEntity>> CRAZY_WITHER = 
            ENTITY_TYPES.register("crazy_wither", 
                    () -> EntityType.Builder.<CrazyWitherEntity>create(
                            CrazyWitherEntity::new, EntityClassification.MONSTER)
                            .size(2.0f, 3.0f)
                            .trackingRange(128)
                            .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "crazy_wither").toString()));
    
    public static final RegistryObject<EntityType<MegaDragonEntity>> MEGA_DRAGON = 
            ENTITY_TYPES.register("mega_dragon", 
                    () -> EntityType.Builder.<MegaDragonEntity>create(
                            MegaDragonEntity::new, EntityClassification.MONSTER)
                            .size(8.0f, 4.0f)
                            .trackingRange(128)
                            .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "mega_dragon").toString()));
    
    // Custom boss entities based on Insane Craft
    public static class ChaosGuardianEntity extends MonsterEntity {
        public ChaosGuardianEntity(EntityType<? extends MonsterEntity> type, net.minecraft.world.World worldIn) {
            super(type, worldIn);
        }
        
        @Override
        protected void registerGoals() {
            super.registerGoals();
            // Custom AI goals would be added here
        }
    }
    
    public static class UltimateBossEntity extends MonsterEntity {
        public UltimateBossEntity(EntityType<? extends MonsterEntity> type, net.minecraft.world.World worldIn) {
            super(type, worldIn);
        }
        
        @Override
        protected void registerGoals() {
            super.registerGoals();
            // Custom AI goals would be added here
        }
    }
    
    public static class CrazyWitherEntity extends MonsterEntity {
        public CrazyWitherEntity(EntityType<? extends MonsterEntity> type, net.minecraft.world.World worldIn) {
            super(type, worldIn);
        }
        
        @Override
        protected void registerGoals() {
            super.registerGoals();
            // Custom AI goals would be added here
        }
    }
    
    public static class MegaDragonEntity extends MonsterEntity {
        public MegaDragonEntity(EntityType<? extends MonsterEntity> type, net.minecraft.world.World worldIn) {
            super(type, worldIn);
        }
        
        @Override
        protected void registerGoals() {
            super.registerGoals();
            // Custom AI goals would be added here
        }
    }
    
    // Register entity attributes
    @Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class AttributeRegistry {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(CHAOS_GUARDIAN.get(), MonsterEntity.func_234295_eP_()
                    .createMutableAttribute(Attributes.MAX_HEALTH, 300.0D)
                    .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                    .createMutableAttribute(Attributes.ATTACK_DAMAGE, 15.0D)
                    .createMutableAttribute(Attributes.ARMOR, 10.0D)
                    .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D).create());
            
            event.put(ULTIMATE_BOSS.get(), MonsterEntity.func_234295_eP_()
                    .createMutableAttribute(Attributes.MAX_HEALTH, 500.0D)
                    .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D)
                    .createMutableAttribute(Attributes.ATTACK_DAMAGE, 25.0D)
                    .createMutableAttribute(Attributes.ARMOR, 15.0D)
                    .createMutableAttribute(Attributes.FOLLOW_RANGE, 70.0D).create());
            
            event.put(CRAZY_WITHER.get(), MonsterEntity.func_234295_eP_()
                    .createMutableAttribute(Attributes.MAX_HEALTH, 250.0D)
                    .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4D)
                    .createMutableAttribute(Attributes.ATTACK_DAMAGE, 12.0D)
                    .createMutableAttribute(Attributes.ARMOR, 8.0D)
                    .createMutableAttribute(Attributes.FOLLOW_RANGE, 40.0D).create());
            
            event.put(MEGA_DRAGON.get(), MonsterEntity.func_234295_eP_()
                    .createMutableAttribute(Attributes.MAX_HEALTH, 400.0D)
                    .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5D)
                    .createMutableAttribute(Attributes.ATTACK_DAMAGE, 20.0D)
                    .createMutableAttribute(Attributes.ARMOR, 12.0D)
                    .createMutableAttribute(Attributes.FOLLOW_RANGE, 80.0D).create());
        }
    }
}
