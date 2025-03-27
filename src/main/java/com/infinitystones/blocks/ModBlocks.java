package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    // Create a deferred register for blocks
    public static final DeferredRegister<Block> BLOCKS = 
            DeferredRegister.create(ForgeRegistries.BLOCKS, InfinityStonesMod.MOD_ID);

    // Create a deferred register for items (for block items)
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);

    // Register the one-way block
    public static final RegistryObject<Block> ONE_WAY_BLOCK = registerBlock("one_way_block",
            OneWayBlock::new, 
            () -> new Item.Properties().group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB));

    /**
     * Helper method to register a block and its corresponding item
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier, Supplier<Item.Properties> itemProperties) {
        // Register the block
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        
        // Register the block item
        ITEMS.register(name, () -> new BlockItem(block.get(), itemProperties.get()));
        
        return block;
    }
}