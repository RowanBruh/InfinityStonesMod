package com.infinitystones.compat.jei;

import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe class for Infinity Stones JEI integration
 */
public class InfinityStoneRecipe implements IRecipeCategoryExtension {
    private final List<Ingredient> ingredients;
    private final ItemStack result;
    
    public InfinityStoneRecipe(List<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }
    
    /**
     * Get the ingredients for this recipe
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    
    /**
     * Get the result item stack
     */
    public ItemStack getResultItem() {
        return result;
    }
    
    /**
     * Builder for creating recipes more easily
     */
    public static class Builder {
        private final List<Ingredient> ingredients = new ArrayList<>();
        private ItemStack result = ItemStack.EMPTY;
        
        /**
         * Add an ingredient to the recipe
         */
        public Builder addIngredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }
        
        /**
         * Add an item ingredient to the recipe
         */
        public Builder addIngredient(Item item) {
            return addIngredient(Ingredient.of(item));
        }
        
        /**
         * Add an item ingredient to the recipe
         */
        public Builder addIngredient(RegistryObject<Item> item) {
            return addIngredient(item.get());
        }
        
        /**
         * Set the result of the recipe
         */
        public Builder setResult(ItemStack result) {
            this.result = result;
            return this;
        }
        
        /**
         * Set the result of the recipe
         */
        public Builder setResult(Item result, int count) {
            return setResult(new ItemStack(result, count));
        }
        
        /**
         * Set the result of the recipe
         */
        public Builder setResult(RegistryObject<Item> result, int count) {
            return setResult(result.get(), count);
        }
        
        /**
         * Build the recipe
         */
        public InfinityStoneRecipe build() {
            return new InfinityStoneRecipe(ingredients, result);
        }
    }
}