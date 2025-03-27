package com.infinitystones.tabs;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * Creative tabs for the Infinity Stones mod
 */
public class ModItemGroups {
    
    /**
     * Main creative tab for Infinity Stones mod items
     */
    public static final ItemGroup INFINITY_STONES = new ItemGroup(InfinityStonesMod.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.INFINITY_GAUNTLET.get());
        }
    };
    
    /**
     * Special creative tab for Rowan Industries items
     */
    public static final ItemGroup ROWAN_INDUSTRIES = new ItemGroup("rowan_industries") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COSMIC_BOX.get());
        }
    };
    
    /**
     * Special creative tab for Bonc's items
     */
    public static final ItemGroup BONCS_ITEMS = new ItemGroup("boncs_items") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.GRAVITY_HAMMER.get());
        }
    };
}