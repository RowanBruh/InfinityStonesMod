package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.network.NetworkHandler;
import com.infinitystones.network.AdminCommandPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Screen for inputting admin commands
 */
public class AdminCommandBlockScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(InfinityStonesMod.MOD_ID, 
            "textures/gui/admin_command_block.png");
    
    private TextFieldWidget commandField;
    private Button executeButton;
    private Button cancelButton;
    private PlayerEntity player;
    
    public AdminCommandBlockScreen(PlayerEntity player) {
        super(new StringTextComponent("Admin Command Block"));
        this.player = player;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Command input field
        this.commandField = new TextFieldWidget(this.font, 
                this.width / 2 - 150, this.height / 2 - 20, 
                300, 20, new StringTextComponent("Command"));
        this.commandField.setMaxStringLength(256);
        this.commandField.setEnableBackgroundDrawing(true);
        this.commandField.setVisible(true);
        this.commandField.setFocused2(true);
        this.children.add(this.commandField);
        
        // Execute button
        this.executeButton = new Button(
                this.width / 2 - 155, this.height / 2 + 10, 
                150, 20, 
                new TranslationTextComponent("Execute"),
                button -> executeCommand());
        this.addButton(this.executeButton);
        
        // Cancel button
        this.cancelButton = new Button(
                this.width / 2 + 5, this.height / 2 + 10, 
                150, 20, 
                new TranslationTextComponent("Cancel"),
                button -> this.onClose());
        this.addButton(this.cancelButton);
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        
        // Title
        drawCenteredString(matrixStack, this.font, this.title.getString(), 
                this.width / 2, this.height / 2 - 50, 0xFF0000);
        
        // Draw command label
        drawString(matrixStack, this.font, "Enter Admin Command:", 
                this.width / 2 - 150, this.height / 2 - 35, 0xFFFFFF);
        
        // Render widgets
        this.commandField.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void executeCommand() {
        String command = this.commandField.getText();
        if (!command.isEmpty()) {
            // Send command to server via network packet
            NetworkHandler.INSTANCE.sendToServer(new AdminCommandPacket(command));
        }
        this.onClose();
    }
}