package glenn.gasesframework.api.reaction.environment;

import net.minecraft.block.Block;

public interface IBlockReactionEnvironment extends IReactionEnvironment
{
	/**
	 * Get B.
	 * 
	 * @return
	 */
	Block getB();

	/**
	 * Get the metadata of B.
	 * 
	 * @return
	 */
	int getBMetadata();

	/**
	 * Set B.
	 * 
	 * @param b
	 */
	void setB(Block b);

	/**
	 * Set B and its metadata.
	 * 
	 * @param b
	 * @param metadata
	 */
	void setB(Block b, int metadata);

	/**
	 * Break and drop B.
	 */
	void breakB();

	/**
	 * Get the block hardness of B.
	 * 
	 * @return
	 */
	float getBHardness();
}
