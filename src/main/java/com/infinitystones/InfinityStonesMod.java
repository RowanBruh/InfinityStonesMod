package com.infinitystones;

import com.infinitystones.blocks.ModBlocks;
import com.infinitystones.blocks.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(InfinityStonesMod.MOD_ID)
public class InfinityStonesMod {
    public static final String MOD_ID = "infinitystones";
    
    // Creative mode tabs
    public static final ItemGroup INFINITY_STONES_TAB = new ItemGroup("infinitystones.infinity_stones") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.DIAMOND); // Placeholder, replace with actual infinity stone item
        }
    };
    
    public static final ItemGroup ROWAN_INDUSTRIES_TAB = new ItemGroup("infinitystones.rowan_industries") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.REDSTONE); // Placeholder for Rowan Industries logo
        }
    };
    
    public static final ItemGroup BONCS_ITEMS_TAB = new ItemGroup("infinitystones.boncs_items") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.GOLDEN_APPLE); // Placeholder for Bonc's items
        }
    };
    
    // Custom damage source for infinity stone related damage
    public static final DamageSource INFINITY_DAMAGE = new DamageSource(MOD_ID + ".infinity").setMagicDamage();
    
    public InfinityStonesMod() {
        // Get the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register DeferredRegisters to the mod event bus
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModTileEntities.TILE_ENTITIES.register(modEventBus);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
    }
}