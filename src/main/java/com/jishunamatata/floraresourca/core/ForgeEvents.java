package com.jishunamatata.floraresourca.core;

import com.jishunamatata.floraresourca.common.features.DoubleWaterPlantFeature;
import com.jishunamatata.floraresourca.core.registry.FloraResourcaBlocks;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(bus = Bus.FORGE)
public class ForgeEvents {
	private static ConfiguredFeature<?, ?> CATTAIL_FEAUTRE;
	private static Feature<BlockClusterFeatureConfig> WATER_PATCH = new DoubleWaterPlantFeature(
			BlockClusterFeatureConfig.field_236587_a_);

	@SubscribeEvent
	public static void serverStart(FMLServerStartingEvent event) {
		CATTAIL_FEAUTRE = WATER_PATCH.withConfiguration(new BlockClusterFeatureConfig.Builder(
				new SimpleBlockStateProvider(FloraResourcaBlocks.COAL_CATTAIL.get().getDefaultState()),
				new DoublePlantBlockPlacer()).replaceable().tries(64).requiresWater().build());

		for (Biome biome : ForgeRegistries.BIOMES) {
			if (BiomeDictionary.hasType(biome, Type.OVERWORLD)
					&& (BiomeDictionary.hasType(biome, Type.SWAMP) || BiomeDictionary.hasType(biome, Type.RIVER)))
				Biomes.SWAMP.addFeature(Decoration.VEGETAL_DECORATION, CATTAIL_FEAUTRE);
		}
	}

}
