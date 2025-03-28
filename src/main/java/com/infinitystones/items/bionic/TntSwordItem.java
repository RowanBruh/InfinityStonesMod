package com.infinitystones.items.bionic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;

public class TntSwordItem extends SwordItem {
    
    private static final float EXPLOSION_POWER = 2.0F;
    private static final int COOLDOWN_TICKS = 40; // 2 seconds
    
    public TntSwordItem(IItemTier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = target.world;
        
        if (!world.isRemote) {
            // Create explosion at the target's location
            world.createExplosion(attacker, target.getPosX(), target.getPosY(), target.getPosZ(), 
                    EXPLOSION_POWER, Explosion.Mode.BREAK);
            
            // Play TNT fuse sound
            world.playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(),
                    SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (!world.isRemote) {
            // Get the direction the player is looking and create an explosion
            float pitch = player.rotationPitch;
            float yaw = player.rotationYaw;
            
            double x = player.getPosX() - MathHelper.sin(yaw * 0.017453292F) * 2.0;
            double y = player.getPosYEye() - MathHelper.sin(pitch * 0.017453292F) * 2.0;
            double z = player.getPosZ() + MathHelper.cos(yaw * 0.017453292F) * 2.0;
            
            world.createExplosion(player, x, y, z, EXPLOSION_POWER, Explosion.Mode.BREAK);
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1.0F);
            
            // Set cooldown to prevent spam
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            // Apply a small amount of damage to the player for balance
            player.attackEntityFrom(DamageSource.GENERIC, 1.0F);
        }
        
        return ActionResult.resultSuccess(stack);
    }
}