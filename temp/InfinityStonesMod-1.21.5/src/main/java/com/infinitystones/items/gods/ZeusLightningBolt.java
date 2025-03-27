package com.infinitystones.items.gods;

import com.infinitystones.util.GodPowerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ZeusLightningBolt extends Item {
    
    private static final int COOLDOWN_TICKS = 40; // 2 seconds cooldown
    private static final int MAX_RANGE = 50; // Maximum range for lightning strike

    public ZeusLightningBolt(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Check if player is on cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemStack);
        }

        // Add cooldown
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // Calculate where the lightning should strike
        HitResult rayTrace = GodPowerHelper.rayTrace(level, player, ClipContext.Fluid.NONE, MAX_RANGE);
        
        if (!level.isClientSide()) {
            // Get position to strike lightning
            BlockPos targetPos = GodPowerHelper.getTargetPosition(rayTrace);
            
            // Create lightning bolt entity
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create((ServerLevel) level);
            if (lightningBolt != null) {
                lightningBolt.moveTo(Vec3.atBottomCenterOf(targetPos));
                lightningBolt.setCause(player);
                level.addFreshEntity(lightningBolt);
            }
            
            // Damage the item
            if (!player.getAbilities().instabuild) {
                itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
        }

        // Add some visual and sound effects
        player.swing(hand);
        
        return InteractionResultHolder.consume(itemStack);
    }
}