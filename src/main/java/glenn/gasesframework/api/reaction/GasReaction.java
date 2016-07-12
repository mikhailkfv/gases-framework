package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IGasReactionEnvironment;

/**
 * Abstract base class for reactions between gases.
 */
public abstract class GasReaction extends Reaction
{
	/**
	 * Do what is necessary for this reaction to act.
	 * 
	 * @param environment
	 *            The reaction environment
	 */
	public abstract void react(IGasReactionEnvironment environment);
}
