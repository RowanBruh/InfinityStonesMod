package com.infinitystones.gui;

import com.infinitystones.InfinityStonesMod;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GauntletScreen extends ContainerScreen<GauntletContainer> {
    private static final ResourceLocation GAUNTLET_GUI = new ResourceLocation(InfinityStonesMod.MOD_ID, 
            "textures/gui/gauntlet_gui.png");
    
    public GauntletScreen(GauntletContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        
        // Set the size of the GUI
        this.xSize = 176;
        this.ySize = 166;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        
        // Draw the slot descriptions when hovering
        for (int i = 0; i < 6; i++) {
            // Calculate the coordinates of each stone slot
            int slotX = leftPos + 44 + (i % 3) * 30;
            int slotY = topPos + 20 + (i / 3) * 30;
            
            if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                // Draw stone type tooltip based on slot index
                matrixStack.push();
                ITextComponent stoneType;
                
                switch (i) {
                    case 0:
                        stoneType = new StringTextComponent("Space Stone Slot").mergeStyle(TextFormatting.BLUE);
                        break;
                    case 1:
                        stoneType = new StringTextComponent("Mind Stone Slot").mergeStyle(TextFormatting.YELLOW);
                        break;
                    case 2:
                        stoneType = new StringTextComponent("Reality Stone Slot").mergeStyle(TextFormatting.RED);
                        break;
                    case 3:
                        stoneType = new StringTextComponent("Power Stone Slot").mergeStyle(TextFormatting.DARK_PURPLE);
                        break;
                    case 4:
                        stoneType = new StringTextComponent("Time Stone Slot").mergeStyle(TextFormatting.GREEN);
                        break;
                    case 5:
                        stoneType = new StringTextComponent("Soul Stone Slot").mergeStyle(TextFormatting.GOLD);
                        break;
                    default:
                        stoneType = new StringTextComponent("Stone Slot");
                }
                
                this.renderTooltip(matrixStack, stoneType, mouseX, mouseY);
                matrixStack.pop();
            }
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        // Draw the title
        String title = "Infinity Gauntlet";
        this.font.drawString(matrixStack, title, (this.xSize / 2f) - (this.font.getStringWidth(title) / 2f), 6, 0xFFD700);
        
        // Draw the player inventory title
        this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GAUNTLET_GUI);
        
        // Draw the background
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
        
        // Draw colored borders around stone slots
        drawColoredSlot(matrixStack, relX + 44, relY + 20, 0x8000BFFF); // Space Stone (Blue)
        drawColoredSlot(matrixStack, relX + 74, relY + 20, 0x80FFFF00); // Mind Stone (Yellow)
        drawColoredSlot(matrixStack, relX + 104, relY + 20, 0x80FF0000); // Reality Stone (Red)
        drawColoredSlot(matrixStack, relX + 44, relY + 50, 0x80800080); // Power Stone (Purple)
        drawColoredSlot(matrixStack, relX + 74, relY + 50, 0x8000FF00); // Time Stone (Green)
        drawColoredSlot(matrixStack, relX + 104, relY + 50, 0x80FFA500); // Soul Stone (Orange)
    }
    
    private void drawColoredSlot(MatrixStack matrixStack, int x, int y, int color) {
        // Draw a colored border around the slot
        fill(matrixStack, x - 1, y - 1, x + 19, y, color);      // Top
        fill(matrixStack, x - 1, y, x, y + 18, color);          // Left
        fill(matrixStack, x + 18, y, x + 19, y + 18, color);    // Right
        fill(matrixStack, x - 1, y + 18, x + 19, y + 19, color); // Bottom
    }
}
