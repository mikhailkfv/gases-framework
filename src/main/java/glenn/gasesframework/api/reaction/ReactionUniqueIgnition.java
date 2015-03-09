package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.GasesFrameworkAPI;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ReactionUniqueIgnition extends ReactionBase
{
	public ReactionUniqueIgnition(int priority, Block gasBlock, Block ignitionBlock)
	{
		super(priority, gasBlock, ignitionBlock);
	}
	
	@Override
	public int getDelay(World world, int block1X, int block1Y, int block1Z, int block2X, int block2Y, int block2Z)
	{
		return 0;
	}

	@Override
	protected boolean reactBoth(World world, Random random, int block1x, int block1y, int block1z, int block2x, int block2y, int block2z)
	{
		return false;
	}

	@Override
	protected boolean reactBlock1(World world, Random random, int blockX, int blockY, int blockZ)
	{
		Block block = world.getBlock(blockX, blockY, blockZ);
		if(block == reactionBlock1)
		{
			GasesFrameworkAPI.ignite(world, blockX, blockY, blockZ, random);
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean reactBlock2(World world, Random random, int blockX, int blockY, int blockZ)
	{
		Block block = world.getBlock(blockX, blockY, blockZ);
		if(block == reactionBlock1)
		{
			GasesFrameworkAPI.ignite(world, blockX, blockY, blockZ, random);
			return true;
		}
		
		return false;
	}
}