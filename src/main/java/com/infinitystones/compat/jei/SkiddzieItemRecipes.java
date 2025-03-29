package com.infinitystones.compat.jei;

import com.infinitystones.items.skiddzie.SkiddzieItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides recipes for Skiddzie Items JEI integration
 */
public class SkiddzieItemRecipes {
    /**
     * Get all Skiddzie Item crafting recipes for JEI
     */
    public static List<InfinityStoneRecipe> getRecipes() {
        List<InfinityStoneRecipe> recipes = new ArrayList<>();
        
        // Rowan Lucky Block
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.GOLD_BLOCK)
                .addIngredient(Items.LAPIS_BLOCK)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.REDSTONE_BLOCK)
                .setResult(SkiddzieItems.ROWAN_LUCKY_BLOCK.get(), 1)
                .build());
        
        // Skiddzie Base Locator Wand
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.STICK)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.COMPASS)
                .addIngredient(Items.MAP)
                .setResult(SkiddzieItems.SKIDDZIE_BASE_LOCATOR.get(), 1)
                .build());
        
        // Skeppy Arena Locator
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.STICK)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.COMPASS)
                .addIngredient(Items.DRAGON_BREATH)
                .setResult(SkiddzieItems.SKEPPY_ARENA_LOCATOR.get(), 1)
                .build());
        
        // Admin Command Block
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.REDSTONE_BLOCK)
                .addIngredient(Items.COMMAND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.DIAMOND_BLOCK)
                .setResult(SkiddzieItems.ADMIN_COMMAND_BLOCK.get(), 1)
                .build());
        
        // One Way Block
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.GLASS)
                .addIngredient(Items.REDSTONE)
                .addIngredient(Items.OBSERVER)
                .addIngredient(Items.QUARTZ)
                .setResult(SkiddzieItems.ONE_WAY_BLOCK.get(), 4)
                .build());
        
        // TNT Trap Block
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.TNT)
                .addIngredient(Items.STONE)
                .addIngredient(Items.REDSTONE)
                .addIngredient(Items.TRIPWIRE_HOOK)
                .setResult(SkiddzieItems.TNT_TRAP_BLOCK.get(), 1)
                .build());
        
        // Arrow Trap Block
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DISPENSER)
                .addIngredient(Items.ARROW)
                .addIngredient(Items.REDSTONE)
                .addIngredient(Items.STONE)
                .setResult(SkiddzieItems.ARROW_TRAP_BLOCK.get(), 1)
                .build());
        
        // Pitfall Trap Block
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.GRASS_BLOCK)
                .addIngredient(Items.PISTON)
                .addIngredient(Items.REDSTONE)
                .addIngredient(Items.TRIPWIRE_HOOK)
                .setResult(SkiddzieItems.PITFALL_TRAP_BLOCK.get(), 1)
                .build());
        
        return recipes;
    }
}