package com.infinitystones.items;

import java.util.List;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import com.infinitystones.gui.GauntletContainer;
import com.infinitystones.items.InfinityStones.StoneType;
import com.infinitystones.util.StoneAbilities;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

public class InfinityGauntlet extends Item implements INamedContainerProvider {
    
    public InfinityGauntlet() {
        super(new Item.Properties()
                .group(InfinityStonesMod.INFINITY_GROUP)
                .maxStackSize(1)
                .setNoRepair());
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                // Open the GUI to insert/remove stones
                NetworkHooks.openGui((ServerPlayerEntity) playerIn, this, buf -> {});
            } else {
                // Activate the gauntlet's power if stones are inserted
                if (hasAllStones(stack)) {
                    if (ModConfig.COMMON_CONFIG.enableGauntletSnapAbility.get()) {
                        if (!playerIn.getCooldownTracker().hasCooldown(this)) {
                            StoneAbilities.activateInfinityGauntlet(playerIn, worldIn);
                            playerIn.getCooldownTracker().setCooldown(this, 
                                    ModConfig.COMMON_CONFIG.gauntletSnapCooldown.get() * 20);
                        } else {
                            playerIn.sendMessage(new StringTextComponent("The Gauntlet is recharging...").mergeStyle(TextFormatting.RED), 
                                    playerIn.getUniqueID());
                        }
                    }
                } else {
                    // Activate individual stone effects based on inserted stones
                    activateInsertedStones(stack, playerIn, worldIn);
                }
            }
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    private void activateInsertedStones(ItemStack stack, PlayerEntity player, World world) {
        ItemStackHandler stoneHandler = getStoneHandler(stack);
        
        for (int i = 0; i < stoneHandler.getSlots(); i++) {
            ItemStack stoneStack = stoneHandler.getStackInSlot(i);
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                InfinityStones.InfinityStoneItem stone = (InfinityStones.InfinityStoneItem) stoneStack.getItem();
                switch (stone.getStoneType()) {
                    case SPACE:
                        StoneAbilities.activateSpaceStone(player, world);
                        break;
                    case MIND:
                        StoneAbilities.activateMindStone(player, world);
                        break;
                    case REALITY:
                        StoneAbilities.activateRealityStone(player, world);
                        break;
                    case POWER:
                        StoneAbilities.activatePowerStone(player, world);
                        break;
                    case TIME:
                        StoneAbilities.activateTimeStone(player, world);
                        break;
                    case SOUL:
                        StoneAbilities.activateSoulStone(player, world);
                        break;
                }
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("The Infinity Gauntlet").mergeStyle(TextFormatting.GOLD));
        tooltip.add(new StringTextComponent("Right-click to use the power of inserted stones"));
        tooltip.add(new StringTextComponent("Shift+Right-click to open the gauntlet inventory"));
        
        if (hasAllStones(stack)) {
            tooltip.add(new StringTextComponent("§lAll Infinity Stones collected!").mergeStyle(TextFormatting.DARK_PURPLE));
            if (ModConfig.COMMON_CONFIG.enableGauntletSnapAbility.get()) {
                tooltip.add(new StringTextComponent("Right-click to snap and eliminate half of all life"));
            }
        } else {
            // Show which stones are inserted
            tooltip.add(new StringTextComponent("Stones inserted:").mergeStyle(TextFormatting.GRAY));
            
            ItemStackHandler stoneHandler = getStoneHandler(stack);
            boolean anyStones = false;
            
            for (int i = 0; i < stoneHandler.getSlots(); i++) {
                ItemStack stoneStack = stoneHandler.getStackInSlot(i);
                if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                    InfinityStones.InfinityStoneItem stone = (InfinityStones.InfinityStoneItem) stoneStack.getItem();
                    tooltip.add(new StringTextComponent(" - " + stone.getStoneType().name()).mergeStyle(stone.getStoneType().getColor()));
                    anyStones = true;
                }
            }
            
            if (!anyStones) {
                tooltip.add(new StringTextComponent(" None").mergeStyle(TextFormatting.GRAY));
            }
        }
        
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    public static ItemStackHandler getStoneHandler(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(6);
        if (stack.hasTag() && stack.getTag().contains("Stones")) {
            handler.deserializeNBT((CompoundNBT) stack.getTag().get("Stones"));
        }
        return handler;
    }
    
    public static void saveStoneHandler(ItemStack stack, ItemStackHandler handler) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        stack.getTag().put("Stones", handler.serializeNBT());
    }
    
    public boolean hasAllStones(ItemStack stack) {
        ItemStackHandler handler = getStoneHandler(stack);
        boolean[] foundStones = new boolean[StoneType.values().length];
        
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stoneStack = handler.getStackInSlot(i);
            if (!stoneStack.isEmpty() && stoneStack.getItem() instanceof InfinityStones.InfinityStoneItem) {
                InfinityStones.InfinityStoneItem stone = (InfinityStones.InfinityStoneItem) stoneStack.getItem();
                foundStones[stone.getStoneType().ordinal()] = true;
            }
        }
        
        for (boolean found : foundStones) {
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Infinity Gauntlet");
    }
    
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GauntletContainer(windowId, playerInventory, player.getHeldItemMainhand());
    }
}
