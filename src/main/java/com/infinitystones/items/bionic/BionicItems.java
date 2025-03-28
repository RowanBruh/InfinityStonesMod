package com.infinitystones.items.bionic;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BionicItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // TNT Sword - Explodes on hit, can be used to create explosions at range
    public static final RegistryObject<Item> TNT_SWORD = ITEMS.register("tnt_sword", 
            () -> new TntSwordItem(ItemTier.DIAMOND, 4, -2.4F, 
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.RARE)
                    .group(InfinityStonesMod.BONC_TAB)));
    
    // Wormhole Generator - Creates portals to teleport the player
    public static final RegistryObject<Item> WORMHOLE_GENERATOR = ITEMS.register("wormhole_generator", 
            () -> new WormholeGeneratorItem(
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC)
                    .group(InfinityStonesMod.BONC_TAB)));
}