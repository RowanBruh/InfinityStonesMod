package com.infinitystones.items.rejected;

import com.infinitystones.tabs.ModItemGroups;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

/**
 * Wildfire Staff - a weapon based on the rejected Wildfire mob concept.
 * In this implementation, it creates bursts of fire and damages entities around the player.
 */
public class WildfireStaff extends Item {

    private static final int COOLDOWN = 80; // 4 seconds cooldown

    public WildfireStaff() {
        super(new Item.Properties()
                .group(ModItemGroups.ROWAN_INDUSTRIES)
                .maxStackSize(1)
                .maxDamage(100)); // Can be used 100 times before breaking
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultFail(stack);
        }
        
        if (!world.isRemote) {
            // Create a wildfire attack
            createWildfireAttack((ServerWorld) world, player);
            
            // Set cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN);
            
            // Play sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,
                    1.0F, 0.8F);
            
            // Damage the staff
            if (!player.isCreative()) {
                stack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
            }
        }
        
        return ActionResult.resultSuccess(stack);
    }
    
    private void createWildfireAttack(ServerWorld world, PlayerEntity player) {
        // Create a fire circle around the player
        BlockPos playerPos = player.getPosition();
        int range = 8;
        
        // 1. Create fire particles in a spiral from the player upward
        Vector3d center = player.getPositionVec().add(0, 1, 0);
        
        for (int height = 0; height < 6; height++) {
            double radius = 1.0 + (height * 0.5);
            for (int angle = 0; angle < 360; angle += 10) {
                double radian = Math.toRadians(angle);
                double x = center.x + (Math.cos(radian) * radius);
                double z = center.z + (Math.sin(radian) * radius);
                double y = center.y + (height * 0.5);
                
                world.spawnParticle(
                        ParticleTypes.FLAME,
                        x, y, z,
                        1, 0, 0, 0, 0
                );
            }
        }
        
        // 2. Find and damage nearby entities
        List<LivingEntity> entities = world.getEntitiesWithinAABB(
                LivingEntity.class,
                new AxisAlignedBB(
                        playerPos.getX() - range, playerPos.getY() - 2, playerPos.getZ() - range,
                        playerPos.getX() + range, playerPos.getY() + 4, playerPos.getZ() + range
                ),
                entity -> entity != player
        );
        
        if (entities.isEmpty()) {
            player.sendStatusMessage(new StringTextComponent("No targets in range for the Wildfire attack!"), true);
            return;
        }
        
        int targetCount = 0;
        for (LivingEntity target : entities) {
            // Calculate distance
            double distance = player.getDistanceSq(target);
            if (distance <= range * range) {
                // Apply damage inversely proportional to distance
                float damage = (float) (10.0 - (distance / (range * range) * 6.0));
                target.attackEntityFrom(DamageSource.IN_FIRE, damage);
                
                // Set the entity on fire
                target.setFire(5); // 5 seconds of fire
                
                // Create fire particles at target location
                world.spawnParticle(
                        ParticleTypes.LAVA,
                        target.getPosX(), target.getPosY() + 1, target.getPosZ(),
                        10, 0.4, 0.5, 0.4, 0.1
                );
                
                targetCount++;
            }
        }
        
        player.sendStatusMessage(new StringTextComponent("Wildfire attack hit " + targetCount + " targets!"), true);
        
        // 3. Place actual fire blocks in a small circle (only on solid ground)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // Skip center block (where player is)
                if (x == 0 && z == 0) continue;
                
                // Create circle pattern
                if (x*x + z*z <= 4) {
                    BlockPos firePos = playerPos.add(x, 0, z);
                    BlockPos belowPos = firePos.down();
                    
                    // Only place fire on solid ground and in air
                    if (world.getBlockState(belowPos).isSolid() && world.isAirBlock(firePos)) {
                        world.setBlockState(firePos, net.minecraft.block.Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true; // Give the staff a permanent enchantment glow
    }
}