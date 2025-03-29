package com.infinitystones.compat.jei;

import com.infinitystones.InfinityStonesMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI Plugin for the Infinity Stones Mod
 * Registers all the recipe categories and recipes
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "jei_plugin");
    
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        
        // Register recipe categories
        registration.addRecipeCategories(
                new InfinityStoneRecipeCategory(guiHelper),
                new GauntletCraftingCategory(guiHelper),
                new GreekGodItemCategory(guiHelper),
                new BionicItemCategory(guiHelper),
                new SkiddzieItemCategory(guiHelper)
        );
    }
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Register recipes for Infinity Stones
        registration.addRecipes(
                InfinityStoneRecipeCategory.RECIPE_TYPE,
                InfinityStoneRecipes.getRecipes()
        );
        
        // Register recipes for Gauntlet crafting
        registration.addRecipes(
                GauntletCraftingCategory.RECIPE_TYPE,
                GauntletRecipes.getRecipes()
        );
        
        // Register recipes for Greek God Items
        registration.addRecipes(
                GreekGodItemCategory.RECIPE_TYPE,
                GreekGodRecipes.getRecipes()
        );
        
        // Register recipes for Bionic Items
        registration.addRecipes(
                BionicItemCategory.RECIPE_TYPE,
                BionicItemRecipes.getRecipes()
        );
        
        // Register recipes for Skiddzie Items
        registration.addRecipes(
                SkiddzieItemCategory.RECIPE_TYPE,
                SkiddzieItemRecipes.getRecipes()
        );
    }
}