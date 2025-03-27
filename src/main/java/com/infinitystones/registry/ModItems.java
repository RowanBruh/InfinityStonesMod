package com.infinitystones.registry;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.NanoTechItems;
import net.minecraft.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all mod items
 */
public class ModItems {
    // DeferredRegister for items
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Placeholder items for tabs - these will be replaced with actual items later
    public static final RegistryObject<Item> SPACE_STONE = ITEMS.register("space_stone", 
            () -> new Item(new Item.Properties()));
            
    public static final RegistryObject<Item> ROWAN_LUCKY_BLOCK = ITEMS.register("rowan_lucky_block", 
            () -> new Item(new Item.Properties()));
            
    public static final RegistryObject<Item> BONC_BOX = ITEMS.register("bonc_box", 
            () -> new Item(new Item.Properties()));
    
    /**
     * Handle BuildCreativeModeTabContentsEvent to add items to creative tabs
     */
    public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        // Delegate to other item classes to handle their items
        NanoTechItems.addItemsToTabs(event);
        
        // Add more items to tabs here or delegate to other classes
    }
    
    /**
     * Register this content with the appropriate event bus
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        NanoTechItems.register(eventBus);
        
        // Register the event handler for creative tabs
        eventBus.addListener(ModItems::buildCreativeModeTabs);
    }
}