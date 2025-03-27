package com.infinitystones.events;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.InfinityArmorItem;
import com.infinitystones.items.InsaneCraftWeapons;
import com.infinitystones.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles events related to Infinity Stones and Insane Craft items
 */
@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID)
public class StoneEvents {
    
    /**
     * Handles effects for wearing Infinity Armor
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            PlayerEntity player = event.player;
            
            // Apply effects for each piece of Infinity Armor
            for (ItemStack stack : player.getArmorInventoryList()) {
                if (stack.getItem() instanceof InfinityArmorItem) {
                    InsaneCraftWeapons.applyInfinityArmorEffect(player, stack);
                }
            }
        }
    }
    
    /**
     * Handles damage modifiers for special items
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // If the damage source is a player
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            ItemStack heldItem = player.getHeldItemMainhand();
            
            // Check if the player is using an Insane Craft weapon
            if (heldItem.getItem() == ModItems.ROYAL_GUARDIAN_SWORD.get()) {
                // Increase damage for Royal Guardian Sword
                event.setAmount(event.getAmount() * 1.5f);
            } else if (heldItem.getItem() == ModItems.THOR_HAMMER.get()) {
                // Increase damage for Thor's Hammer and add lightning effect
                event.setAmount(event.getAmount() * 2.0f);
            }
        }
    }
    
    /**
     * Handles damage reduction for Infinity Armor
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            
            // Count how many pieces of Infinity Armor are worn
            int piecesWorn = 0;
            for (ItemStack armorStack : player.getArmorInventoryList()) {
                if (!armorStack.isEmpty() && armorStack.getItem() instanceof InfinityArmorItem) {
                    piecesWorn++;
                }
            }
            
            // Apply damage reduction based on number of pieces
            if (piecesWorn > 0) {
                // Reduce damage by 10% per piece
                float damageReduction = 0.1f * piecesWorn;
                if (piecesWorn == 4) {
                    // Complete set bonus: additional 20% reduction
                    damageReduction += 0.2f;
                }
                
                // Apply the reduction (cap at 90%)
                damageReduction = Math.min(damageReduction, 0.9f);
                event.setAmount(event.getAmount() * (1 - damageReduction));
            }
        }
    }
}