package com.infinitystones.data;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

/**
 * Handles the registration and generation of mod recipes
 */
public class ModRecipes extends RecipeProvider {

    public ModRecipes(DataGenerator generatorIn) {
        super(generatorIn);
    }
    
    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        // Crafting recipes for Infinity Stones (in a real mod, these would be much more complex)
        // Space Stone
        ShapedRecipeBuilder.shapedRecipe(ModItems.SPACE_STONE.get())
                .patternLine("DSD")
                .patternLine("SES")
                .patternLine("DSD")
                .key('D', Items.DIAMOND)
                .key('S', Items.NETHER_STAR)
                .key('E', Items.ENDER_EYE)
                .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "space_stone"));
        
        // Mind Stone
        ShapedRecipeBuilder.shapedRecipe(ModItems.MIND_STONE.get())
                .patternLine("GBG")
                .patternLine("BNB")
                .patternLine("GBG")
                .key('G', Items.GOLD_BLOCK)
                .key('B', Items.BLAZE_ROD)
                .key('N', Items.NETHER_STAR)
                .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "mind_stone"));
        
        // Reality Stone
        ShapedRecipeBuilder.shapedRecipe(ModItems.REALITY_STONE.get())
                .patternLine("RNR")
                .patternLine("NMN")
                .patternLine("RNR")
                .key('R', Items.REDSTONE_BLOCK)
                .key('N', Items.NETHER_STAR)
                .key('M', Items.MAGMA_BLOCK)
                .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "reality_stone"));
        
        // Power Stone
        ShapedRecipeBuilder.shapedRecipe(ModItems.POWER_STONE.get())
                .patternLine("OPO")
                .patternLine("PNP")
                .patternLine("OPO")
                .key('O', Items.OBSIDIAN)
                .key('P', Items.END_CRYSTAL)
                .key('N', Items.NETHER_STAR)
                .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "power_stone"));
        
        // Time Stone
        ShapedRecipeBuilder.shapedRecipe(ModItems.TIME_STONE.get())
                .patternLine("ECE")
                .patternLine("CNE")
                .patternLine("ECE")
                .key('E', Items.EMERALD_BLOCK)
                .key('C', Items.CLOCK)
                .key('N', Items.NETHER_STAR)
                .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "time_stone"));
        
        // Soul Stone
        ShapedRecipeBuilder.shapedRecipe(ModItems.SOUL_STONE.get())
                .patternLine("GSG")
                .patternLine("SNS")
                .patternLine("GSG")
                .key('G', Items.GOLD_BLOCK)
                .key('S', Items.SOUL_SOIL)
                .key('N', Items.NETHER_STAR)
                .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "soul_stone"));
        
        // Infinity Gauntlet
        ShapedRecipeBuilder.shapedRecipe(ModItems.INFINITY_GAUNTLET.get())
                .patternLine("NNN")
                .patternLine("NGN")
                .patternLine("  N")
                .key('N', Items.NETHERITE_INGOT)
                .key('G', Items.GOLD_BLOCK)
                .addCriterion("has_netherite", hasItem(Items.NETHERITE_INGOT))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_gauntlet"));
                
        // Insane Craft Base Components
        
        // Ultimate Ingot
        ShapedRecipeBuilder.shapedRecipe(ModItems.ULTIMATE_INGOT.get())
                .patternLine("IDG")
                .patternLine("ENR")
                .patternLine("LCE")
                .key('I', Items.IRON_BLOCK)
                .key('D', Items.DIAMOND_BLOCK)
                .key('G', Items.GOLD_BLOCK)
                .key('E', Items.EMERALD_BLOCK)
                .key('N', Items.NETHERITE_BLOCK)
                .key('R', Items.REDSTONE_BLOCK)
                .key('L', Items.LAPIS_BLOCK)
                .key('C', Items.COAL_BLOCK)
                .addCriterion("has_netherite_block", hasItem(Items.NETHERITE_BLOCK))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "ultimate_ingot"));
        
        // Chaos Shard
        ShapedRecipeBuilder.shapedRecipe(ModItems.CHAOS_SHARD.get())
                .patternLine("OEO")
                .patternLine("ENE")
                .patternLine("OEO")
                .key('O', Items.OBSIDIAN)
                .key('E', Items.END_CRYSTAL)
                .key('N', Items.NETHER_STAR)
                .addCriterion("has_end_crystal", hasItem(Items.END_CRYSTAL))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "chaos_shard"));
        
        // Cosmic Dust
        ShapedRecipeBuilder.shapedRecipe(ModItems.COSMIC_DUST.get(), 3)
                .patternLine("GDG")
                .patternLine("DED")
                .patternLine("GDG")
                .key('G', Items.GLOWSTONE_DUST)
                .key('D', Items.DIAMOND)
                .key('E', Items.ENDER_PEARL)
                .addCriterion("has_diamond", hasItem(Items.DIAMOND))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "cosmic_dust"));
                
        // Special crafting components
        
        // Dragon Scale
        ShapedRecipeBuilder.shapedRecipe(ModItems.DRAGON_SCALE.get(), 2)
                .patternLine("OEO")
                .patternLine("EDE")
                .patternLine("OEO")
                .key('O', Items.OBSIDIAN)
                .key('E', Items.ENDER_EYE)
                .key('D', Items.DRAGON_HEAD)
                .addCriterion("has_dragon_head", hasItem(Items.DRAGON_HEAD))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "dragon_scale"));
        
        // Withered Bone
        ShapedRecipeBuilder.shapedRecipe(ModItems.WITHERED_BONE.get(), 3)
                .patternLine("SSS")
                .patternLine("SWS")
                .patternLine("SSS")
                .key('S', Items.WITHER_SKELETON_SKULL)
                .key('W', Items.BONE_BLOCK)
                .addCriterion("has_wither_skull", hasItem(Items.WITHER_SKELETON_SKULL))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "withered_bone"));
        
        // Cosmic Pearl
        ShapedRecipeBuilder.shapedRecipe(ModItems.COSMIC_PEARL.get())
                .patternLine("CDC")
                .patternLine("DED")
                .patternLine("CDC")
                .key('C', ModItems.COSMIC_DUST.get())
                .key('D', Items.DIAMOND)
                .key('E', Items.ENDER_EYE)
                .addCriterion("has_cosmic_dust", hasItem(ModItems.COSMIC_DUST.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "cosmic_pearl"));
        
        // Enchanted Metal
        ShapedRecipeBuilder.shapedRecipe(ModItems.ENCHANTED_METAL.get())
                .patternLine("NEN")
                .patternLine("ECE")
                .patternLine("NEN")
                .key('N', Items.NETHERITE_INGOT)
                .key('E', Items.ENCHANTED_GOLDEN_APPLE)
                .key('C', Items.END_CRYSTAL)
                .addCriterion("has_enchanted_apple", hasItem(Items.ENCHANTED_GOLDEN_APPLE))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "enchanted_metal"));
                
        // Insane Craft Weapons
        
        // Royal Guardian Sword
        ShapedRecipeBuilder.shapedRecipe(ModItems.ROYAL_GUARDIAN_SWORD.get())
                .patternLine(" U ")
                .patternLine(" U ")
                .patternLine(" S ")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .key('S', Items.NETHER_STAR)
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "royal_guardian_sword"));
        
        // Ultimate Bow
        ShapedRecipeBuilder.shapedRecipe(ModItems.ULTIMATE_BOW.get())
                .patternLine("UCU")
                .patternLine("U B")
                .patternLine("UCU")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .key('C', ModItems.COSMIC_PEARL.get())
                .key('B', Items.BOW)
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "ultimate_bow"));
        
        // Thor's Hammer
        ShapedRecipeBuilder.shapedRecipe(ModItems.THOR_HAMMER.get())
                .patternLine("UUU")
                .patternLine("USU")
                .patternLine(" S ")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .key('S', Items.NETHER_STAR)
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "thor_hammer"));
                
        // Infinity Armor
        
        // Infinity Helmet
        ShapedRecipeBuilder.shapedRecipe(ModItems.INFINITY_HELMET.get())
                .patternLine("UUU")
                .patternLine("U U")
                .patternLine("   ")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_helmet"));
        
        // Infinity Chestplate
        ShapedRecipeBuilder.shapedRecipe(ModItems.INFINITY_CHESTPLATE.get())
                .patternLine("U U")
                .patternLine("UUU")
                .patternLine("UUU")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_chestplate"));
        
        // Infinity Leggings
        ShapedRecipeBuilder.shapedRecipe(ModItems.INFINITY_LEGGINGS.get())
                .patternLine("UUU")
                .patternLine("U U")
                .patternLine("U U")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_leggings"));
        
        // Infinity Boots
        ShapedRecipeBuilder.shapedRecipe(ModItems.INFINITY_BOOTS.get())
                .patternLine("   ")
                .patternLine("U U")
                .patternLine("U U")
                .key('U', ModItems.ULTIMATE_INGOT.get())
                .addCriterion("has_ultimate_ingot", hasItem(ModItems.ULTIMATE_INGOT.get()))
                .build(consumer, new ResourceLocation(InfinityStonesMod.MOD_ID, "infinity_boots"));
    }
}