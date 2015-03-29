package glenn.gasesframework.common.reaction;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.reaction.Reaction;
import glenn.gasesframework.common.block.BlockGas;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ReactionIgnition extends Reaction
{
	public ReactionIgnition()
	{
		super(10);
	}

	@Override
	public int getDelay(World world, int block1X, int block1Y, int block1Z, int block2X, int block2Y, int block2Z)
	{
		Block block1 = world.getBlock(block1X, block1Y, block1Z);
		Block block2 = world.getBlock(block2X, block2Y, block2Z);
		
		if(GasesFrameworkAPI.isIgnitionBlock(block2))
		{
			if(block1 instanceof BlockGas && block1 != GasesFrameworkAPI.gasTypeFire.block)
			{
				return ((BlockGas)block1).type.combustibility.fireSpreadRate;
			}
		}
		else if(GasesFrameworkAPI.isIgnitionBlock(block1))
		{
			if(block2 instanceof BlockGas && block2 != GasesFrameworkAPI.gasTypeFire.block)
			{
				return ((BlockGas)block2).type.combustibility.fireSpreadRate;
			}
		}
		
		return -1;
	}

	@Override
	public boolean isErroneous()
	{
		return false;
	}

	@Override
	public boolean is(World world, Block block1, int block1X, int block1Y, int block1Z, Block block2, int block2X, int block2Y, int block2Z)
	{
		if(GasesFrameworkAPI.isIgnitionBlock(block2))
		{
			return block1 instanceof BlockGas && ((BlockGas)block1).canCombustNormally();
		}
		else if(GasesFrameworkAPI.isIgnitionBlock(block1))
		{
			return block2 instanceof BlockGas && ((BlockGas)block2).canCombustNormally();
		}
		
		return false;
	}

	@Override
	public boolean reactIfPossible(World world, Random random, int block1X, int block1Y, int block1Z, int block2X, int block2Y, int block2Z)
	{
		Block block1 = world.getBlock(block1X, block1Y, block1Z);
		Block block2 = world.getBlock(block2X, block2Y, block2Z);
		
		if(GasesFrameworkAPI.isIgnitionBlock(block2))
		{
			if(block1 instanceof BlockGas && block1 != GasesFrameworkAPI.gasTypeFire.block)
			{
				((BlockGas)block1).onFire(world, block1X, block1Y, block1Z, random, world.getBlockMetadata(block1X, block1Y, block1Z));
				return true;
			}
		}
		else if(GasesFrameworkAPI.isIgnitionBlock(block1))
		{
			if(block2 instanceof BlockGas && block2 != GasesFrameworkAPI.gasTypeFire.block)
			{
				((BlockGas)block2).onFire(world, block2X, block2Y, block2Z, random, world.getBlockMetadata(block2X, block2Y, block2Z));
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean reactBoth(World world, Random random, int block1x, int block1y, int block1z, int block2x, int block2y, int block2z)
	{
		return false;
	}

	@Override
	protected boolean reactBlock1(World world, Random random, int blockX, int blockY, int blockZ)
	{
		return false;
	}

	@Override
	protected boolean reactBlock2(World world, Random random, int blockX, int blockY, int blockZ)
	{
		return false;
	}
}