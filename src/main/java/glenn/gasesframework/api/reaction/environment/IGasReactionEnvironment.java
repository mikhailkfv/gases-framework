package glenn.gasesframework.api.reaction.environment;

import glenn.gasesframework.api.PartialGasStack;

public interface IGasReactionEnvironment extends IReactionEnvironment
{
	/**
	 * Get B as a partial gas stack.
	 * @return B as a partial gas stack
	 */
	PartialGasStack getB();

	/**
	 * Set B as a partial gas stack.
	 * @param b A partial gas stack
	 */
	void setB(PartialGasStack b);

	/**
	 * Ignite B.
	 */
	void igniteB();
}
