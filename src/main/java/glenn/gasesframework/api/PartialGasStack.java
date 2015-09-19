package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;

/**
 * A utility class to represent a partial amount of a specific gas.
 */
public class PartialGasStack
{
	public GasType gasType;
	public int partialAmount;

	public PartialGasStack(GasType gasType)
	{
		this(gasType, 1);
	}

	public PartialGasStack(GasType gasType, int partialAmount)
	{
		this.gasType = gasType;
		this.partialAmount = partialAmount;
	}

	public boolean isEmpty()
	{
		return gasType == null || partialAmount <= 0;
	}
}
