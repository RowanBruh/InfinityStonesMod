package com.infinitystones.items.rejected;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class IceologerStaff extends Item {

    private static final int MAX_USE_DURATION = 72000; // Long duration for continuous use
    private static final int USE_COOLDOWN = 20; // 1 second cooldown between uses
    
    public IceologerStaff() {
        super(new Item.Properties()
                .group(InfinityStonesMod.ROWAN_INDUSTRIES_TAB)
                .maxStackSize(1)
                .maxDamage(250)); // 250 uses before it breaks
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Start using the item
        player.setActiveHand(hand);
        return ActionResult.resultConsume(stack);
    }
    
    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if (!(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entity;
        World world = player.world;
        
        // Only trigger effects every few ticks to avoid overwhelming effects
        if (MAX_USE_DURATION - count - 1 % 5 != 0) return;
        
        // Add visual effects around the player
        for (int i = 0; i < 3; i++) {
            double offsetX = (world.rand.nextDouble() - 0.5) * 2.0;
            double offsetY = world.rand.nextDouble() * 2.0;
            double offsetZ = (world.rand.nextDouble() - 0.5) * 2.0;
            
            world.addParticle(
                ParticleTypes.SNOWFLAKE,
                player.getPosX() + offsetX,
                player.getPosY() + offsetY,
                player.getPosZ() + offsetZ,
                offsetX * 0.1,
                0.1,
                offsetZ * 0.1
            );
        }
        
        // Apply slow falling to the player
        if (!world.isRemote && world.getGameTime() % 20 == 0) {
            player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 40, 0, false, false));
        }
        
        // Create ice pathway beneath player if in the air
        if (!world.isRemote && player.isAirBorne && !player.abilities.isFlying &&
                world.getGameTime() % 10 == 0) {
            BlockPos belowPos = player.getPosition().down();
            
            if (world.getBlockState(belowPos).isAir()) {
                // Create temporary ice block
                world.setBlockState(belowPos, Blocks.BLUE_ICE.getDefaultState());
                
                // Schedule block to disappear after a delay
                world.getPendingBlockTicks().scheduleTick(belowPos, Blocks.BLUE_ICE, 60); // 3 seconds
                
                // Damage the staff slightly
                if (!player.abilities.isCreativeMode) {
                    stack.damageItem(1, player, (p) -> p.sendBreakAnimation(player.getActiveHand()));
                }
            }
        }
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        
        if (player == null) return ActionResultType.PASS;
        
        // Freeze water blocks in the area
        if (!world.isRemote) {
            boolean frozeAny = false;
            
            // Check blocks in a small radius
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPos checkPos = pos.add(x, y, z);
                        BlockState state = world.getBlockState(checkPos);
                        
                        // Convert water to ice
                        if (state.getBlock() == Blocks.WATER) {
                            world.setBlockState(checkPos, Blocks.ICE.getDefaultState());
                            frozeAny = true;
                        }
                        // Convert lava to obsidian
                        else if (state.getBlock() == Blocks.LAVA) {
                            world.setBlockState(checkPos, Blocks.OBSIDIAN.getDefaultState());
                            frozeAny = true;
                        }
                    }
                }
            }
            
            if (frozeAny) {
                // Sound effect
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                
                // Damage the item if not in creative mode
                if (!player.abilities.isCreativeMode) {
                    context.getItem().damageItem(3, player, (p) -> p.sendBreakAnimation(context.getHand()));
                }
                
                return ActionResultType.SUCCESS;
            }
        }
        
        return ActionResultType.PASS;
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            
            // Calculate power based on how long the staff was used
            int useDuration = this.getUseDuration(stack) - timeLeft;
            float power = MathHelper.clamp(useDuration / 20.0F, 0.1F, 1.0F);
            
            if (!world.isRemote) {
                // Create an ice blast attack
                Vector3d lookVec = player.getLookVec();
                
                // Calculate the range based on power
                double range = 10.0 * power;
                
                // Raycast to find where the blast should hit
                Vector3d startPos = player.getEyePosition(1.0F);
                Vector3d endPos = startPos.add(lookVec.scale(range));
                
                BlockRayTraceResult rayTrace = world.rayTraceBlocks(
                    new RayTraceContext(
                        startPos,
                        endPos,
                        RayTraceContext.BlockMode.COLLIDER,
                        RayTraceContext.FluidMode.NONE,
                        player
                    )
                );
                
                BlockPos hitPos = rayTrace.getPos();
                
                // Freeze area around hit position
                int radius = Math.max(1, (int)(power * 3));
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            // Only affect blocks within a sphere
                            if (x*x + y*y + z*z <= radius*radius) {
                                BlockPos affectedPos = hitPos.add(x, y, z);
                                BlockState state = world.getBlockState(affectedPos);
                                
                                // Convert water to ice
                                if (state.getBlock() == Blocks.WATER) {
                                    world.setBlockState(affectedPos, Blocks.ICE.getDefaultState());
                                } 
                                // Create snow on solid surfaces
                                else if (world.getBlockState(affectedPos.up()).isAir() && 
                                         state.isSolidSide(world, affectedPos, Direction.UP)) {
                                    world.setBlockState(affectedPos.up(), Blocks.SNOW.getDefaultState());
                                }
                            }
                        }
                    }
                }
                
                // Affect entities in the area
                Vector3d hitVector = new Vector3d(hitPos.getX(), hitPos.getY(), hitPos.getZ());
                List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(
                    player, 
                    net.minecraft.util.math.AxisAlignedBB.fromVector(hitVector).grow(radius)
                );
                
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) entity;
                        // Apply slowness and freezing damage
                        living.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, (int)(power * 3), false, true));
                        living.attackEntityFrom(DamageSource.MAGIC, power * 5.0F);
                    }
                }
                
                // Play ice blast sound
                world.playSound(null, player.getPosition(), 
                        SoundEvents.BLOCK_GLASS_BREAK, 
                        SoundCategory.PLAYERS, 1.0F, 0.8F);
                
                // Damage item based on power used
                if (!player.abilities.isCreativeMode) {
                    stack.damageItem((int)(power * 5) + 1, player, (p) -> p.sendBreakAnimation(player.getActiveHand()));
                }
                
                // Set cooldown
                player.getCooldownTracker().setCooldown(this, USE_COOLDOWN);
            }
        }
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Freeze the target temporarily
        target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 80, 2)); // Slowness III for 4 seconds
        
        // Visual effect
        target.world.addParticle(
            ParticleTypes.ITEM_SNOWBALL,
            target.getPosX(),
            target.getPosY() + target.getHeight() / 2,
            target.getPosZ(),
            0, 0, 0
        );
        
        // Play hit sound
        target.world.playSound(null, target.getPosition(), 
                SoundEvents.ENTITY_PLAYER_HURT_FREEZE, 
                SoundCategory.PLAYERS, 1.0F, 1.0F);
        
        // Damage the staff
        stack.damageItem(2, attacker, (entity) -> {
            entity.sendBreakAnimation(attacker.getActiveHand());
        });
        
        return true;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return MAX_USE_DURATION;
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR; // Staff-like pose when using
    }
}