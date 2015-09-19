package glenn.gasesframework.api.reaction.environment;

import glenn.gasesframework.api.PartialGasStack;

public interface IGasReactionEnvironment extends IReactionEnvironment
{
	PartialGasStack getB();

	void setB(PartialGasStack b);

	void igniteB();
}
