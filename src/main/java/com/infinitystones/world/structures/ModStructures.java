package com.infinitystones.world.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.infinitystones.InfinityStonesMod;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {
    // Register structures 
    public static final DeferredRegister<Structure<?>> STRUCTURES = 
            DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, InfinityStonesMod.MOD_ID);

    // Register each structure
    public static final RegistryObject<Structure<NoFeatureConfig>> BIONIC_BASE = 
            STRUCTURES.register("bionic_base", 
            () -> new BionicBaseStructure(NoFeatureConfig.field_236558_a_));
            
    public static final RegistryObject<Structure<NoFeatureConfig>> SKIDDZIE_BASE = 
            STRUCTURES.register("skiddzie_base", 
            () -> new SkiddzieBaseStructure(NoFeatureConfig.field_236558_a_));
            
    public static final RegistryObject<Structure<NoFeatureConfig>> SKEPPY_ARENA = 
            STRUCTURES.register("skeppy_arena", 
            () -> new SkeppyArenaStructure(NoFeatureConfig.field_236558_a_));
    
    // Structure spawn frequency settings
    private static final Map<Structure<?>, StructureSeparationSettings> STRUCTURE_SETTINGS = 
            new HashMap<Structure<?>, StructureSeparationSettings>();
    
    public static void setupStructures() {
        // Setup each structure with its spawn settings
        setupStructure(BIONIC_BASE.get(), 
                      new StructureSeparationSettings(50, // Average distance apart in chunks
                                                   25,  // Minimum distance apart in chunks
                                                   112358132), // Random seed
                      true);
                      
        setupStructure(SKIDDZIE_BASE.get(), 
                      new StructureSeparationSettings(46, // Average distance apart in chunks
                                                   24,  // Minimum distance apart in chunks
                                                   987654321), // Random seed
                      true);
                      
        setupStructure(SKEPPY_ARENA.get(), 
                      new StructureSeparationSettings(80, // Average distance apart in chunks
                                                   40,  // Minimum distance apart in chunks
                                                   135792468), // Random seed
                      true);
    }
    
    /**
     * Register structure with appropriate settings
     */
    private static <F extends Structure<?>> void setupStructure(F structure, 
                                                             StructureSeparationSettings settings,
                                                             boolean transformSurroundingLand) {
        // Add to structure settings
        STRUCTURE_SETTINGS.put(structure, settings);
        
        // Add to structure features
        if (transformSurroundingLand) {
            Structure.field_236384_t_ = 
                    ImmutableList.<Structure<?>>builder()
                            .addAll(Structure.field_236384_t_)
                            .add(structure)
                            .build();
        }
        
        // Add to NOISE_AFFECTING_FEATURES
        Structure.field_236365_a_.put(structure.getRegistryName().toString(), structure);
    }
    
    /**
     * Add our structures to the structure settings map
     */
    public static void addStructuresToSettings() {
        // Get structure settings
        Map<Structure<?>, StructureSeparationSettings> tempMap = 
                new HashMap<>(WorldGenRegistries.NOISE_GENERATOR_SETTINGS
                             .getValueForKey(DimensionStructuresSettings.field_236191_b_)
                             .getStructures().func_236195_a_());
        
        // Add our structures to the map
        tempMap.putAll(STRUCTURE_SETTINGS);
        
        // Replace original map with our map containing new structures
        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.getValueForKey(DimensionStructuresSettings.field_236191_b_)
                          .getStructures().field_236193_d_ = tempMap;
    }
    
    public static void register(IEventBus eventBus) {
        STRUCTURES.register(eventBus);
    }
}