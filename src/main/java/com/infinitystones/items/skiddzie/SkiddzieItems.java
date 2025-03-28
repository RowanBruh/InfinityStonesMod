package com.infinitystones.items.skiddzie;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SkiddzieItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);
    
    // Chaos Stone - Creates chaotic effects in an area
    public static final RegistryObject<Item> CHAOS_STONE = ITEMS.register("chaos_stone", 
            () -> new ChaosStoneItem(
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC)
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)));
    
    // Magic Mirror - Teleportation to saved locations
    public static final RegistryObject<Item> MAGIC_MIRROR = ITEMS.register("magic_mirror", 
            () -> new MagicMirrorItem(
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.RARE)
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)));
    
    // X-Ray Glasses - See through blocks to find valuable ores
    public static final RegistryObject<Item> XRAY_GLASSES = ITEMS.register("xray_glasses", 
            () -> new XRayGlassesItem(
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.RARE)
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)));
    
    // Weather Staff - Control weather cycles
    public static final RegistryObject<Item> WEATHER_STAFF = ITEMS.register("weather_staff", 
            () -> new WeatherStaffItem(
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.RARE)
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)));
    
    // Gravity Manipulator - Manipulate gravity in an area
    public static final RegistryObject<Item> GRAVITY_MANIPULATOR = ITEMS.register("gravity_manipulator", 
            () -> new GravityManipulatorItem(
                    new Item.Properties()
                    .maxStackSize(1)
                    .rarity(Rarity.EPIC)
                    .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)));
}