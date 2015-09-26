package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.gastype.GasType;

/**
 * A filter that accepts any gas type.
 */
public class GasTypeFilterOpen extends GasTypeFilterSimple
{
	public static final byte TYPE = 0;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}

	@Override
	public boolean accept(GasType gasType)
	{
		return true;
	}

	@Override
	public boolean equals(GasTypeFilterSimple other)
	{
		return other instanceof GasTypeFilterOpen;
	}
}
