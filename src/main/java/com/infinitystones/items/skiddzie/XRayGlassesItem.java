package com.infinitystones.items.skiddzie;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class XRayGlassesItem extends Item {
    
    private static final int X_RAY_RANGE = 12;
    private static final Map<UUID, Long> lastUseTime = new HashMap<>();
    private static final long USE_DURATION = 10000; // 10 seconds in milliseconds
    
    public XRayGlassesItem(Properties properties) {
        super(properties);
    }
    
    /**
     * Checks if the player is wearing the X-Ray glasses
     */
    public static boolean isWearingGlasses(PlayerEntity player) {
        ItemStack headItem = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
        return !headItem.isEmpty() && headItem.getItem() instanceof XRayGlassesItem;
    }
    
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    
    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (world.isRemote) {
            // Update last use time
            lastUseTime.put(player.getUniqueID(), System.currentTimeMillis());
            
            // Apply night vision effect to see in the dark
            player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 300, 0, false, false));
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        PlayerEntity player = mc.player;
        
        // Check if player is wearing X-Ray glasses and has used them recently
        if (isWearingGlasses(player) && isActivelyUsing(player.getUniqueID())) {
            handleXRayVision(mc, player);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    private static void handleXRayVision(Minecraft mc, PlayerEntity player) {
        World world = mc.world;
        ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();
        
        BlockPos playerPos = player.getPosition();
        
        // Scan blocks in range
        for (int x = -X_RAY_RANGE; x <= X_RAY_RANGE; x++) {
            for (int y = -X_RAY_RANGE; y <= X_RAY_RANGE; y++) {
                for (int z = -X_RAY_RANGE; z <= X_RAY_RANGE; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    
                    // Highlight valuable ores - we'll make them temporarily "glowing"
                    if (isValuableBlock(state)) {
                        // Add a temporary particle effect to highlight the ore
                        double distance = Math.sqrt(playerPos.distanceSq(pos));
                        if (distance <= X_RAY_RANGE && world.rand.nextInt(8) == 0) {
                            world.addParticle(
                                    net.minecraft.particles.ParticleTypes.END_ROD,
                                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    0, 0, 0);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Check if a block is a valuable ore worth highlighting
     */
    private static boolean isValuableBlock(BlockState state) {
        return state.getBlock() == Blocks.DIAMOND_ORE ||
               state.getBlock() == Blocks.EMERALD_ORE ||
               state.getBlock() == Blocks.GOLD_ORE ||
               state.getBlock() == Blocks.ANCIENT_DEBRIS ||
               state.getBlock() == Blocks.NETHER_GOLD_ORE ||
               state.getBlock() == Blocks.NETHER_QUARTZ_ORE ||
               state.getBlock() == Blocks.REDSTONE_ORE ||
               state.getBlock() == Blocks.LAPIS_ORE;
    }
    
    /**
     * Check if the player is actively using the glasses (to prevent them from staying on permanently)
     */
    private static boolean isActivelyUsing(UUID playerID) {
        long lastUse = lastUseTime.getOrDefault(playerID, 0L);
        return System.currentTimeMillis() - lastUse < USE_DURATION;
    }
}