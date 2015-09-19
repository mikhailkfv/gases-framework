package glenn.gasesframework.api.reaction.environment;

import glenn.gasesframework.api.PartialGasStack;

public interface IGasReactionEnvironment extends IReactionEnvironment
{
	/**
	 * Get B as a PartialGasStack.
	 * @return
	 */
	PartialGasStack getB();

	/**
	 * Set B as a PartialGasStack.
	 * @param b
	 */
	void setB(PartialGasStack b);

	/**
	 * Ignite B.
	 */
	void igniteB();
}
