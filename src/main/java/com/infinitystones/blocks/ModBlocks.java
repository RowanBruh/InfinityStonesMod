package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.blocks.skiddzie.EnhancedCommandBlock;
import com.infinitystones.blocks.skiddzie.EnhancedCommandBlockTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InfinityStonesMod.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, InfinityStonesMod.MOD_ID);
    
    // SkiddziePlays Blocks
    
    // Enhanced Command Block - A command block that can be used by non-creative players
    public static final RegistryObject<Block> ENHANCED_COMMAND_BLOCK = registerBlock("enhanced_command_block",
            EnhancedCommandBlock::new, InfinityStonesMod.ROWAN_INDUSTRIES_TAB);
    
    public static final RegistryObject<TileEntityType<EnhancedCommandBlockTileEntity>> ENHANCED_COMMAND_BLOCK_TILE_ENTITY = 
            TILE_ENTITIES.register("enhanced_command_block", 
                    () -> TileEntityType.Builder.create(
                            EnhancedCommandBlockTileEntity::new, 
                            ENHANCED_COMMAND_BLOCK.get())
                            .build(null));
    
    // Helper method for registering blocks
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Group group) {
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties().group(group)));
        return registeredBlock;
    }
}