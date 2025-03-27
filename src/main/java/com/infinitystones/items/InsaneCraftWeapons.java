package com.infinitystones.items;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class InsaneCraftWeapons {
    private static final Random random = new Random();
    
    // Tier for insane weapons
    public static class InsaneTier implements IItemTier {
        private final int harvestLevel;
        private final int maxUses;
        private final float efficiency;
        private final float attackDamage;
        private final int enchantability;
        private final Ingredient repairMaterial;
        
        public InsaneTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Ingredient repairMaterial) {
            this.harvestLevel = harvestLevel;
            this.maxUses = maxUses;
            this.efficiency = efficiency;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
            this.repairMaterial = repairMaterial;
        }
        
        @Override
        public int getMaxUses() {
            return maxUses;
        }
        
        @Override
        public float getEfficiency() {
            return efficiency;
        }
        
        @Override
        public float getAttackDamage() {
            return attackDamage;
        }
        
        @Override
        public int getHarvestLevel() {
            return harvestLevel;
        }
        
        @Override
        public int getEnchantability() {
            return enchantability;
        }
        
        @Override
        public Ingredient getRepairMaterial() {
            return repairMaterial;
        }
    }
    
    // The Royal Guardian Sword (inspired by Insane Craft)
    public static class RoyalGuardianSword extends SwordItem {
        public RoyalGuardianSword() {
            super(
                new InsaneTier(4, 5000, 12.0f, 25.0f, 30, 
                    Ingredient.fromItems(ModItems.ULTIMATE_INGOT.get())
                ),
                0, // Base attack damage is managed through the tier
                -2.0f, // Attack speed modifier
                new Item.Properties()
                    .group(InfinityStonesMod.INFINITY_GROUP)
                    .maxStackSize(1)
                    .setNoRepair()
            );
        }
        
        @Override
        public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // Apply fire and lightning effects
            if (!target.world.isRemote && attacker instanceof PlayerEntity) {
                // Set fire to the target
                target.setFire(5);
                
                // Chance to summon lightning
                if (random.nextFloat() < 0.3f) {
                    if (target.world instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld) target.world;
                        LightningBoltEntity lightning = new LightningBoltEntity(
                                serverWorld.getWorld().getRegistryKey().getRegistry(), 
                                target.getPosX(), target.getPosY(), target.getPosZ(), false);
                        serverWorld.addEntity(lightning);
                    }
                }
                
                // Apply random negative potion effects
                if (random.nextFloat() < 0.5f) {
                    target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
                    target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 1));
                }
            }
            
            return super.hitEntity(stack, target, attacker);
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            
            if (!worldIn.isRemote) {
                if (!playerIn.getCooldownTracker().hasCooldown(this)) {
                    // Create area damage effect
                    int radius = 8;
                    float damage = 10.0f;
                    
                    List<LivingEntity> entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, 
                        new AxisAlignedBB(
                            playerIn.getPosX() - radius, playerIn.getPosY() - radius, playerIn.getPosZ() - radius,
                            playerIn.getPosX() + radius, playerIn.getPosY() + radius, playerIn.getPosZ() + radius
                        )
                    );
                    
                    for (LivingEntity entity : entities) {
                        if (entity != playerIn) {
                            entity.attackEntityFrom(DamageSource.causePlayerDamage(playerIn), damage);
                            entity.setFire(3);
                        }
                    }
                    
                    // Visual and sound effects
                    worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                            
                    // Send message to player
                    playerIn.sendMessage(
                        new StringTextComponent("The Royal Guardian's Wrath!").mergeStyle(TextFormatting.GOLD),
                        playerIn.getUniqueID()
                    );
                    
                    // Set cooldown
                    playerIn.getCooldownTracker().setCooldown(this, 200);
                }
            }
            
            return ActionResult.resultSuccess(stack);
        }
    }
    
    // The Ultimate Bow (inspired by Insane Craft)
    public static class UltimateBow extends Item {
        public UltimateBow() {
            super(new Item.Properties()
                .group(InfinityStonesMod.INFINITY_GROUP)
                .maxStackSize(1)
                .setNoRepair());
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            
            if (!worldIn.isRemote) {
                if (!playerIn.getCooldownTracker().hasCooldown(this)) {
                    // Get player look direction
                    Vector3d lookVec = playerIn.getLookVec();
                    double power = ModConfig.COMMON_CONFIG.insaneCraftWeaponPower.get();
                    
                    // Create multiple explosions in the direction player is looking
                    for (int i = 5; i < 30; i += 5) {
                        double x = playerIn.getPosX() + lookVec.x * i;
                        double y = playerIn.getPosY() + 1.5 + lookVec.y * i;
                        double z = playerIn.getPosZ() + lookVec.z * i;
                        
                        float explosionPower = 2.0f + (float)(power / 5.0);
                        worldIn.createExplosion(playerIn, x, y, z, explosionPower, Explosion.Mode.DESTROY);
                    }
                    
                    // Send message to player
                    playerIn.sendMessage(
                        new StringTextComponent("Ultimate Bow unleashes destruction!").mergeStyle(TextFormatting.RED),
                        playerIn.getUniqueID()
                    );
                    
                    // Set cooldown
                    playerIn.getCooldownTracker().setCooldown(this, 100);
                }
            }
            
            return ActionResult.resultSuccess(stack);
        }
    }
    
    // The Hammer of Thor (inspired by Insane Craft)
    public static class ThorHammer extends Item {
        public ThorHammer() {
            super(new Item.Properties()
                .group(InfinityStonesMod.INFINITY_GROUP)
                .maxStackSize(1)
                .setNoRepair());
        }
        
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            
            if (!worldIn.isRemote && worldIn instanceof ServerWorld) {
                if (!playerIn.getCooldownTracker().hasCooldown(this)) {
                    ServerWorld serverWorld = (ServerWorld) worldIn;
                    int radius = 10;
                    
                    // Summon lightning bolts around the player
                    for (int i = 0; i < 10; i++) {
                        double x = playerIn.getPosX() + (random.nextDouble() * radius * 2) - radius;
                        double z = playerIn.getPosZ() + (random.nextDouble() * radius * 2) - radius;
                        BlockPos strikePos = new BlockPos(x, worldIn.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE, (int)x, (int)z), z);
                        
                        LightningBoltEntity lightning = new LightningBoltEntity(
                            serverWorld.getWorld().getRegistryKey().getRegistry(), 
                            strikePos.getX(), strikePos.getY(), strikePos.getZ(), false);
                        serverWorld.addEntity(lightning);
                    }
                    
                    // Apply damage to nearby entities
                    List<LivingEntity> entities = worldIn.getEntitiesWithinAABB(LivingEntity.class, 
                        new AxisAlignedBB(
                            playerIn.getPosX() - radius, playerIn.getPosY() - radius, playerIn.getPosZ() - radius,
                            playerIn.getPosX() + radius, playerIn.getPosY() + radius, playerIn.getPosZ() + radius
                        )
                    );
                    
                    for (LivingEntity entity : entities) {
                        if (entity != playerIn) {
                            entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 15.0f);
                        }
                    }
                    
                    // Send message to player
                    playerIn.sendMessage(
                        new StringTextComponent("The power of Thor strikes!").mergeStyle(TextFormatting.AQUA),
                        playerIn.getUniqueID()
                    );
                    
                    // Set cooldown
                    playerIn.getCooldownTracker().setCooldown(this, 200);
                }
            }
            
            return ActionResult.resultSuccess(stack);
        }
    }
    
    // The Infinity Armor Effect - used by all armor pieces
    public static void applyInfinityArmorEffect(PlayerEntity player, ItemStack stack) {
        // Give powerful effects when wearing Infinity Armor
        if (!stack.isEmpty() && stack.getItem() instanceof InfinityArmorItem) {
            // Count how many pieces of Infinity Armor are worn
            int piecesWorn = 0;
            for (ItemStack armorStack : player.getArmorInventoryList()) {
                if (!armorStack.isEmpty() && armorStack.getItem() instanceof InfinityArmorItem) {
                    piecesWorn++;
                }
            }
            
            // Apply effects based on number of pieces
            if (piecesWorn >= 1) {
                // One piece - basic effects
                player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 40, 0, false, false));
            }
            
            if (piecesWorn >= 2) {
                // Two pieces - better effects
                player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40, 0, false, false));
            }
            
            if (piecesWorn >= 3) {
                // Three pieces - even better effects
                player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 40, 1, false, false));
            }
            
            if (piecesWorn >= 4) {
                // Full set - ultimate effects
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 40, 1, false, false));
                player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 300, 0, false, false));
                player.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 40, 0, false, false));
                
                // Flight-like effect by canceling fall damage
                player.fallDistance = 0.0f;
                
                // Prevent drowning
                if (player.getAir() < 300) {
                    player.setAir(300);
                }
            }
        }
    }
}