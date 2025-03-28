package com.infinitystones.items.skiddzie;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class WeatherStaffItem extends Item {
    
    private static final int COOLDOWN_TICKS = 300; // 15 seconds
    private static final Random RANDOM = new Random();
    
    // Weather cycles: Clear -> Rain -> Thunder -> Clear
    private enum WeatherType {
        CLEAR, RAIN, THUNDER
    }
    
    public WeatherStaffItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        if (world.isRemote) {
            return ActionResult.resultSuccess(stack);
        }
        
        ServerWorld serverWorld = (ServerWorld) world;
        MinecraftServer server = serverWorld.getServer();
        
        // Determine current weather type
        WeatherType currentWeather = getCurrentWeatherType(serverWorld);
        
        // Cycle to next weather
        WeatherType nextWeather = getNextWeatherType(currentWeather);
        
        // Apply the new weather
        setWeather(serverWorld, nextWeather);
        
        // Visual and audio effects
        createWeatherEffects(serverWorld, player, nextWeather);
        
        // Notify player
        notifyWeatherChange(player, nextWeather);
        
        // Add cooldown
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        
        return ActionResult.resultSuccess(stack);
    }
    
    private WeatherType getCurrentWeatherType(ServerWorld world) {
        if (world.isThundering()) {
            return WeatherType.THUNDER;
        } else if (world.isRaining()) {
            return WeatherType.RAIN;
        } else {
            return WeatherType.CLEAR;
        }
    }
    
    private WeatherType getNextWeatherType(WeatherType current) {
        switch (current) {
            case CLEAR:
                return WeatherType.RAIN;
            case RAIN:
                return WeatherType.THUNDER;
            case THUNDER:
            default:
                return WeatherType.CLEAR;
        }
    }
    
    private void setWeather(ServerWorld world, WeatherType weatherType) {
        switch (weatherType) {
            case CLEAR:
                world.func_241113_a_(0, 0, false, false); // Clear weather
                break;
            case RAIN:
                world.func_241113_a_(0, 6000, true, false); // 5 minutes of rain
                break;
            case THUNDER:
                world.func_241113_a_(0, 6000, true, true); // 5 minutes of thunder
                break;
        }
    }
    
    private void createWeatherEffects(ServerWorld world, PlayerEntity player, WeatherType weatherType) {
        BlockPos playerPos = player.getPosition();
        
        // Play different sounds based on the new weather
        switch (weatherType) {
            case CLEAR:
                world.playSound(null, playerPos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                spawnSunParticles(world, playerPos);
                break;
            case RAIN:
                world.playSound(null, playerPos, SoundEvents.WEATHER_RAIN, SoundCategory.AMBIENT, 1.0F, 1.0F);
                spawnRainParticles(world, playerPos);
                break;
            case THUNDER:
                world.playSound(null, playerPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.AMBIENT, 1.0F, 1.0F);
                spawnThunderParticles(world, playerPos);
                // Spawn a display-only lightning bolt near the player
                BlockPos lightningPos = playerPos.add(RANDOM.nextInt(10) - 5, 0, RANDOM.nextInt(10) - 5);
                world.addLightningBolt(new net.minecraft.entity.effect.LightningBoltEntity(world, lightningPos.getX(), lightningPos.getY(), lightningPos.getZ(), true));
                break;
        }
    }
    
    private void spawnSunParticles(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 50; i++) {
            double xOffset = (RANDOM.nextDouble() - 0.5) * 16;
            double yOffset = RANDOM.nextDouble() * 3 + 2;
            double zOffset = (RANDOM.nextDouble() - 0.5) * 16;
            world.spawnParticle(ParticleTypes.END_ROD, 
                               pos.getX() + xOffset, 
                               pos.getY() + yOffset, 
                               pos.getZ() + zOffset, 
                               1, 0, 0, 0, 0.1);
        }
    }
    
    private void spawnRainParticles(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 100; i++) {
            double xOffset = (RANDOM.nextDouble() - 0.5) * 16;
            double yOffset = RANDOM.nextDouble() * 3 + 5;
            double zOffset = (RANDOM.nextDouble() - 0.5) * 16;
            world.spawnParticle(ParticleTypes.DRIPPING_WATER, 
                               pos.getX() + xOffset, 
                               pos.getY() + yOffset, 
                               pos.getZ() + zOffset, 
                               1, 0, -0.1, 0, 0.1);
        }
    }
    
    private void spawnThunderParticles(ServerWorld world, BlockPos pos) {
        for (int i = 0; i < 80; i++) {
            double xOffset = (RANDOM.nextDouble() - 0.5) * 16;
            double yOffset = RANDOM.nextDouble() * 3 + 8;
            double zOffset = (RANDOM.nextDouble() - 0.5) * 16;
            world.spawnParticle(ParticleTypes.SOUL_FIRE_FLAME, 
                               pos.getX() + xOffset, 
                               pos.getY() + yOffset, 
                               pos.getZ() + zOffset, 
                               1, 0, -0.1, 0, 0.1);
        }
    }
    
    private void notifyWeatherChange(PlayerEntity player, WeatherType weatherType) {
        StringTextComponent message;
        switch (weatherType) {
            case CLEAR:
                message = new StringTextComponent("The skies clear up!");
                message.mergeStyle(TextFormatting.YELLOW);
                break;
            case RAIN:
                message = new StringTextComponent("Rain begins to fall...");
                message.mergeStyle(TextFormatting.AQUA);
                break;
            case THUNDER:
                message = new StringTextComponent("Thunder and lightning rage!");
                message.mergeStyle(TextFormatting.RED);
                break;
            default:
                return;
        }
        player.sendMessage(message, player.getUniqueID());
    }
}