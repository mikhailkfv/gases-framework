package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;

/**
 * Abstract base class for reactions between a gas and a block.
 */
public abstract class BlockReaction extends Reaction
{
	/**
	 * Do what is necessary for this reaction to act.
	 * @param environment
	 */
	public abstract void react(IBlockReactionEnvironment environment);
}
