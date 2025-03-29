package com.infinitystones.compat;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Compatibility with Botania
 * Adds mana interaction for Infinity Stones and special Botania flower recipes
 */
public class BotaniaCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Botania integration resources
    public static final ResourceLocation INFINITY_PETAL = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_petal");
    public static final ResourceLocation MANA_INFUSION_STONE = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "mana_infusion/infinity_stone");
    public static final ResourceLocation LEXICON_ENTRY = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "lexicon/infinity_stones");
    
    // Mana costs
    public static final int MANA_COST_SPACE_ABILITY = 50000;
    public static final int MANA_COST_MIND_ABILITY = 40000;
    public static final int MANA_COST_REALITY_ABILITY = 60000;
    public static final int MANA_COST_POWER_ABILITY = 70000;
    public static final int MANA_COST_TIME_ABILITY = 55000;
    public static final int MANA_COST_SOUL_ABILITY = 45000;
    
    // Capability token for Botania mana handling (would be different in implementation)
    private static final Capability<Object> MANA_ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
    
    /**
     * Initialize Botania compatibility
     */
    public static void init() {
        LOGGER.info("Initializing Botania compatibility");
        
        // Register the IMC event handler
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(BotaniaCompat::enqueueIMC);
    }
    
    /**
     * Send InterModComms to Botania
     */
    private static void enqueueIMC(InterModEnqueueEvent event) {
        if (!ModCompat.isModLoaded(ModCompat.BOTANIA_MOD_ID)) {
            return;
        }
        
        LOGGER.info("Sending Botania IMC messages");
        
        // Register Botania Lexicon entries
        InterModComms.sendTo("botania", "register_lexicon_entry", 
                () -> new LexiconEntryInfo(LEXICON_ENTRY, "infinitystones.lexicon.infinity_stones"));
    }
    
    /**
     * Consumes mana from the player's mana items (Botania tablets, bands, etc.)
     * 
     * @param player The player using the ability
     * @param mana Amount of mana to consume
     * @return True if enough mana was available and consumed
     */
    public static boolean consumePlayerMana(Player player, int mana) {
        if (!ModCompat.isModLoaded(ModCompat.BOTANIA_MOD_ID)) {
            return false; // Botania not loaded, no mana system
        }
        
        // In actual implementation:
        // return ManaItemHandler.instance().requestManaExactForTool(stack, player, mana, true);
        return false; // Placeholder implementation
    }
    
    /**
     * Get the amount of mana in a player's mana items
     * 
     * @param player The player to check
     * @return The amount of mana available
     */
    public static int getPlayerMana(Player player) {
        if (!ModCompat.isModLoaded(ModCompat.BOTANIA_MOD_ID)) {
            return 0; // Botania not loaded, no mana system
        }
        
        // In actual implementation:
        // return ManaItemHandler.instance().getManaTotalForNetwork(player);
        return 0; // Placeholder implementation
    }
    
    /**
     * Try to transfer mana from an Infinity Stone to a Botania mana pool
     * 
     * @param level The world
     * @param pos The position of the mana pool
     * @param stack The Infinity Stone item stack
     * @param manaAmount Amount of mana to transfer
     * @return True if mana was transferred
     */
    public static boolean transferManaToPool(Level level, BlockPos pos, ItemStack stack, int manaAmount) {
        if (!ModCompat.isModLoaded(ModCompat.BOTANIA_MOD_ID)) {
            return false; // Botania not loaded, no mana system
        }
        
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile == null) {
            return false;
        }
        
        // This would normally use the Botania API
        LazyOptional<Object> cap = tile.getCapability(MANA_ITEM_HANDLER, Direction.UP);
        if (cap.isPresent()) {
            // In actual implementation:
            // IManaPool pool = cap.orElse(null);
            // if (pool != null && !pool.isFull()) {
            //     pool.receiveMana(manaAmount);
            //     return true;
            // }
        }
        
        return false; // Placeholder implementation
    }
    
    /**
     * Helper class for Lexicon entry registration
     * This would normally use real Botania API classes
     */
    private static class LexiconEntryInfo {
        private final ResourceLocation id;
        private final String unlocalizedName;
        
        public LexiconEntryInfo(ResourceLocation id, String unlocalizedName) {
            this.id = id;
            this.unlocalizedName = unlocalizedName;
        }
        
        // These methods would interact with actual Botania API
    }
}