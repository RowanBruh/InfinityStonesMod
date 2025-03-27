package com.infinitystones.blocks.traps;

import com.infinitystones.InfinityStonesMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TrapBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
            DeferredRegister.create(ForgeRegistries.BLOCKS, InfinityStonesMod.MOD_ID);

    // Fake Diamond Ore - Looks like diamond ore but explodes when mined
    public static final RegistryObject<Block> FAKE_DIAMOND_ORE = BLOCKS.register("fake_diamond_ore",
            () -> new FakeDiamondOreBlock(AbstractBlock.Properties.create(Material.ROCK)
                    .hardnessAndResistance(3.0F, 3.0F)
                    .setRequiresTool()
                    .sound(SoundType.STONE)));

    // Trap Chest - Opens but damages the player
    public static final RegistryObject<Block> TRAP_CHEST = BLOCKS.register("trap_chest",
            () -> new TrapChestBlock(AbstractBlock.Properties.create(Material.WOOD)
                    .hardnessAndResistance(2.5F)
                    .sound(SoundType.WOOD)));

    // Landmine - Explodes when stepped on
    public static final RegistryObject<Block> LANDMINE = BLOCKS.register("landmine",
            () -> new LandmineBlock(AbstractBlock.Properties.create(Material.IRON)
                    .hardnessAndResistance(0.5F)
                    .sound(SoundType.METAL)
                    .notSolid()));

    // Spike Trap - Damages entities that walk over it
    public static final RegistryObject<Block> SPIKE_TRAP = BLOCKS.register("spike_trap",
            () -> new SpikeTrapBlock(AbstractBlock.Properties.create(Material.IRON)
                    .hardnessAndResistance(2.0F)
                    .sound(SoundType.METAL)
                    .notSolid()));

    // Falling Anvil Trap - Summons falling anvils when triggered
    public static final RegistryObject<Block> FALLING_ANVIL_TRAP = BLOCKS.register("falling_anvil_trap",
            () -> new FallingAnvilTrapBlock(AbstractBlock.Properties.create(Material.IRON)
                    .hardnessAndResistance(1.5F)
                    .sound(SoundType.METAL)));

    // Pitfall Trap - Creates a pit when activated
    public static final RegistryObject<Block> PITFALL_TRAP = BLOCKS.register("pitfall_trap",
            () -> new PitfallTrapBlock(AbstractBlock.Properties.create(Material.EARTH)
                    .hardnessAndResistance(0.5F)
                    .sound(SoundType.GROUND)));

    // Arrow Trap - Shoots arrows when triggered
    public static final RegistryObject<Block> ARROW_TRAP = BLOCKS.register("arrow_trap",
            () -> new ArrowTrapBlock(AbstractBlock.Properties.create(Material.ROCK)
                    .hardnessAndResistance(2.0F)
                    .sound(SoundType.STONE)));

    // Lava Trap - Places lava when triggered
    public static final RegistryObject<Block> LAVA_TRAP = BLOCKS.register("lava_trap",
            () -> new LavaTrapBlock(AbstractBlock.Properties.create(Material.ROCK)
                    .hardnessAndResistance(2.0F)
                    .sound(SoundType.STONE)));

    // TNT Trap - Places and ignites TNT in a ring pattern
    public static final RegistryObject<Block> TNT_TRAP = BLOCKS.register("tnt_trap",
            () -> new TNTTrapBlock(AbstractBlock.Properties.create(Material.TNT)
                    .hardnessAndResistance(0.5F)
                    .sound(SoundType.PLANT)));
}