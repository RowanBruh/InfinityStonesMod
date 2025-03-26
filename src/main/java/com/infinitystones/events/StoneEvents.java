package com.infinitystones.events;

import java.util.Random;

import com.infinitystones.config.ModConfig;
import com.infinitystones.items.InfinityStones;
import com.infinitystones.items.ModItems;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class StoneEvents {
    
    private static final Random random = new Random();
    
    // Power stone damage amplification
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            
            // Check if player is wielding Power Stone
            if (isHoldingStone(player, ModItems.POWER_STONE.get())) {
                double multiplier = ModConfig.COMMON_CONFIG.powerStoneDamageMultiplier.get();
                event.setAmount(event.getAmount() * (float) multiplier);
                
                // Visual effect for power stone damage
                if (!player.world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Power Stone amplifies damage!").mergeStyle(TextFormatting.DARK_PURPLE),
                            player.getUniqueID());
                }
            }
            
            // Check if player has full Infinity Gauntlet
            if (hasFullGauntlet(player)) {
                // Even more damage bonus with full gauntlet
                event.setAmount(event.getAmount() * 2.0f);
            }
        }
    }
    
    // Insane Craft Bosses drop infinity stones
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.getEntity().world.isRemote && ModConfig.COMMON_CONFIG.enableBossDrops.get()) {
            LivingEntity entity = event.getEntityLiving();
            
            if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                // Drop stones based on enemy type
                if (entity instanceof WitherEntity) {
                    // Wither has a chance to drop Power Stone
                    if (random.nextFloat() < 0.3f) {
                        entity.entityDropItem(new ItemStack(ModItems.POWER_STONE.get()));
                    }
                } else if (entity instanceof ElderGuardianEntity) {
                    // Elder Guardian has a chance to drop Mind Stone
                    if (random.nextFloat() < 0.3f) {
                        entity.entityDropItem(new ItemStack(ModItems.MIND_STONE.get()));
                    }
                } else if (entity instanceof WitherSkeletonEntity) {
                    // Wither Skeleton has a small chance to drop Soul Stone
                    if (random.nextFloat() < 0.05f) {
                        entity.entityDropItem(new ItemStack(ModItems.SOUL_STONE.get()));
                    }
                } else if (entity instanceof BlazeEntity) {
                    // Blaze has a small chance to drop Reality Stone
                    if (random.nextFloat() < 0.05f) {
                        entity.entityDropItem(new ItemStack(ModItems.REALITY_STONE.get()));
                    }
                } else if (entity instanceof GuardianEntity) {
                    // Guardian has a small chance to drop Time Stone
                    if (random.nextFloat() < 0.05f) {
                        entity.entityDropItem(new ItemStack(ModItems.TIME_STONE.get()));
                    }
                } else if (entity instanceof ZombifiedPiglinEntity) {
                    // Zombified Piglin has a tiny chance to drop Space Stone
                    if (random.nextFloat() < 0.01f) {
                        entity.entityDropItem(new ItemStack(ModItems.SPACE_STONE.get()));
                    }
                }
                
                // For Insane Craft special bosses (named entities)
                String entityName = entity.getName().getString();
                if (ModConfig.COMMON_CONFIG.bossNames.get().contains(entityName)) {
                    // 50% chance to drop a random stone
                    if (random.nextFloat() < 0.5f) {
                        int stoneIndex = random.nextInt(6);
                        ItemStack stoneToDrop = null;
                        
                        switch (stoneIndex) {
                            case 0:
                                stoneToDrop = new ItemStack(ModItems.SPACE_STONE.get());
                                break;
                            case 1:
                                stoneToDrop = new ItemStack(ModItems.MIND_STONE.get());
                                break;
                            case 2:
                                stoneToDrop = new ItemStack(ModItems.REALITY_STONE.get());
                                break;
                            case 3:
                                stoneToDrop = new ItemStack(ModItems.POWER_STONE.get());
                                break;
                            case 4:
                                stoneToDrop = new ItemStack(ModItems.TIME_STONE.get());
                                break;
                            case 5:
                                stoneToDrop = new ItemStack(ModItems.SOUL_STONE.get());
                                break;
                        }
                        
                        if (stoneToDrop != null) {
                            entity.entityDropItem(stoneToDrop);
                        }
                    }
                    
                    // Also drop Insane Craft components
                    if (random.nextFloat() < 0.3f) {
                        entity.entityDropItem(new ItemStack(ModItems.ULTIMATE_INGOT.get()));
                    }
                    
                    if (random.nextFloat() < 0.2f) {
                        entity.entityDropItem(new ItemStack(ModItems.CHAOS_SHARD.get()));
                    }
                }
            }
        }
    }
    
    // Soul Stone requires sacrifice
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            LivingEntity victim = event.getEntityLiving();
            
            // If player has the Soul Stone, add a small healing effect
            if (isHoldingStone(player, ModItems.SOUL_STONE.get())) {
                float healAmount = (float) ModConfig.COMMON_CONFIG.soulStoneLifeStealAmount.get();
                player.heal(healAmount);
                
                if (!player.world.isRemote) {
                    player.sendMessage(
                            new StringTextComponent("Soul Stone absorbs life essence!").mergeStyle(TextFormatting.GOLD),
                            player.getUniqueID());
                }
            }
            
            // Special case for zombies and the Soul Stone
            if (victim instanceof ZombieEntity && !player.inventory.hasItemStack(new ItemStack(ModItems.SOUL_STONE.get()))) {
                // There's a very small chance to get the Soul Stone from killing a zombie
                if (random.nextFloat() < 0.001f) {
                    victim.entityDropItem(new ItemStack(ModItems.SOUL_STONE.get()));
                    
                    if (!player.world.isRemote) {
                        player.sendMessage(
                                new StringTextComponent("A zombie's soul reveals the Soul Stone!").mergeStyle(TextFormatting.GOLD),
                                player.getUniqueID());
                    }
                }
            }
        }
    }
    
    // Attribute modifications for stone holders
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (ModConfig.COMMON_CONFIG.enableInsaneCraftIntegration.get()) {
            PlayerEntity player = event.getPlayer();
            
            if (!player.world.isRemote) {
                player.sendMessage(
                        new StringTextComponent("The power of the Infinity Stones awaits...").mergeStyle(TextFormatting.LIGHT_PURPLE),
                        player.getUniqueID());
            }
        }
    }
    
    private boolean isHoldingStone(PlayerEntity player, InfinityStones.InfinityStoneItem stone) {
        return player.getHeldItemMainhand().getItem() == stone || 
               player.getHeldItemOffhand().getItem() == stone;
    }
    
    private boolean isHoldingStone(PlayerEntity player, net.minecraft.item.Item stone) {
        return player.getHeldItemMainhand().getItem() == stone || 
               player.getHeldItemOffhand().getItem() == stone;
    }
    
    private boolean hasFullGauntlet(PlayerEntity player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        
        if (mainHand.getItem() == ModItems.INFINITY_GAUNTLET.get()) {
            return ((com.infinitystones.items.InfinityGauntlet) mainHand.getItem()).hasAllStones(mainHand);
        }
        
        return false;
    }
}
