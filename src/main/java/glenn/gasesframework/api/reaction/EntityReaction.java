package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;

/**
 * Abstract base class for reactions between a gas and an entity.
 * Happens when a gas and an entity are in contact.
 */
public abstract class EntityReaction extends Reaction
{
	/**
	 * Do what is necessary for this reaction to act.
	 * @param environment The reaction environment
	 */
	public abstract void react(IEntityReactionEnvironment environment);
}
