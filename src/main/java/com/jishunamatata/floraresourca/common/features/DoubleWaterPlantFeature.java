package com.jishunamatata.floraresourca.common.features;

import java.util.Random;

import com.jishunamatata.floraresourca.core.FluidUtils;
import com.mojang.serialization.Codec;

import net.minecraft.block.DoublePlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraftforge.common.Tags;

public class DoubleWaterPlantFeature extends Feature<BlockClusterFeatureConfig> {
	public DoubleWaterPlantFeature(Codec<BlockClusterFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean func_230362_a_(ISeedReader worldIn, StructureManager manager, ChunkGenerator generator, Random rand,
			BlockPos pos, BlockClusterFeatureConfig config) {
		boolean flag = false;
		pos = worldIn.getHeight(Type.WORLD_SURFACE_WG, pos);

		for (int i = 0; i < 64; ++i) {
			BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4),
					rand.nextInt(8) - rand.nextInt(8));
			if (FluidUtils.isSourceBlock(worldIn, blockpos) && !FluidUtils.isSourceBlock(worldIn, blockpos.up())
					&& blockpos.getY() < worldIn.getWorld().getHeight() - 2
					&& Tags.Blocks.DIRT.contains(worldIn.getBlockState(blockpos.down()).getBlock())) {
				((DoublePlantBlock) config.stateProvider.getBlockState(rand, pos).getBlock()).placeAt(worldIn, blockpos,
						2);
				flag = true;
			}
		}
		return flag;
	}
}
