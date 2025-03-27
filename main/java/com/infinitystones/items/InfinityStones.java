package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.util.StoneAbilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class InfinityStones {

    /**
     * Enum defining the different types of Infinity Stones
     */
    public enum StoneType {
        SPACE("space_stone", TextFormatting.BLUE, "Manipulate space and teleport"),
        MIND("mind_stone", TextFormatting.YELLOW, "Control the minds of others"),
        REALITY("reality_stone", TextFormatting.RED, "Alter reality itself"),
        POWER("power_stone", TextFormatting.DARK_PURPLE, "Unleash destructive energy"),
        TIME("time_stone", TextFormatting.GREEN, "Manipulate time"),
        SOUL("soul_stone", TextFormatting.GOLD, "Control life and death");
        
        private final String registryName;
        private final TextFormatting color;
        private final String description;
        
        StoneType(String registryName, TextFormatting color, String description) {
            this.registryName = registryName;
            this.color = color;
            this.description = description;
        }
        
        public String getRegistryName() {
            return registryName;
        }
        
        public TextFormatting getColor() {
            return color;
        }
        
        public String getDescription() {
            return description;
        }
        
        public ResourceLocation getTextureLocation() {
            return new ResourceLocation(InfinityStonesMod.MOD_ID, "textures/item/" + registryName + ".png");
        }
    }
    
    /**
     * The Infinity Stone item class
     */
    public static class InfinityStoneItem extends Item {
        private final StoneType stoneType;
        
        public InfinityStoneItem(StoneType stoneType) {
            super(new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .maxStackSize(1)
                    .setNoRepair());
            this.stoneType = stoneType;
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack itemStack = playerIn.getHeldItem(handIn);
            
            if (!playerIn.getCooldownTracker().hasCooldown(this)) {
                boolean success = StoneAbilities.activateStoneAbility(worldIn, playerIn, stoneType);
                
                if (success) {
                    return ActionResult.resultSuccess(itemStack);
                }
            }
            
            return ActionResult.resultPass(itemStack);
        }
        
        @OnlyIn(Dist.CLIENT)
        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            tooltip.add(new StringTextComponent(stoneType.getDescription()).mergeStyle(stoneType.getColor()));
            tooltip.add(new StringTextComponent("Right click to use").mergeStyle(TextFormatting.GRAY));
            
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
        
        public StoneType getStoneType() {
            return stoneType;
        }
    }
}