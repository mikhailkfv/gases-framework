package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.reaction.environment.IGasReactionEnvironment;

public class GasReactionIgnition extends GasReaction
{
	public final GasType match;

	public GasReactionIgnition(GasType match)
	{
		this.match = match;
	}

	@Override
	public void react(IGasReactionEnvironment environment)
	{
		if (environment.getB().gasType == match)
		{
			environment.igniteA();
		}
	}
}
