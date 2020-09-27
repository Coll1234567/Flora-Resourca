package com.jishunamatata.floraresourca.core;

import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FluidUtils {

	public static boolean isSourceBlock(IWorld worldIn, BlockPos pos) {
		FluidState fluid = worldIn.getFluidState(pos);
		return fluid.isTagged(FluidTags.WATER) && fluid.getLevel() == 8;
	}

}
