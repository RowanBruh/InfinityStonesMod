package com.infinitystones.blocks;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = 
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, InfinityStonesMod.MOD_ID);

    // Register the OneWayBlockTileEntity
    public static final RegistryObject<TileEntityType<OneWayBlockTileEntity>> ONE_WAY_BLOCK_TILE_ENTITY =
            TILE_ENTITIES.register("one_way_block_tile_entity",
                    () -> TileEntityType.Builder.create(
                            OneWayBlockTileEntity::new,
                            ModBlocks.ONE_WAY_BLOCK.get()
                    ).build(null));
}