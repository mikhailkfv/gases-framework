package glenn.gasesframework.api.reaction;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ReactionBlockReplacement extends ReactionBase
{
	protected Block block1New;
	protected int block1NewMetadata;
	protected Block block2New;
	protected int block2NewMetadata;
	
	public ReactionBlockReplacement(int priority, Block block1, Block block2, Block block1New, int block1NewMetadata, Block block2New, int block2NewMetadata)
	{
		super(priority, block1, block2);
		this.block1New = block1New;
		this.block1NewMetadata = block1NewMetadata;
		this.block2New = block2New;
		this.block2NewMetadata = block2NewMetadata;
	}
	
	public ReactionBlockReplacement(int priority, Block block1, Block block2, Block block1New, Block block2New)
	{
		super(priority, block1, block2);
		this.block1New = block1New;
		this.block1NewMetadata = 0;
		this.block2New = block2New;
		this.block2NewMetadata = 0;
	}

	@Override
	protected boolean reactBoth(World world, Random random, int block1x, int block1y, int block1z, int block2x, int block2y, int block2z)
	{
		return false;
	}

	@Override
	protected boolean reactBlock1(World world, Random random, int blockX, int blockY, int blockZ)
	{
		if(block1New != null)
		{
			world.setBlock(blockX, blockY, blockZ, block1New, block1NewMetadata, 3);
			return true;
		}
		return false;
	}

	@Override
	protected boolean reactBlock2(World world, Random random, int blockX, int blockY, int blockZ)
	{
		if(block2New != null)
		{
			world.setBlock(blockX, blockY, blockZ, block2New, block1NewMetadata, 3);
			return true;
		}
		return false;
	}
}