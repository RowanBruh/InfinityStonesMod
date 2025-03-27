package com.infinitystones.items.bionic;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BionicCreativeTab extends ItemGroup {
    
    public static final BionicCreativeTab INSTANCE = new BionicCreativeTab();
    
    public BionicCreativeTab() {
        super(InfinityStonesMod.MOD_ID + ".boncs_items");
    }
    
    @Override
    public ItemStack createIcon() {
        // Use the Bionic Challenge Book as the tab icon
        return new ItemStack(BionicItems.BIONIC_CHALLENGE_BOOK);
    }
    
    @Override
    public void fill(NonNullList<ItemStack> items) {
        super.fill(items);
    }
}