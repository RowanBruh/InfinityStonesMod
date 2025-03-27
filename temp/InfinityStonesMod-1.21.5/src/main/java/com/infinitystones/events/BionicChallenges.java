package com.infinitystones.events;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.bionic.BionicItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID)
public class BionicChallenges {

    // Map to store active challenges for each player
    private static final Map<UUID, ActiveChallenge> activeChallenges = new HashMap<>();
    
    // Inner class to track challenge details
    private static class ActiveChallenge {
        public final ChallengeType type;
        public int timeRemaining; // in ticks
        public boolean completed;
        
        public ActiveChallenge(ChallengeType type, int duration) {
            this.type = type;
            this.timeRemaining = duration;
            this.completed = false;
        }
    }
    
    // Different challenge types
    public enum ChallengeType {
        HUNDRED_DAYS("Survive 100 Minecraft days", 24000 * 100),
        SPEEDRUN("Complete the game in 30 minutes", 20 * 60 * 30),
        COLLECT_ALL_STONES("Collect all Infinity Stones", 20 * 60 * 60),
        NO_JUMPING("Play without jumping for 10 minutes", 20 * 60 * 10),
        SKYBLOCK("Survive on a floating island", 24000 * 5);
        
        public final String description;
        public final int defaultDuration; // in ticks
        
        ChallengeType(String description, int defaultDuration) {
            this.description = description;
            this.defaultDuration = defaultDuration;
        }
    }
    
    // Start a challenge for a player
    public static void startChallenge(PlayerEntity player, ChallengeType type) {
        activeChallenges.put(player.getUniqueID(), new ActiveChallenge(type, type.defaultDuration));
        player.sendMessage(new StringTextComponent("Bionic Challenge Started: " + type.description), player.getUniqueID());
    }
    
    // Handle player ticks to update challenge timers and check conditions
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            PlayerEntity player = event.player;
            UUID playerID = player.getUniqueID();
            
            if (activeChallenges.containsKey(playerID)) {
                ActiveChallenge challenge = activeChallenges.get(playerID);
                
                // Update timer
                challenge.timeRemaining--;
                
                // Periodic updates to player
                if (challenge.timeRemaining % 1200 == 0) { // Every minute
                    int minutesLeft = challenge.timeRemaining / 1200;
                    player.sendMessage(new StringTextComponent(
                            "Challenge time remaining: " + minutesLeft + " minutes"), playerID);
                }
                
                // Check for completion
                if (challenge.timeRemaining <= 0 && !challenge.completed) {
                    completeChallenge(player, challenge.type, true);
                }
                
                // Check for specific challenge conditions
                checkChallengeConditions(player, challenge);
            }
        }
    }
    
    // Check specific conditions for different challenge types
    private static void checkChallengeConditions(PlayerEntity player, ActiveChallenge challenge) {
        World world = player.world;
        
        switch (challenge.type) {
            case COLLECT_ALL_STONES:
                // Check if player has all infinity stones in inventory
                boolean hasAllStones = true;
                // Implementation would check for all infinity stones
                
                if (hasAllStones && !challenge.completed) {
                    completeChallenge(player, challenge.type, true);
                }
                break;
                
            case SPEEDRUN:
                // Check if player defeated Ender Dragon
                if (player.world.getServer() != null && 
                    player.world.getServer().getWorld(World.THE_END) != null) {
                    if (player.world.getServer().getWorld(World.THE_END).getDragonFightManager() != null &&
                        player.world.getServer().getWorld(World.THE_END).getDragonFightManager().hasPreviouslyKilledDragon() &&
                        !challenge.completed) {
                        completeChallenge(player, challenge.type, true);
                    }
                }
                break;
                
            case NO_JUMPING:
                // Check if player jumped
                if (player.isJumping && !challenge.completed) {
                    completeChallenge(player, challenge.type, false);
                    player.sendMessage(new StringTextComponent("Challenge failed: You jumped!"), player.getUniqueID());
                }
                break;
        }
    }
    
    // Handle challenge completion
    private static void completeChallenge(PlayerEntity player, ChallengeType type, boolean success) {
        UUID playerID = player.getUniqueID();
        if (activeChallenges.containsKey(playerID)) {
            ActiveChallenge challenge = activeChallenges.get(playerID);
            challenge.completed = true;
            
            if (success) {
                player.sendMessage(new StringTextComponent("Challenge completed: " + type.description), playerID);
                
                // Give rewards
                switch (type) {
                    case HUNDRED_DAYS:
                        player.addItemStackToInventory(new ItemStack(BionicItems.HUNDRED_DAYS_SWORD));
                        break;
                    case SPEEDRUN:
                        player.addItemStackToInventory(new ItemStack(BionicItems.TELEPORT_PEARL, 64));
                        break;
                    case COLLECT_ALL_STONES:
                        player.addItemStackToInventory(new ItemStack(BionicItems.BIONIC_LUCKY_BLOCK, 10));
                        break;
                    case NO_JUMPING:
                        player.addItemStackToInventory(new ItemStack(BionicItems.LIGHTNING_TRIDENT));
                        break;
                    case SKYBLOCK:
                        player.addItemStackToInventory(new ItemStack(BionicItems.GIANT_PICKAXE));
                        break;
                }
            } else {
                player.sendMessage(new StringTextComponent("Challenge failed: " + type.description), playerID);
            }
            
            // Remove the challenge
            activeChallenges.remove(playerID);
        }
    }
    
    // Handle player login to provide challenge info
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        
        player.sendMessage(new StringTextComponent("Welcome! Use the Bionic Challenge Book to start a challenge!"), player.getUniqueID());
    }
}