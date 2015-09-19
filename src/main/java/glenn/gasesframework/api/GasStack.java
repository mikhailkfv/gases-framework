package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;

/**
 * A utility class to represent an amount of a specific gas.
 */
public class GasStack
{
	public GasType gasType;
	public int amount;

	public GasStack(GasType gasType)
	{
		this(gasType, 1);
	}

	public GasStack(GasType gasType, int amount)
	{
		this.gasType = gasType;
		this.amount = amount;
	}
}
