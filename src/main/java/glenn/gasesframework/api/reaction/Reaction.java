package glenn.gasesframework.api.reaction;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class Reaction
{
	public final int priority;
	
	public Reaction(int priority)
	{
		this.priority = priority;
	}
	
	/**
	 * Returns true if this reaction is erroneous and cannot be used
	 * @return
	 */
	public abstract boolean isErroneous();
	
	/**
	 * Gets the delay of the reaction. If the delay is -1, it will happen at the speed of the gas flow.
	 * @return
	 */
	public int getDelay(World world, int block1X, int block1Y, int block1Z, int block2X, int block2Y, int block2Z)
	{
		return -1;
	}
	
	public abstract boolean is(World world, Block block1, int block1X, int block1Y, int block1Z, Block block2, int block2X, int block2Y, int block2Z);
	
	public boolean reactIfPossible(World world, Random random, int block1X, int block1Y, int block1Z, int block2X, int block2Y, int block2Z)
	{
		if(isErroneous())
		{
			return false;
		}
		
		Block block1 = world.getBlock(block1X, block1Y, block1Z);
		Block block2 = world.getBlock(block2X, block2Y, block2Z);
		
		if(is(world, block1, block1X, block1Y, block1Z, block2, block2X, block2Y, block2Z))
		{
			return reactBoth(world, random, block1X, block1Y, block1Z, block2X, block2Y, block2Z) | reactBlock1(world, random, block1X, block1Y, block1Z) | reactBlock2(world, random, block2X, block2Y, block2Z);
		}
		
		return false;
	}
	
	/**
	 * The part of the reaction that encompasses both blocks at the same time. Should return true if anything happens
	 * @param world
	 * @param random
	 * @param block1X
	 * @param block1Y
	 * @param block1Z
	 * @param block2X
	 * @param block2Y
	 * @param block2Z
	 */
	protected abstract boolean reactBoth(World world, Random random, int block1X, int block1Y, int block1Z, int block2X, int block2Y, int block2Z);
	
	/**
	 * The part of the reaction that encompasses the first block passed in the constructor. Should return true if anything happens
	 * @param world
	 * @param random
	 * @param blockX
	 * @param blockY
	 * @param blockZ
	 */
	protected abstract boolean reactBlock1(World world, Random random, int blockX, int blockY, int blockZ);
	
	/**
	 * The part of the reaction that encompasses the second block passed in the constructor. Should return true if anything happens
	 * @param world
	 * @param random
	 * @param blockX
	 * @param blockY
	 * @param blockZ
	 */
	protected abstract boolean reactBlock2(World world, Random random, int blockX, int blockY, int blockZ);
}