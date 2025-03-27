package com.infinitystones.items.gods;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class GreekGodItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);

    // Zeus's Lightning Bolt - Throws lightning at enemies
    public static final RegistryObject<Item> ZEUS_LIGHTNING_BOLT = ITEMS.register("zeus_lightning_bolt",
            () -> new ZeusLightningBolt(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Poseidon's Trident - Controls water and summons water-based attacks
    public static final RegistryObject<Item> POSEIDON_TRIDENT = ITEMS.register("poseidon_trident",
            () -> new PoseidonTrident(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Hades' Bident - Controls undead mobs and creates soul flame
    public static final RegistryObject<Item> HADES_BIDENT = ITEMS.register("hades_bident",
            () -> new HadesBident(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Artemis' Bow - Superior ranged weapon with special arrows
    public static final RegistryObject<Item> ARTEMIS_BOW = ITEMS.register("artemis_bow",
            () -> new ArtemisBow(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Apollo's Lyre - Heals allies and damages undead
    public static final RegistryObject<Item> APOLLO_LYRE = ITEMS.register("apollo_lyre",
            () -> new ApolloLyre(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Athena's Spear - Grants wisdom (XP) and has shield capability
    public static final RegistryObject<Item> ATHENA_SPEAR = ITEMS.register("athena_spear",
            () -> new AthenaSpear(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Ares' Sword - Deals massive damage and causes bloodlust effect
    public static final RegistryObject<Item> ARES_SWORD = ITEMS.register("ares_sword",
            () -> new AresSword(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));

    // Hermes' Winged Boots - Grants speed and jump boost
    public static final RegistryObject<Item> HERMES_BOOTS = ITEMS.register("hermes_boots",
            () -> new HermesBoots(new Item.Properties()
                    .group(ModItems.INFINITY_STONES_TAB)
                    .maxStackSize(1)
                    .maxDamage(1000)
                    .rarity(Rarity.EPIC)));
}