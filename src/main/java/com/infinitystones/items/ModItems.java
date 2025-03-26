package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityStones.StoneType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Infinity Stones
    public static final RegistryObject<Item> SPACE_STONE = ITEMS.register(
            StoneType.SPACE.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.SPACE));
    
    public static final RegistryObject<Item> MIND_STONE = ITEMS.register(
            StoneType.MIND.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.MIND));
    
    public static final RegistryObject<Item> REALITY_STONE = ITEMS.register(
            StoneType.REALITY.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.REALITY));
    
    public static final RegistryObject<Item> POWER_STONE = ITEMS.register(
            StoneType.POWER.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.POWER));
    
    public static final RegistryObject<Item> TIME_STONE = ITEMS.register(
            StoneType.TIME.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.TIME));
    
    public static final RegistryObject<Item> SOUL_STONE = ITEMS.register(
            StoneType.SOUL.getRegistryName(),
            () -> new InfinityStones.InfinityStoneItem(StoneType.SOUL));
    
    // Infinity Gauntlet
    public static final RegistryObject<Item> INFINITY_GAUNTLET = ITEMS.register(
            "infinity_gauntlet",
            () -> new InfinityGauntlet());
    
    // Insane Craft Items based on Insane Craft series
    public static final RegistryObject<Item> ULTIMATE_INGOT = ITEMS.register(
            "ultimate_ingot",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
    
    public static final RegistryObject<Item> CHAOS_SHARD = ITEMS.register(
            "chaos_shard",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
    
    public static final RegistryObject<Item> COSMIC_DUST = ITEMS.register(
            "cosmic_dust",
            () -> new Item(new Item.Properties().group(InfinityStonesMod.INFINITY_GROUP)));
}
