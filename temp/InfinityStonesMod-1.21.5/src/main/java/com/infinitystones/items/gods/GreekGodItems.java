package com.infinitystones.items.gods;

import com.infinitystones.InfinityStonesMod;
import com.infinitystones.items.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GreekGodItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InfinityStonesMod.MOD_ID);

    // Zeus's Lightning Bolt - Throws lightning at enemies
    public static final RegistryObject<Item> ZEUS_LIGHTNING_BOLT = ITEMS.register("zeus_lightning_bolt",
            () -> new ZeusLightningBolt(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Poseidon's Trident - Controls water and summons water-based attacks
    public static final RegistryObject<Item> POSEIDON_TRIDENT = ITEMS.register("poseidon_trident",
            () -> new PoseidonTrident(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Hades' Bident - Controls undead mobs and creates soul flame
    public static final RegistryObject<Item> HADES_BIDENT = ITEMS.register("hades_bident",
            () -> new HadesBident(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Artemis' Bow - Superior ranged weapon with special arrows
    public static final RegistryObject<Item> ARTEMIS_BOW = ITEMS.register("artemis_bow",
            () -> new ArtemisBow(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Apollo's Lyre - Heals allies and damages undead
    public static final RegistryObject<Item> APOLLO_LYRE = ITEMS.register("apollo_lyre",
            () -> new ApolloLyre(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Athena's Spear - Grants wisdom (XP) and has shield capability
    public static final RegistryObject<Item> ATHENA_SPEAR = ITEMS.register("athena_spear",
            () -> new AthenaSpear(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Ares' Sword - Deals massive damage and causes bloodlust effect
    public static final RegistryObject<Item> ARES_SWORD = ITEMS.register("ares_sword",
            () -> new AresSword(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));

    // Hermes' Winged Boots - Grants speed and jump boost
    public static final RegistryObject<Item> HERMES_BOOTS = ITEMS.register("hermes_boots",
            () -> new HermesBoots(new Item.Properties()
                    .stacksTo(1)
                    .durability(1000)
                    .rarity(Rarity.EPIC)));
    
    // Event handler to populate tabs
    public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModItems.INFINITY_STONES_TAB.get()) {
            // Add Greek God items to Infinity Stones tab
            event.accept(ZEUS_LIGHTNING_BOLT.get());
            event.accept(POSEIDON_TRIDENT.get());
            event.accept(HADES_BIDENT.get());
            event.accept(ARTEMIS_BOW.get());
            event.accept(APOLLO_LYRE.get());
            event.accept(ATHENA_SPEAR.get());
            event.accept(ARES_SWORD.get());
            event.accept(HERMES_BOOTS.get());
        }
    }
    
    // Register items and event handlers
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(GreekGodItems::buildCreativeModeTabs);
    }
}