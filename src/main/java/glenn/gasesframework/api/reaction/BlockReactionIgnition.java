package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;
import net.minecraft.block.Block;

/**
 * Reaction for ignition of a gas against a specific block.
 */
public class BlockReactionIgnition extends BlockReaction
{
	public final Block match;

	public BlockReactionIgnition(Block match)
	{
		this.match = match;
	}

	@Override
	public void react(IBlockReactionEnvironment environment)
	{
		if (environment.getB() == match)
		{
			environment.igniteA();
		}
	}
}
