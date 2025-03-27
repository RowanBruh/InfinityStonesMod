package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.blocks.traps.TrapBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, InfinityStonesMod.MOD_ID);
    public static IEventBus MOD_EVENT_BUS;

    // One Way Block
    public static final RegistryObject<Block> ONE_WAY_BLOCK = BLOCKS.register("one_way_block",
            () -> new OneWayBlock());

    // Register the block registry
    public static void register(IEventBus eventBus) {
        MOD_EVENT_BUS = eventBus;
        BLOCKS.register(eventBus);
        TrapBlocks.BLOCKS.register(eventBus);
        ModTileEntities.TILE_ENTITIES.register(eventBus);
    }
}