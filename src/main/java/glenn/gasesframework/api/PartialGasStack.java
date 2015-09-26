package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;

/**
 * A utility class to represent a partial amount of a specific gas.
 */
public class PartialGasStack
{
	/**
	 * The gas type contained in this partial gas stack
	 */
	public GasType gasType;
	/**
	 * The partial amount of the gas type in this partial gas stack
	 */
	public int partialAmount;

	public PartialGasStack(GasType gasType)
	{
		this(gasType, 16);
	}

	public PartialGasStack(GasType gasType, int partialAmount)
	{
		this.gasType = gasType;
		this.partialAmount = partialAmount;
	}

	/**
	 * Is this gas stack considered empty?
	 * @return True if the gas stack is considered empty
	 */
	public boolean isEmpty()
	{
		return gasType == null || partialAmount <= 0;
	}
}
