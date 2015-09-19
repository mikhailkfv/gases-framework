package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;

public abstract class EntityReaction extends Reaction
{
	public abstract void react(IEntityReactionEnvironment environment);
}
