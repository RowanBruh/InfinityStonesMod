package com.infinitystones.entities;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITIES, InfinityStonesMod.MOD_ID);
    
    // Register Skeppy Boss Entity
    public static final RegistryObject<EntityType<SkeppyBossEntity>> SKEPPY_BOSS = 
            ENTITY_TYPES.register("skeppy_boss",
            () -> EntityType.Builder.<SkeppyBossEntity>create(
                    SkeppyBossEntity::new, 
                    EntityClassification.MONSTER)
                    .size(1.4F, 2.7F) // Slightly larger than a normal player
                    .trackingRange(64)
                    .build(new ResourceLocation(InfinityStonesMod.MOD_ID, "skeppy_boss").toString())
            );
    
    /**
     * Register attribute modifiers for entities
     */
    public static void registerEntityAttributes() {
        // Register skeppy boss attributes
        net.minecraftforge.event.entity.EntityAttributeCreationEvent.class.cast(
            event -> event.put(SKEPPY_BOSS.get(), SkeppyBossEntity.getAttributes().create())
        );
    }
    
    /**
     * Register entity renderers on client side
     */
    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(SKEPPY_BOSS.get(), SkeppyBossRenderer::new);
    }
    
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}