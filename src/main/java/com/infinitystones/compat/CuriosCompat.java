package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.function.Supplier;

/**
 * Compatibility handler for Curios API
 */
public class CuriosCompat {
    private static final String CURIOS_MOD_ID = "curios";
    
    /**
     * Check if Curios is loaded
     */
    public static boolean isCuriosLoaded() {
        return ModList.get().isLoaded(CURIOS_MOD_ID);
    }
    
    /**
     * Register a new Curios slot type
     */
    private static void registerSlotType(String id, ResourceLocation icon, String translationKey, int priority, int size, boolean isVisible) {
        InterModComms.sendTo(CURIOS_MOD_ID, SlotTypeMessage.REGISTER_TYPE, 
                () -> new SlotTypeMessage.Builder(id)
                        .icon(icon)
                        .priority(priority)
                        .size(size)
                        .translationKey(translationKey)
                        .visible(isVisible)
                        .build()
        );
    }
    
    /**
     * Register an infinity stone as curio
     */
    private static void registerInfinityStone(RegistryObject<Item> stone) {
        InterModComms.sendTo(CURIOS_MOD_ID, "register_curio", 
                (Supplier<Object>) () -> stone.get());
    }
    
    /**
     * Register event listeners for Curios
     */
    private static void registerEvents() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CuriosCompat::enqueueIMC);
    }
    
    /**
     * Handle InterModEnqueueEvent to register curio items
     */
    private static void enqueueIMC(final InterModEnqueueEvent event) {
        if (!isCuriosLoaded()) {
            return;
        }
        
        InfinityStonesMod.LOGGER.info("Registering items with Curios API");
        
        // Register Infinity Stone slot
        registerSlotType(
                "infinity_stone", 
                new ResourceLocation(InfinityStonesMod.MOD_ID, "item/empty_infinity_stone_slot"),
                "curios.slot.infinity_stone",
                100,
                6,
                true
        );
        
        // Register Infinity Gauntlet slot
        registerSlotType(
                "gauntlet", 
                new ResourceLocation(InfinityStonesMod.MOD_ID, "item/empty_gauntlet_slot"),
                "curios.slot.gauntlet",
                90,
                1,
                true
        );
        
        // Register Greek God item slot
        registerSlotType(
                "greek_god_item", 
                new ResourceLocation(InfinityStonesMod.MOD_ID, "item/empty_greek_god_slot"),
                "curios.slot.greek_god_item",
                80,
                8,
                true
        );
        
        // Register Infinity Stones with Curios
        registerInfinityStone(ModItems.SPACE_STONE);
        registerInfinityStone(ModItems.MIND_STONE);
        registerInfinityStone(ModItems.REALITY_STONE);
        registerInfinityStone(ModItems.POWER_STONE);
        registerInfinityStone(ModItems.TIME_STONE);
        registerInfinityStone(ModItems.SOUL_STONE);
        
        // Register Infected Infinity Stones with Curios
        registerInfinityStone(ModItems.INFECTED_SPACE_STONE);
        registerInfinityStone(ModItems.INFECTED_MIND_STONE);
        registerInfinityStone(ModItems.INFECTED_REALITY_STONE);
        registerInfinityStone(ModItems.INFECTED_POWER_STONE);
        registerInfinityStone(ModItems.INFECTED_TIME_STONE);
        registerInfinityStone(ModItems.INFECTED_SOUL_STONE);
        
        // Register Gauntlets with Curios
        InterModComms.sendTo(CURIOS_MOD_ID, "register_curio", (Supplier<Object>) () -> ModItems.INFINITY_GAUNTLET.get());
        InterModComms.sendTo(CURIOS_MOD_ID, "register_curio", (Supplier<Object>) () -> ModItems.ADVANCED_INFINITY_GAUNTLET.get());
        InterModComms.sendTo(CURIOS_MOD_ID, "register_curio", (Supplier<Object>) () -> ModItems.INFECTED_INFINITY_GAUNTLET.get());
    }
    
    /**
     * Register compatibility with Curios
     * This is called during mod initialization
     */
    public static void register() {
        if (!isCuriosLoaded()) {
            InfinityStonesMod.LOGGER.info("Curios not detected, skipping Curios compatibility setup");
            return;
        }
        
        InfinityStonesMod.LOGGER.info("Setting up Curios compatibility");
        
        // Register event listeners
        registerEvents();
    }
}