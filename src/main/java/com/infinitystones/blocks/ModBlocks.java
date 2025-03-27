package com.infinitystones.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.bionic.BionicItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    
    private static final List<Block> BLOCKS = new ArrayList<>();
    
    // Block instances
    public static final Block BIONIC_LUCKY_BLOCK = register(
            "bionic_lucky_block", 
            new Block(Block.Properties.create(Material.IRON)
                    .hardnessAndResistance(0.5F)
                    .sound(SoundType.METAL)) {
                @Override
                public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
                    if (!worldIn.isRemote) {
                        Random rand = new Random();
                        int outcome = rand.nextInt(10);
                        
                        switch (outcome) {
                            case 0: // Spawn TNT
                                worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, Explosion.Mode.BREAK);
                                break;
                            case 1: // Give player Bionic items
                                player.addItemStackToInventory(new ItemStack(InfinityStonesMod.findItem("infinitystones:hundred_days_sword")));
                                break;
                            case 2: // Spawn hostile mobs
                                for (int i = 0; i < 5; i++) {
                                    ZombieEntity zombie = new ZombieEntity(EntityType.ZOMBIE, worldIn);
                                    zombie.setPosition(pos.getX(), pos.getY(), pos.getZ());
                                    worldIn.addEntity(zombie);
                                }
                                break;
                            case 3: // Teleport player randomly
                                player.teleportKeepLoaded(
                                        pos.getX() + rand.nextInt(100) - 50,
                                        pos.getY() + rand.nextInt(50),
                                        pos.getZ() + rand.nextInt(100) - 50);
                                break;
                            case 4: // Give player diamond blocks
                                player.addItemStackToInventory(new ItemStack(Blocks.DIAMOND_BLOCK, 16));
                                break;
                            case 5: // Apply potion effects
                                player.addPotionEffect(new EffectInstance(Effects.SPEED, 6000, 3));
                                player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 6000, 3));
                                break;
                            case 6: // Spawn lightning
                                LightningBoltEntity lightning = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, worldIn);
                                lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
                                worldIn.addEntity(lightning);
                                break;
                            case 7: // Give XP levels
                                player.addExperienceLevel(30);
                                break;
                            case 8: // Spawn falling anvils
                                for (int i = 0; i < 10; i++) {
                                    FallingBlockEntity anvil = new FallingBlockEntity(
                                            worldIn, pos.getX() + rand.nextFloat() * 10 - 5,
                                            pos.getY() + 20, pos.getZ() + rand.nextFloat() * 10 - 5,
                                            Blocks.ANVIL.getDefaultState());
                                    worldIn.addEntity(anvil);
                                }
                                break;
                            case 9: // Give random Infinity Stone
                                // Link to existing infinity stones
                                player.addItemStackToInventory(new ItemStack(InfinityStonesMod.findItem("infinitystones:space_stone")));
                                break;
                        }
                    }
                    super.onBlockHarvested(worldIn, pos, state, player);
                }
            });
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : BLOCKS) {
            event.getRegistry().register(block);
        }
    }
    
    private static Block register(String name, Block block) {
        block.setRegistryName(new ResourceLocation(InfinityStonesMod.MOD_ID, name));
        BLOCKS.add(block);
        return block;
    }
    
    public static List<Block> getBlocks() {
        return BLOCKS;
    }
}