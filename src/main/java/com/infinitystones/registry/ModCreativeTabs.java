package com.infinitystones.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.infinitystones.InfinityStonesMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Manages creative mode tabs for the mod
 */
public class ModCreativeTabs {
    // DeferredRegister for creative mode tabs
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InfinityStonesMod.MOD_ID);
    
    // Items to be added to each tab (populated during registration)
    private static final List<Supplier<? extends Item>> INFINITY_STONES_TAB_ITEMS = new ArrayList<>();
    private static final List<Supplier<? extends Item>> BONCS_ITEMS_TAB_ITEMS = new ArrayList<>();
    private static final List<Supplier<? extends Item>> ROWAN_INDUSTRIES_TAB_ITEMS = new ArrayList<>();
    
    // Creative mode tabs
    public static final RegistryObject<CreativeModeTab> INFINITY_STONES_TAB = CREATIVE_MODE_TABS.register("infinity_stones",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.infinitystones.infinity_stones"))
                    .icon(() -> new ItemStack(Items.NETHER_STAR)) // Placeholder icon, will be replaced
                    .displayItems((parameters, output) -> {
                        INFINITY_STONES_TAB_ITEMS.forEach(itemSupplier -> output.accept(itemSupplier.get()));
                    })
                    .build()
    );
    
    public static final RegistryObject<CreativeModeTab> BONCS_ITEMS_TAB = CREATIVE_MODE_TABS.register("boncs_items",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.infinitystones.boncs_items"))
                    .icon(() -> new ItemStack(Items.DIAMOND_SWORD)) // Placeholder icon, will be replaced
                    .displayItems((parameters, output) -> {
                        BONCS_ITEMS_TAB_ITEMS.forEach(itemSupplier -> output.accept(itemSupplier.get()));
                    })
                    .build()
    );
    
    public static final RegistryObject<CreativeModeTab> ROWAN_INDUSTRIES_TAB = CREATIVE_MODE_TABS.register("rowan_industries",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.infinitystones.rowan_industries"))
                    .icon(() -> new ItemStack(Items.REDSTONE_BLOCK)) // Placeholder icon, will be replaced
                    .displayItems((parameters, output) -> {
                        ROWAN_INDUSTRIES_TAB_ITEMS.forEach(itemSupplier -> output.accept(itemSupplier.get()));
                    })
                    .build()
    );
    
    /**
     * Add an item to a specific creative tab
     * @param tab The tab to add the item to
     * @param item The item to add
     */
    public static void addToTab(RegistryObject<CreativeModeTab> tab, Item item) {
        if (tab == INFINITY_STONES_TAB) {
            INFINITY_STONES_TAB_ITEMS.add(() -> item);
        } else if (tab == BONCS_ITEMS_TAB) {
            BONCS_ITEMS_TAB_ITEMS.add(() -> item);
        } else if (tab == ROWAN_INDUSTRIES_TAB) {
            ROWAN_INDUSTRIES_TAB_ITEMS.add(() -> item);
        }
    }
    
    /**
     * Register the creative tabs
     * @param eventBus The mod event bus
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}