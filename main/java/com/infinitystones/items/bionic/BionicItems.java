package com.infinitystones.items.bionic;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = InfinityStonesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(InfinityStonesMod.MOD_ID)
public class BionicItems {
    
    // Bionic's OP Items
    public static final Item HUNDRED_DAYS_SWORD = null;
    public static final Item ULTRA_BOW = null;
    public static final Item GIANT_PICKAXE = null;
    public static final Item LIGHTNING_TRIDENT = null;
    public static final Item XRAY_GOGGLES = null;
    public static final Item BIONIC_CHALLENGE_BOOK = null;
    public static final Item TNT_STICK = null;
    public static final Item TELEPORT_PEARL = null;
    public static final Item BIONIC_LUCKY_BLOCK = null;
    
    // List to keep track of all items
    private static final List<Item> ITEMS = new ArrayList<>();
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        // 100 Days Sword - Ultra powerful sword that gets stronger every "day"
        register(event, new SwordItem(
                ItemTier.NETHERITE, 15, -2.4F,
                new Item.Properties().group(BionicCreativeTab.INSTANCE).maxStackSize(1)
        ) {
            @Override
            public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
                // Increase power each Minecraft day cycle
                if (worldIn.getDayTime() % 24000 == 0 && !worldIn.isRemote) {
                    stack.getOrCreateTag().putInt("days", stack.getOrCreateTag().getInt("days") + 1);
                }
                super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
            }
            
            @Override
            public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                // Additional damage based on days survived
                int days = stack.getOrCreateTag().getInt("days");
                if (days > 0) {
                    target.attackEntityFrom(DamageSource.GENERIC, days);
                }
                return super.hitEntity(stack, target, attacker);
            }
        }, "hundred_days_sword");
        
        // Ultra Bow - Shoots multiple arrows with explosive impact
        register(event, new BowItem(new Item.Properties().group(BionicCreativeTab.INSTANCE).maxDamage(1000)) {
            @Override
            public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
                if (entityLiving instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entityLiving;
                    boolean hasArrows = !player.findAmmo(stack).isEmpty();
                    
                    if (hasArrows) {
                        // Shoot 5 arrows in spread pattern
                        for (int i = 0; i < 5; i++) {
                            super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
                        }
                    }
                }
            }
        }, "ultra_bow");
        
        // Giant Pickaxe - Mines in a 3x3 area
        register(event, new PickaxeItem(
                ItemTier.DIAMOND, 5, -2.8F,
                new Item.Properties().group(BionicCreativeTab.INSTANCE).maxStackSize(1)
        ) {
            @Override
            public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
                if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
                    // Mine blocks in a 3x3 area
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {
                                BlockPos newPos = pos.add(x, y, z);
                                worldIn.destroyBlock(newPos, true);
                            }
                        }
                    }
                    stack.damageItem(1, entityLiving, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
                }
                return true;
            }
        }, "giant_pickaxe");
        
        // Lightning Trident - Summons lightning on impact
        register(event, new TridentItem(new Item.Properties().group(BionicCreativeTab.INSTANCE).maxDamage(250)) {
            @Override
            public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                if (!target.world.isRemote) {
                    BlockPos pos = target.getPosition();
                    // Summon lightning at target's position
                    LightningBoltEntity lightning = new LightningBoltEntity(
                            EntityType.LIGHTNING_BOLT, target.world);
                    lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    target.world.addEntity(lightning);
                }
                return super.hitEntity(stack, target, attacker);
            }
        }, "lightning_trident");
        
        // X-Ray Goggles - Allows seeing through walls (simulated with Night Vision + Glowing effects)
        register(event, new Item(new Item.Properties().group(BionicCreativeTab.INSTANCE).maxStackSize(1)) {
            @Override
            public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
                if (entityIn instanceof PlayerEntity && isSelected) {
                    PlayerEntity player = (PlayerEntity) entityIn;
                    // Apply Night Vision effect to simulate X-ray
                    player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 400, 0, false, false));
                    // Apply Glowing effect to see entities through walls
                    player.addPotionEffect(new EffectInstance(Effects.GLOWING, 400, 0, false, false));
                }
                super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
            }
        }, "xray_goggles");
        
        // Bionic Challenge Book - Gives random challenges when right-clicked
        register(event, new Item(new Item.Properties().group(BionicCreativeTab.INSTANCE).maxStackSize(1)) {
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
                ItemStack stack = playerIn.getHeldItem(handIn);
                
                if (!worldIn.isRemote) {
                    Random rand = new Random();
                    int challenge = rand.nextInt(5);
                    
                    switch (challenge) {
                        case 0:
                            playerIn.sendMessage(new StringTextComponent("Challenge: Survive 100 days!"), playerIn.getUniqueID());
                            break;
                        case 1:
                            playerIn.sendMessage(new StringTextComponent("Challenge: Find all infinity stones in 30 minutes!"), playerIn.getUniqueID());
                            break;
                        case 2:
                            playerIn.sendMessage(new StringTextComponent("Challenge: Defeat the Ender Dragon with only wooden tools!"), playerIn.getUniqueID());
                            break;
                        case 3:
                            playerIn.sendMessage(new StringTextComponent("Challenge: Build a house underwater!"), playerIn.getUniqueID());
                            break;
                        case 4:
                            playerIn.sendMessage(new StringTextComponent("Challenge: Collect one of every item in the game!"), playerIn.getUniqueID());
                            break;
                    }
                }
                
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }, "bionic_challenge_book");
        
        // TNT Stick - Creates explosions when right-clicked
        register(event, new Item(new Item.Properties().group(BionicCreativeTab.INSTANCE).maxStackSize(16)) {
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
                ItemStack stack = playerIn.getHeldItem(handIn);
                
                if (!worldIn.isRemote) {
                    worldIn.createExplosion(null, 
                        playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), 
                        4.0F, Explosion.Mode.BREAK);
                    
                    if (!playerIn.abilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
                
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }, "tnt_stick");
        
        // Teleport Pearl - Teleports player much further than Ender Pearl
        register(event, new Item(new Item.Properties().group(BionicCreativeTab.INSTANCE).maxStackSize(16)) {
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
                ItemStack stack = playerIn.getHeldItem(handIn);
                
                if (!worldIn.isRemote) {
                    // Get player's look direction and teleport 50 blocks in that direction
                    Vector3d look = playerIn.getLookVec();
                    double x = playerIn.getPosX() + look.x * 50;
                    double y = playerIn.getPosY() + look.y * 50;
                    double z = playerIn.getPosZ() + look.z * 50;
                    
                    playerIn.teleportKeepLoaded(x, y, z);
                    
                    if (!playerIn.abilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
                
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }, "teleport_pearl");
        
        // Bionic Lucky Block - Custom lucky block with Bionic-themed rewards
        register(event, new BlockItem(
                ModBlocks.BIONIC_LUCKY_BLOCK,
                new Item.Properties().group(BionicCreativeTab.INSTANCE)
        ), "bionic_lucky_block");
    }
    
    private static void register(RegistryEvent.Register<Item> event, Item item, String name) {
        item.setRegistryName(new ResourceLocation(InfinityStonesMod.MOD_ID, name));
        event.getRegistry().register(item);
        ITEMS.add(item);
    }
    
    public static List<Item> getItems() {
        return ITEMS;
    }
}