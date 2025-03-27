package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InfinityArmorItem extends ArmorItem {

    public static final InfinityArmorMaterial MATERIAL = new InfinityArmorMaterial();

    public InfinityArmorItem(EquipmentSlotType slot) {
        super(
            MATERIAL,
            slot,
            new Item.Properties()
                .group(InfinityStonesMod.INFINITY_GROUP)
                .maxStackSize(1)
                .setNoRepair()
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && entityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityIn;
            InsaneCraftWeapons.applyInfinityArmorEffect(player, stack);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ModItems.ULTIMATE_INGOT.get();
    }

    public static class InfinityArmorMaterial implements IArmorMaterial {
        private static final int[] DURABILITY = new int[]{1000, 1500, 1300, 1100};
        private static final int[] PROTECTION = new int[]{8, 10, 12, 8};

        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return DURABILITY[slotIn.getIndex()];
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return PROTECTION[slotIn.getIndex()];
        }

        @Override
        public int getEnchantability() {
            return 30;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromItems(ModItems.ULTIMATE_INGOT.get());
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getName() {
            return "infinity";
        }

        @Override
        public float getToughness() {
            return 5.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.5F;
        }
    }
}