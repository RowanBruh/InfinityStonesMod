package com.infinitystones.entities;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

/**
 * Renders the Skeppy boss with a custom model
 */
public class SkeppyBossRenderer extends BipedRenderer<SkeppyBossEntity, PlayerModel<SkeppyBossEntity>> {
    // Skeppy's skin texture path
    private static final ResourceLocation TEXTURE = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "textures/entity/skeppy_boss.png");

    public SkeppyBossRenderer(EntityRendererManager renderManager) {
        super(renderManager, new PlayerModel<>(0.0F, false), 0.7F);
        
        // Set model to slim arms version (like Alex model)
        this.entityModel = new PlayerModel<>(0.0F, false);
    }

    @Override
    public ResourceLocation getEntityTexture(SkeppyBossEntity entity) {
        return TEXTURE;
    }
    
    @Override
    protected void preRenderCallback(SkeppyBossEntity entity, float partialTicks) {
        // Make Skeppy boss 2x normal size
        float scale = 2.0F;
        this.shadowSize = 0.7F * scale;
        
        super.preRenderCallback(entity, partialTicks);
    }
}