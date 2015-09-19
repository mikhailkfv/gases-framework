package glenn.gasesframework.api.reaction.environment;

import net.minecraft.block.Block;

public interface IBlockReactionEnvironment extends IReactionEnvironment
{
	Block getB();

	int getBMetadata();

	void setB(Block b);

	void setB(Block b, int metadata);

	void breakB();

	float getBHardness();
}
