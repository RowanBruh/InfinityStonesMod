package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ZeusLightningBolt extends Item {
    
    private static final int COOLDOWN_TICKS = 40; // 2 seconds cooldown
    private static final int MAX_RANGE = 50; // Maximum range for lightning strike

    public ZeusLightningBolt(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        // Check if player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultPass(itemStack);
        }

        // Add cooldown
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);

        // Calculate where the lightning should strike
        RayTraceResult rayTrace = GodPowerHelper.rayTrace(world, player, RayTraceContext.FluidMode.NONE, MAX_RANGE);
        
        if (!world.isRemote) {
            // Get position to strike lightning
            BlockPos targetPos = GodPowerHelper.getTargetPosition(rayTrace);
            
            // Create lightning bolt entity
            LightningBoltEntity lightningBolt = EntityType.LIGHTNING_BOLT.create((ServerWorld) world);
            if (lightningBolt != null) {
                lightningBolt.moveForced(Vector3d.copyCenteredHorizontally(targetPos));
                lightningBolt.setCaster(player);
                world.addEntity(lightningBolt);
            }
            
            // Damage the item
            if (!player.abilities.isCreativeMode) {
                itemStack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
            }
        }

        // Add some visual and sound effects
        player.swingArm(hand);
        
        return ActionResult.resultConsume(itemStack);
    }
}