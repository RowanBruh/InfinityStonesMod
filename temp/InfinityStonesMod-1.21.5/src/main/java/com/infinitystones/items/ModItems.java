package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InfinityStonesMod.MOD_ID);
    
    // Creative Mode Tabs    
    public static final RegistryObject<CreativeModeTab> INFINITY_STONES_TAB = CREATIVE_MODE_TABS.register("infinitystones",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.infinitystones"))
                    .icon(() -> new ItemStack(Items.SPACE_STONE.get()))
                    .displayItems((parameters, output) -> {
                        // Items will be added in the BuildCreativeModeTabContentsEvent
                    })
                    .build());
    
    public static final RegistryObject<CreativeModeTab> ROWAN_INDUSTRIES_TAB = CREATIVE_MODE_TABS.register("rowanindustries",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.rowanindustries"))
                    .icon(() -> new ItemStack(Items.ROWAN_LUCKY_BLOCK.get()))
                    .displayItems((parameters, output) -> {
                        // Items will be added in the BuildCreativeModeTabContentsEvent
                    })
                    .build());
    
    public static final RegistryObject<CreativeModeTab> BONCS_ITEMS_TAB = CREATIVE_MODE_TABS.register("boncsitems",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.boncsitems"))
                    .icon(() -> new ItemStack(Items.BONC_BOX.get()))
                    .displayItems((parameters, output) -> {
                        // Items will be added in the BuildCreativeModeTabContentsEvent
                    })
                    .build());
    
    // Event handler to populate tabs
    public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == INFINITY_STONES_TAB.get()) {
            // Add infinity stones items
            event.accept(Items.SPACE_STONE.get());
            // Add more items here
        } else if (event.getTab() == ROWAN_INDUSTRIES_TAB.get()) {
            // Add Rowan Industries items
            event.accept(Items.ROWAN_LUCKY_BLOCK.get());
            // Add more items here
        } else if (event.getTab() == BONCS_ITEMS_TAB.get()) {
            // Add Bonc's items
            event.accept(Items.BONC_BOX.get());
            // Add more items here
        }
    }
    
    // Register the creative tab event handler
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(ModItems::buildCreativeModeTabs);
    }
    
    // Infinity Stones
    public static final class Items {
        // Placeholder items for creative tabs
        public static final RegistryObject<Item> SPACE_STONE = ITEMS.register("space_stone",
                () -> new Item(new Item.Properties()));
                
        public static final RegistryObject<Item> ROWAN_LUCKY_BLOCK = ITEMS.register("rowan_lucky_block",
                () -> new Item(new Item.Properties()));
                
        public static final RegistryObject<Item> BONC_BOX = ITEMS.register("bonc_box",
                () -> new Item(new Item.Properties()));
    }
}