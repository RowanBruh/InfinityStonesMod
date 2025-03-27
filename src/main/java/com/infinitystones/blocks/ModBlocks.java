package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for all mod blocks
 */
public class ModBlocks {
    // Block registry
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InfinityStonesMod.MOD_ID);
    
    // Block Item registry
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Admin Command Block
    public static final RegistryObject<Block> ADMIN_COMMAND_BLOCK = BLOCKS.register("admin_command_block", 
            AdminCommandBlock::new);
    
    static {
        // Register block items
        ITEMS.register("admin_command_block", 
                () -> new BlockItem(ADMIN_COMMAND_BLOCK.get(), 
                        new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    }
    
    // Lucky Block
    public static final RegistryObject<Block> ROWAN_LUCKY_BLOCK = BLOCKS.register("rowan_lucky_block",
            RowanLuckyBlock::new);
    
    static {
        // Register Rowan Lucky Block item
        ITEMS.register("rowan_lucky_block",
                () -> new BlockItem(ROWAN_LUCKY_BLOCK.get(),
                        new Item.Properties().group(ModItemGroups.ROWAN_INDUSTRIES)));
    }
}