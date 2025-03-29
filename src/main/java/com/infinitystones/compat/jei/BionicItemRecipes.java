package com.infinitystones.compat.jei;

import com.infinitystones.items.bionic.BionicItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides recipes for Bionic Items JEI integration
 */
public class BionicItemRecipes {
    /**
     * Get all Bionic Item crafting recipes for JEI
     */
    public static List<InfinityStoneRecipe> getRecipes() {
        List<InfinityStoneRecipe> recipes = new ArrayList<>();
        
        // Bionic Ultra Pickaxe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_PICKAXE)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.EXPERIENCE_BOTTLE)
                .setResult(BionicItems.BIONIC_ULTRA_PICKAXE.get(), 1)
                .build());
        
        // Bionic Ultra Sword
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_SWORD)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.BLAZE_POWDER)
                .setResult(BionicItems.BIONIC_ULTRA_SWORD.get(), 1)
                .build());
        
        // Bionic Ultra Axe
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_AXE)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.OAK_LOG)
                .setResult(BionicItems.BIONIC_ULTRA_AXE.get(), 1)
                .build());
        
        // Bionic Ultra Helmet
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_HELMET)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.SCUTE)
                .setResult(BionicItems.BIONIC_ULTRA_HELMET.get(), 1)
                .build());
        
        // Bionic Ultra Chestplate
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_CHESTPLATE)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.GOLDEN_APPLE)
                .setResult(BionicItems.BIONIC_ULTRA_CHESTPLATE.get(), 1)
                .build());
        
        // Bionic Ultra Leggings
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_LEGGINGS)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.RABBIT_FOOT)
                .setResult(BionicItems.BIONIC_ULTRA_LEGGINGS.get(), 1)
                .build());
        
        // Bionic Ultra Boots
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_BOOTS)
                .addIngredient(Items.DIAMOND_BLOCK)
                .addIngredient(Items.NETHER_STAR)
                .addIngredient(Items.FEATHER)
                .setResult(BionicItems.BIONIC_ULTRA_BOOTS.get(), 1)
                .build());
        
        // Bionic Base Locator Wand
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.STICK)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.COMPASS)
                .addIngredient(Items.MAP)
                .setResult(BionicItems.BIONIC_BASE_LOCATOR.get(), 1)
                .build());
        
        return recipes;
    }
}