package com.infinitystones.compat.jei;

import com.infinitystones.items.gods.GreekGodItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides recipes for Greek God Items JEI integration
 */
public class GreekGodRecipes {
    /**
     * Get all Greek God crafting recipes for JEI
     */
    public static List<InfinityStoneRecipe> getRecipes() {
        List<InfinityStoneRecipe> recipes = new ArrayList<>();
        
        // Zeus's Lightning Bolt
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.BLAZE_ROD)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.LIGHTNING_ROD)
                .setResult(GreekGodItems.ZEUS_LIGHTNING_BOLT.get(), 1)
                .build());
        
        // Poseidon's Trident
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.TRIDENT)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.HEART_OF_THE_SEA)
                .setResult(GreekGodItems.POSEIDON_TRIDENT.get(), 1)
                .build());
        
        // Hades's Helmet
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_HELMET)
                .addIngredient(Items.WITHER_SKELETON_SKULL)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.NETHER_STAR)
                .setResult(GreekGodItems.HADES_HELMET.get(), 1)
                .build());
        
        // Athena's Shield
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.SHIELD)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.ENCHANTED_BOOK)
                .setResult(GreekGodItems.ATHENA_SHIELD.get(), 1)
                .build());
        
        // Apollo's Bow
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.BOW)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.GLOWSTONE)
                .setResult(GreekGodItems.APOLLO_BOW.get(), 1)
                .build());
        
        // Hermes's Boots
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_BOOTS)
                .addIngredient(Items.FEATHER)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.RABBIT_FOOT)
                .setResult(GreekGodItems.HERMES_BOOTS.get(), 1)
                .build());
        
        // Artemis's Bow
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_INGOT)
                .addIngredient(Items.BOW)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.SWEET_BERRIES)
                .setResult(GreekGodItems.ARTEMIS_BOW.get(), 1)
                .build());
        
        // Ares's Sword
        recipes.add(new InfinityStoneRecipe.Builder()
                .addIngredient(Items.NETHERITE_SWORD)
                .addIngredient(Items.BLAZE_POWDER)
                .addIngredient(Items.DIAMOND)
                .addIngredient(Items.TNT)
                .setResult(GreekGodItems.ARES_SWORD.get(), 1)
                .build());
        
        return recipes;
    }
}