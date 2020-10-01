package com.jishunamatata.floraresourca.common.blocks;

import java.util.Random;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class DoubleCropBlock extends DoublePlantBlock implements IWaterLoggable, IGrowable {

	private static final IntegerProperty STAGE = BlockStateProperties.AGE_0_7;
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	@SuppressWarnings("unchecked")
	protected static final Pair<VoxelShape, VoxelShape>[] BLOCK_SHAPES = new Pair[] {
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 0.2, 1), VoxelShapes.create(0, 0, 0, 0, 0, 0)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 0.5, 1), VoxelShapes.create(0, 0, 0, 0, 0, 0)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 0.8, 1), VoxelShapes.create(0, 0, 0, 0, 0, 0)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 1, 1), VoxelShapes.create(0, 0, 0, 0, 0, 0)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 1, 1), VoxelShapes.create(0, 0, 0, 1, 0.2, 1)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 1, 1), VoxelShapes.create(0, 0, 0, 1, 0.5, 1)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 1, 1), VoxelShapes.create(0, 0, 0, 1, 0.8, 1)),
			new Pair<>(VoxelShapes.create(0, 0, 0, 1, 1, 1), VoxelShapes.create(0, 0, 0, 1, 1, 1)) };

	public DoubleCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.NONE;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(STAGE, WATERLOGGED);
	}

	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!player.abilities.allowEdit) {
			return ActionResultType.PASS;
		}

		if (state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			pos = pos.down();
			state = worldIn.getBlockState(pos);

			if (state.getBlock() != this)
				return ActionResultType.PASS;
		}
		if (state.get(STAGE) != getMaxAge()) {
			return ActionResultType.PASS;
		}

		worldIn.setBlockState(pos, this.withAge(0));
		worldIn.setBlockState(pos.up(), this.withAge(0).with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, false), 2);
		return ActionResultType.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		DoubleBlockHalf half = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF);

		if (half == DoubleBlockHalf.LOWER) {
			return BLOCK_SHAPES[state.get(STAGE)].getFirst();
		} else {
			BlockState downState = worldIn.getBlockState(pos.down());
			if (downState.getBlock() instanceof DoubleCropBlock) {
				return BLOCK_SHAPES[downState.get(STAGE)].getSecond();
			} else {
				return BLOCK_SHAPES[state.get(STAGE)].getSecond();
			}
		}

	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
			return isSourceBlock(worldIn, pos) && super.isValidPosition(state, worldIn, pos);
		}
		return super.isValidPosition(state, worldIn, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();

		return isSourceBlock(world, pos) && !isSourceBlock(world, pos.up())
				? this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, true)
				: null;

	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(),
				this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, false), 3);
	}

	@Override
	public void placeAt(IWorld worldIn, BlockPos pos, int flags) {
		worldIn.setBlockState(pos, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER).with(STAGE, 7), flags);
		worldIn.setBlockState(pos.up(),
				this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, false).with(STAGE, 7),
				flags);
	}

	public boolean isSourceBlock(IWorldReader worldIn, BlockPos pos) {
		FluidState fluid = worldIn.getFluidState(pos);
		return fluid.isTagged(FluidTags.WATER) && fluid.getLevel() == 8;
	}

	public IntegerProperty getAgeProperty() {
		return STAGE;
	}

	public int getMaxAge() {
		return 7;
	}

	protected int getAge(BlockState state) {
		return state.get(this.getAgeProperty());
	}

	public BlockState withAge(int age) {
		return this.getDefaultState().with(this.getAgeProperty(), Integer.valueOf(age));
	}

	public boolean isMaxAge(BlockState state) {
		return state.get(this.getAgeProperty()) >= this.getMaxAge();
	}

	@Override
	public boolean ticksRandomly(BlockState state) {
		return !this.isMaxAge(state) && state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (!worldIn.isAreaLoaded(pos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light

		int age = this.getAge(state);
		if (age < this.getMaxAge()) {
			if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt(10) == 0)) {
				grow(worldIn, random, pos, state);
				ForgeHooks.onCropsGrowPost(worldIn, pos, state);
			}
		}
	}

	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isMaxAge(state);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld worldIn, Random random, BlockPos pos, BlockState state) {
		if (state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
			pos = pos.down();
			state = worldIn.getBlockState(pos);

			if (state.getBlock() != this)
				return;
		}
		int age = Math.min(this.getAge(state) + 1, this.getMaxAge());
		worldIn.setBlockState(pos, this.withAge(age), 2);
		worldIn.setBlockState(pos.up(), this.withAge(age).with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, false),
				2);
	}

}
