package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;

/**
 * Abstract base class for reactions between a gas and a block.
 * Happens when a gas and a block are in contact.
 */
public abstract class BlockReaction extends Reaction
{
	/**
	 * Do what is necessary for this reaction to act.
	 * @param environment The reaction environment
	 */
	public abstract void react(IBlockReactionEnvironment environment);
}
