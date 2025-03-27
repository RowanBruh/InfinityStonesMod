package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Creative Mode Tabs
    public static final ItemGroup INFINITY_STONES_TAB = new ItemGroup("infinitystones") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.SPACE_STONE.get());
        }
    };
    
    public static final ItemGroup ROWAN_INDUSTRIES_TAB = new ItemGroup("rowanindustries") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.ROWAN_LUCKY_BLOCK.get());
        }
    };
    
    public static final ItemGroup BONCS_ITEMS_TAB = new ItemGroup("boncsitems") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.BONC_BOX.get());
        }
    };
    
    // Infinity Stones
    public static final class Items {
        // Placeholder items for creative tabs
        public static final RegistryObject<Item> SPACE_STONE = ITEMS.register("space_stone",
                () -> new Item(new Item.Properties().group(INFINITY_STONES_TAB)));
                
        public static final RegistryObject<Item> ROWAN_LUCKY_BLOCK = ITEMS.register("rowan_lucky_block",
                () -> new Item(new Item.Properties().group(ROWAN_INDUSTRIES_TAB)));
                
        public static final RegistryObject<Item> BONC_BOX = ITEMS.register("bonc_box",
                () -> new Item(new Item.Properties().group(BONCS_ITEMS_TAB)));
    }
}