package glenn.gasesframework.common.reaction;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.reaction.BlockReaction;
import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;

public class ReactionCommonIgnition extends BlockReaction
{
	@Override
	public void react(IBlockReactionEnvironment environment)
	{
		if (GasesFramework.registry.isIgnitionBlock(environment.getB()))
		{
			environment.igniteA();
		}
	}
}