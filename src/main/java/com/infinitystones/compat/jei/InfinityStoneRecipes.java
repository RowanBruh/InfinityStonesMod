package com.infinitystones.compat.jei;

import com.infinitystones.items.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides recipes for Infinity Stones JEI integration
 */
public class InfinityStoneRecipes {
    /**
     * Get all Infinity Stone recipes for JEI
     */
    public static List<InfinityStoneRecipe> getRecipes() {
        List<InfinityStoneRecipe> recipes = new ArrayList<>();
        
        // Add Space Stone recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(Items.ENDER_EYE)
                .addIngredient(Items.LAPIS_BLOCK)
                .setResult(ModItems.SPACE_STONE.get(), 1)
                .build());
        
        // Add Mind Stone recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(Items.GOLDEN_APPLE)
                .addIngredient(Items.YELLOW_DYE)
                .setResult(ModItems.MIND_STONE.get(), 1)
                .build());
        
        // Add Reality Stone recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(Items.REDSTONE_BLOCK)
                .addIngredient(Items.RED_DYE)
                .setResult(ModItems.REALITY_STONE.get(), 1)
                .build());
        
        // Add Power Stone recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.PURPLE_DYE)
                .setResult(ModItems.POWER_STONE.get(), 1)
                .build());
        
        // Add Time Stone recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(Items.CLOCK)
                .addIngredient(Items.GREEN_DYE)
                .setResult(ModItems.TIME_STONE.get(), 1)
                .build());
        
        // Add Soul Stone recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(Items.TOTEM_OF_UNDYING)
                .addIngredient(Items.ORANGE_DYE)
                .setResult(ModItems.SOUL_STONE.get(), 1)
                .build());
        
        return recipes;
    }
}