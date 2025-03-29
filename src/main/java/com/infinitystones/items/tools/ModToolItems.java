package com.infinitystones.items.tools;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.world.structures.ModStructures;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModToolItems {
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Bionic Base Locator
    public static final RegistryObject<Item> BIONIC_BASE_LOCATOR = ITEMS.register("bionic_base_locator",
            () -> new BaseLocatorWandItem(
                    new Item.Properties()
                        .group(ItemGroup.TOOLS)
                        .maxStackSize(1)
                        .maxDamage(100), // Can be used 100 times
                    ModStructures.BIONIC_BASE.get(),
                    "Bionic's Base",
                    TextFormatting.BLUE
            ));
    
    // SkiddziePlays Base Locator
    public static final RegistryObject<Item> SKIDDZIE_BASE_LOCATOR = ITEMS.register("skiddzie_base_locator",
            () -> new BaseLocatorWandItem(
                    new Item.Properties()
                        .group(ItemGroup.TOOLS)
                        .maxStackSize(1)
                        .maxDamage(100), // Can be used 100 times
                    ModStructures.SKIDDZIE_BASE.get(),
                    "SkiddziePlays' Base",
                    TextFormatting.GREEN
            ));
    
    // Skeppy Arena Locator
    public static final RegistryObject<Item> SKEPPY_ARENA_LOCATOR = ITEMS.register("skeppy_arena_locator",
            () -> new BaseLocatorWandItem(
                    new Item.Properties()
                        .group(ItemGroup.TOOLS)
                        .maxStackSize(1)
                        .maxDamage(100), // Can be used 100 times
                    ModStructures.SKEPPY_ARENA.get(),
                    "Skeppy's Boss Arena",
                    TextFormatting.RED
            ));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}