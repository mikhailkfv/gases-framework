package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;
import net.minecraft.block.Block;

public class BlockReactionReplaceBlock extends BlockReaction
{
	public final Block replaceBlock;
	public final Block replacementBlock;

	public BlockReactionReplaceBlock(Block replaceBlock, Block replacementBlock)
	{
		this.replaceBlock = replaceBlock;
		this.replacementBlock = replacementBlock;
	}

	@Override
	public void react(IBlockReactionEnvironment environment)
	{
		if (environment.getB() == replaceBlock)
		{
			environment.setB(replacementBlock);
		}
	}
}
