package glenn.gasesframework.common.reaction.environment;

import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldBlockReactionEnvironment extends WorldReactionEnvironment implements IBlockReactionEnvironment
{
	public WorldBlockReactionEnvironment(World world, int ax, int ay, int az, int bx, int by, int bz)
	{
		super(world, ax, ay, az, bx, by, bz);
	}

	@Override
	public Block getB()
	{
		return world.getBlock(bx, by, bz);
	}

	@Override
	public int getBMetadata()
	{
		return world.getBlockMetadata(bx, by, bz);
	}

	@Override
	public void setB(Block b)
	{
		world.setBlock(bx, by, bz, b);
	}

	@Override
	public void setB(Block b, int metadata)
	{
		world.setBlock(bx, by, bz, b, metadata, 3);
	}

	@Override
	public void breakB()
	{
		Block b = world.getBlock(bx, by, bz);
		b.dropBlockAsItem(world, bx, by, bz, world.getBlockMetadata(bx, by, bz), 0);
		world.setBlockToAir(bx, by, bz);
	}
}
