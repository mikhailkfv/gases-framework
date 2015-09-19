package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.PartialGasStack;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.reaction.environment.IGasReactionEnvironment;

public abstract class GasReactionMixBase extends GasReaction
{
	@Override
	public void react(IGasReactionEnvironment environment)
	{
		PartialGasStack a = environment.getA();
		PartialGasStack b = environment.getB();

		GasType mixType = mix(a.gasType, b.gasType);
		if (mixType != null)
		{
			environment.setA(new PartialGasStack(mixType, a.partialAmount));
			environment.setB(new PartialGasStack(mixType, b.partialAmount));
		}
	}

	public abstract GasType mix(GasType typeA, GasType typeB);
}