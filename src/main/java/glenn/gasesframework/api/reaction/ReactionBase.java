package glenn.gasesframework.api.reaction;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class ReactionBase extends Reaction
{
	protected final Block reactionBlock1;
	protected final Block reactionBlock2;
	
	/**
	 * Constructs a new reaction between the specified blocks. One of the blocks should be a {@link glenn.gasesframework.common.block.BlockGas}, otherwise the reaction will never occur
	 * @param priority - The priority of this reaction. If a gas touches several blocks it may react with, it will priorify the reaction of the greatest priority. For example, an ignition reaction uses priority level 10
	 * @param delay - The delay of the reaction. If set below 0, it will happen at the same rate as the flow of the block
	 * @param block1
	 * @param block2
	 */
	public ReactionBase(int priority, Block block1, Block block2)
	{
		super(priority);
		this.reactionBlock1 = block1;
		this.reactionBlock2 = block2;
	}
	
	/**
	 * Returns true if this reaction is erroneous and cannot be used
	 * @return
	 */
	@Override
	public boolean isErroneous()
	{
		return this.reactionBlock1 == this.reactionBlock2;
	}
	
	@Override
	public boolean is(World world, Block block1, int block1X, int block1Y, int block1Z, Block block2, int block2X, int block2Y, int block2Z)
	{
		return (block1 == reactionBlock1 & block2 == reactionBlock2) | (block1 == reactionBlock2 & block2 == reactionBlock1);
	}
}
