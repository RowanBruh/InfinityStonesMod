package com.infinitystones.items.tools;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.gen.feature.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;

public class BaseLocatorWandItem extends Item {
    private final Structure<?> targetStructure;
    private final String structureName;
    private final TextFormatting textColor;

    public BaseLocatorWandItem(Properties properties, Structure<?> targetStructure, String structureName, TextFormatting textColor) {
        super(properties);
        this.targetStructure = targetStructure;
        this.structureName = structureName;
        this.textColor = textColor;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        
        if (!worldIn.isRemote) {
            findNearestStructure((ServerWorld) worldIn, playerIn);
            
            // Damage the wand after use
            if (!playerIn.abilities.isCreativeMode) {
                itemstack.damageItem(1, playerIn, (player) -> {
                    player.sendBreakAnimation(handIn);
                });
            }
        }
        
        // Play sound effect
        worldIn.playSound(playerIn, playerIn.getPosition(), 
                SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 0.8F, 1.0F);
        
        // Add cooldown
        playerIn.getCooldownTracker().setCooldown(this, 200); // 10 second cooldown
        
        return ActionResult.resultSuccess(itemstack);
    }

    /**
     * Find the nearest structure of the target type and notify the player
     */
    private void findNearestStructure(ServerWorld world, PlayerEntity player) {
        // Get player position
        BlockPos playerPos = player.getPosition();
        
        // Find the nearest structure
        BlockPos structurePos = world.func_241117_a_(targetStructure, playerPos, 100, false);
        
        if (structurePos != null) {
            // Calculate distance and direction
            int distance = (int) Math.sqrt(playerPos.distanceSq(structurePos.getX(), structurePos.getY(), structurePos.getZ()));
            String direction = getDirectionString(playerPos, structurePos);
            
            // Send message to player
            player.sendMessage(
                new StringTextComponent(structureName + " found: ")
                    .mergeStyle(textColor)
                    .appendSibling(new StringTextComponent(distance + " blocks " + direction))
                    .appendSibling(new StringTextComponent(" [" + structurePos.getX() + ", " + structurePos.getY() + ", " + structurePos.getZ() + "]")
                        .mergeStyle(TextFormatting.GRAY)),
                player.getUniqueID());
            
            // Display effects
            for (int i = 0; i < 8; i++) {
                double offsetX = player.getRNG().nextGaussian() * 0.02D;
                double offsetY = player.getRNG().nextGaussian() * 0.02D;
                double offsetZ = player.getRNG().nextGaussian() * 0.02D;
                
                world.spawnParticle(ParticleTypes.END_ROD, 
                        player.getPosX() + offsetX, 
                        player.getPosY() + 1.0 + offsetY, 
                        player.getPosZ() + offsetZ,
                        1, 0, 0, 0, 0);
            }
        } else {
            // No structure found
            player.sendMessage(
                new StringTextComponent("No " + structureName + " found within 1600 blocks.")
                    .mergeStyle(TextFormatting.RED),
                player.getUniqueID());
        }
    }

    /**
     * Get a string representation of the direction to the target
     */
    private String getDirectionString(BlockPos from, BlockPos to) {
        // Calculate differences
        int xDiff = to.getX() - from.getX();
        int zDiff = to.getZ() - from.getZ();
        
        // Determine primary direction
        if (Math.abs(xDiff) > Math.abs(zDiff) * 2) {
            // Primarily east/west
            return xDiff > 0 ? "east" : "west";
        } else if (Math.abs(zDiff) > Math.abs(xDiff) * 2) {
            // Primarily north/south
            return zDiff > 0 ? "south" : "north";
        } else {
            // Diagonal
            String ns = zDiff > 0 ? "south" : "north";
            String ew = xDiff > 0 ? "east" : "west";
            return ns + "-" + ew;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.infinitystones.base_locator", structureName)
                .mergeStyle(TextFormatting.GRAY));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}