package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compatibility with Patchouli
 * Adds a comprehensive guidebook for the Infinity Stones mod
 */
public class PatchouliCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * Initialize Patchouli compatibility
     */
    public static void init() {
        LOGGER.info("Initializing Patchouli compatibility");
        
        // Register the IMC event handler
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(PatchouliCompat::enqueueIMC);
    }
    
    /**
     * Send InterModComms to Patchouli
     */
    private static void enqueueIMC(InterModEnqueueEvent event) {
        LOGGER.info("Sending Patchouli IMC messages");
        
        // Register the Infinity Stones Book
        InterModComms.sendTo("patchouli", "register_book", 
                () -> new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_stones_guide"));
    }
    
    /**
     * Creates book data folder structure and necessary book files
     */
    public static void setupBookResources(ResourceManager resourceManager) {
        if (!ModCompat.isModLoaded(ModCompat.PATCHOULI_MOD_ID)) {
            return;
        }
        
        LOGGER.info("Setting up Infinity Stones guidebook resources");
        
        // In actual implementation, we would create:
        // - book.json
        // - Categories (infinity_stones, gauntlets, greek_gods, youtube_content, etc.)
        // - Entries for each category
        // - Templates for custom pages
    }
    
    /**
     * Book section information structure
     */
    public static class BookSection {
        private final String name;
        private final String category;
        private final int sortIndex;
        
        public BookSection(String name, String category, int sortIndex) {
            this.name = name;
            this.category = category;
            this.sortIndex = sortIndex;
        }
        
        // Book section definitions
        public static final BookSection INTRODUCTION = new BookSection("introduction", "main", 0);
        public static final BookSection INFINITY_STONES = new BookSection("infinity_stones", "stones", 10);
        public static final BookSection GAUNTLETS = new BookSection("gauntlets", "gauntlets", 20);
        public static final BookSection YOUTUBE_CONTENT = new BookSection("youtube_content", "youtube", 30);
        public static final BookSection GREEK_GODS = new BookSection("greek_gods", "gods", 40);
        public static final BookSection DIMENSIONS = new BookSection("dimensions", "dimensions", 50);
        public static final BookSection BOSS_BATTLES = new BookSection("boss_battles", "bosses", 60);
        public static final BookSection COMPATIBILITY = new BookSection("compatibility", "compat", 70);
    }
}