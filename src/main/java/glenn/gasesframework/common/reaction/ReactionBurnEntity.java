package glenn.gasesframework.common.reaction;

import glenn.gasesframework.api.reaction.EntityReaction;
import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;

public class ReactionBurnEntity extends EntityReaction
{
	@Override
	public void react(IEntityReactionEnvironment environment)
	{
		environment.getB().setFire(5);
	}
}
