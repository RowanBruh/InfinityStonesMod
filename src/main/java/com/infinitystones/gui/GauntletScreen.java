package com.infinitystones.gui;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityGauntlet.GauntletContainer;
import com.infinitystones.items.InfinityStones.StoneType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GauntletScreen extends ContainerScreen<GauntletContainer> {
    // Background texture for the GUI
    private static final ResourceLocation BACKGROUND_TEXTURE = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "textures/gui/gauntlet_gui.png");
    
    // Stone textures
    private static final ResourceLocation[] STONE_TEXTURES = new ResourceLocation[StoneType.values().length];
    
    // Coordinates for stone slots in the GUI
    private static final int[][] STONE_POSITIONS = {
            {80, 20},  // SPACE
            {50, 40},  // MIND
            {110, 40}, // REALITY
            {50, 70},  // POWER
            {110, 70}, // TIME
            {80, 90}   // SOUL
    };
    
    static {
        // Initialize stone textures
        for (StoneType stoneType : StoneType.values()) {
            STONE_TEXTURES[stoneType.ordinal()] = stoneType.getTextureLocation();
        }
    }
    
    public GauntletScreen(GauntletContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        
        // Set GUI dimensions
        this.xSize = 176;
        this.ySize = 166;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        
        // Render tooltips for stone slots when hovering
        for (int i = 0; i < StoneType.values().length; i++) {
            int x = this.guiLeft + STONE_POSITIONS[i][0];
            int y = this.guiTop + STONE_POSITIONS[i][1];
            
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                StoneType stoneType = StoneType.values()[i];
                this.renderTooltip(matrixStack, 
                        new StringTextComponent(stoneType.name() + " STONE")
                                .mergeStyle(stoneType.getColor()), 
                        mouseX, mouseY);
            }
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Bind and draw the background texture
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        // Draw stone slot highlights based on whether stones are equipped
        for (int i = 0; i < StoneType.values().length; i++) {
            // In a full implementation, we would check if stones are equipped here
            // For simplicity, we're just drawing the slots
            int x = this.guiLeft + STONE_POSITIONS[i][0];
            int y = this.guiTop + STONE_POSITIONS[i][1];
            
            // Draw slot background
            this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            this.blit(matrixStack, x, y, 176, 0, 16, 16);
            
            // Draw stone icon
            // In a full implementation, this would only draw if the stone is equipped
            // this.minecraft.getTextureManager().bindTexture(STONE_TEXTURES[i]);
            // this.blit(matrixStack, x, y, 0, 0, 16, 16);
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Draw title
        this.font.drawText(matrixStack, 
                new StringTextComponent("Infinity Gauntlet").mergeStyle(TextFormatting.GOLD), 
                8, 6, 0xFFFFFF);
        
        // Draw player inventory label
        this.font.drawText(matrixStack, 
                this.playerInventory.getDisplayName(), 
                8, this.ySize - 96 + 2, 0x404040);
    }
}