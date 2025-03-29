package com.infinitystones.compat.jei;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.gods.GreekGodItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI Recipe Category for Greek God Items crafting
 */
public class GreekGodItemCategory implements IRecipeCategory<InfinityStoneRecipe> {
    public static final RecipeType<InfinityStoneRecipe> RECIPE_TYPE = 
            RecipeType.create(InfinityStonesMod.MOD_ID, "greek_god_items", InfinityStoneRecipe.class);
    
    private static final ResourceLocation BACKGROUND_TEXTURE = 
            new ResourceLocation(InfinityStonesMod.MOD_ID, "textures/gui/jei/greek_god_items.png");
    
    private final IDrawable background;
    private final IDrawable icon;
    
    public GreekGodItemCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 176, 85);
        this.icon = guiHelper.createDrawableIngredient(
                RecipeIngredientRole.RENDER_ONLY, 
                new ItemStack(GreekGodItems.ZEUS_LIGHTNING_BOLT.get())
        );
    }
    
    @Override
    public RecipeType<InfinityStoneRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("infinitystones.jei.category.greek_god_items");
    }
    
    @Override
    public IDrawable getBackground() {
        return background;
    }
    
    @Override
    public IDrawable getIcon() {
        return icon;
    }
    
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfinityStoneRecipe recipe, IFocusGroup focuses) {
        // Add ingredients to the layout
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            int x = 18 + (i % 3) * 18;
            int y = 18 + (i / 3) * 18;
            
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addIngredients(recipe.getIngredients().get(i));
        }
        
        // Add output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 36)
                .addItemStack(recipe.getResultItem());
    }
}