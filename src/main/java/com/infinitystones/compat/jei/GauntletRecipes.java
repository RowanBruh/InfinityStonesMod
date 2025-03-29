package com.infinitystones.compat.jei;

import com.infinitystones.items.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides recipes for Gauntlet crafting JEI integration
 */
public class GauntletRecipes {
    /**
     * Get all Gauntlet crafting recipes for JEI
     */
    public static List<InfinityStoneRecipe> getRecipes() {
        List<InfinityStoneRecipe> recipes = new ArrayList<>();
        
        // Add Infinity Gauntlet recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.GOLD_INGOT)
                .addIngredient(Items.GOLDEN_APPLE)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.GOLD_INGOT)
                .setResult(ModItems.INFINITY_GAUNTLET.get(), 1)
                .build());
        
        // Add Advanced Infinity Gauntlet recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(ModItems.INFINITY_GAUNTLET.get())
                .addIngredient(ModItems.SPACE_STONE.get())
                .addIngredient(ModItems.MIND_STONE.get())
                .addIngredient(ModItems.REALITY_STONE.get())
                .addIngredient(ModItems.POWER_STONE.get())
                .addIngredient(ModItems.TIME_STONE.get())
                .addIngredient(ModItems.SOUL_STONE.get())
                .addIngredient(Items.NETHER_STAR)
                .setResult(ModItems.ADVANCED_INFINITY_GAUNTLET.get(), 1)
                .build());
        
        // Add Infected Infinity Gauntlet recipe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(ModItems.INFINITY_GAUNTLET.get())
                .addIngredient(ModItems.INFECTED_SPACE_STONE.get())
                .addIngredient(ModItems.INFECTED_MIND_STONE.get())
                .addIngredient(ModItems.INFECTED_REALITY_STONE.get())
                .addIngredient(ModItems.INFECTED_POWER_STONE.get())
                .addIngredient(ModItems.INFECTED_TIME_STONE.get())
                .addIngredient(ModItems.INFECTED_SOUL_STONE.get())
                .addIngredient(Items.NETHER_STAR)
                .setResult(ModItems.INFECTED_INFINITY_GAUNTLET.get(), 1)
                .build());
        
        return recipes;
    }
}