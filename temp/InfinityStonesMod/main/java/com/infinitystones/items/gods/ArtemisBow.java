package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.Random;

public class ArtemisBow extends BowItem {
    
    private static final float BASE_DAMAGE = 2.0F; // Base damage multiplier for arrows
    private static final float MOONLIGHT_DAMAGE_BONUS = 1.5F; // Extra damage at night
    private final Random random = new Random();

    public ArtemisBow(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            boolean hasInfiniteArrows = player.abilities.isCreativeMode || 
                    EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack arrowStack = player.findAmmo(stack);

            if (!arrowStack.isEmpty() || hasInfiniteArrows) {
                if (arrowStack.isEmpty()) {
                    arrowStack = new ItemStack(Items.ARROW);
                }

                int useDuration = this.getUseDuration(stack) - timeLeft;
                float arrowVelocity = getArrowVelocity(useDuration);

                if (arrowVelocity >= 0.1F) {
                    boolean consumeArrow = !hasInfiniteArrows;

                    if (!world.isRemote) {
                        ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                        AbstractArrowEntity arrow = arrowItem.createArrow(world, arrowStack, player);
                        arrow = customizeArrow(arrow, stack, player, world);
                        
                        arrow.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 
                                arrowVelocity * 3.0F, 1.0F);

                        // Apply damage multiplier based on draw time
                        float damageMultiplier = arrowVelocity * getBowPowerDamage(player, world);
                        arrow.setDamage(arrow.getDamage() * damageMultiplier);

                        // Apply knockback
                        int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (knockbackLevel > 0) {
                            arrow.setKnockbackStrength(knockbackLevel);
                        }

                        // Apply flame
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            arrow.setFire(100);
                        }

                        // Damage the bow
                        stack.damageItem(1, player, (p) -> p.sendBreakAnimation(player.getActiveHand()));

                        // Set as critical for visual effect
                        if (arrowVelocity >= 1.0F) {
                            arrow.setCritArrow(true);
                        }

                        world.addEntity(arrow);
                    }

                    world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                            1.0F / (random.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

                    if (consumeArrow) {
                        arrowStack.shrink(1);
                        if (arrowStack.isEmpty()) {
                            player.inventory.deleteStack(arrowStack);
                        }
                    }

                    player.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    /**
     * Customizes the arrow with Artemis' power
     */
    private AbstractArrowEntity customizeArrow(AbstractArrowEntity arrow, ItemStack bowStack, PlayerEntity player, World world) {
        // Special effect: Artemis arrows can see through darkness
        arrow.setGlowing(true);
        
        // Special effect: Artemis arrows are more accurate
        arrow.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        
        // Add visual moon particle effect
        addMoonlightEffect(arrow, world);
        
        return arrow;
    }
    
    /**
     * Calculate damage bonus based on time of day and environment
     */
    private float getBowPowerDamage(PlayerEntity player, World world) {
        float damage = BASE_DAMAGE;
        
        // Stronger during night time
        long worldTime = world.getDayTime() % 24000;
        if (worldTime > 13000 && worldTime < 23000) {
            damage *= MOONLIGHT_DAMAGE_BONUS;
        }
        
        // Stronger when under the sky
        if (world.canSeeSky(player.getPosition())) {
            damage *= 1.2F;
        }
        
        return damage;
    }
    
    /**
     * Add moonlight particle effect to arrows
     */
    private void addMoonlightEffect(AbstractArrowEntity arrow, World world) {
        if (world.isRemote) {
            for (int i = 0; i < 7; i++) {
                double offsetX = random.nextFloat() * 0.2 - 0.1;
                double offsetY = random.nextFloat() * 0.2 - 0.1;
                double offsetZ = random.nextFloat() * 0.2 - 0.1;
                
                world.addParticle(
                    ParticleTypes.END_ROD,
                    arrow.getPosX() + offsetX,
                    arrow.getPosY() + offsetY,
                    arrow.getPosZ() + offsetZ,
                    0, 0, 0);
            }
        }
    }
}