package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

public class GasTypeFilterSingleIncluding extends GasTypeFilterSingle
{
	public static final byte TYPE = 1;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}
	
	public GasTypeFilterSingleIncluding(GasType filterType)
	{
		super(filterType);
	}

	@Override
	public boolean accept(GasType gasType)
	{
		return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir || gasType == filterType;
	}

	@Override
	public boolean equals(GasTypeFilterSimple other)
	{
		if (other instanceof GasTypeFilterSingleIncluding)
		{
			return ((GasTypeFilterSingleIncluding)other).filterType == filterType;
		}
		else
		{
			return false;
		}
	}

	@Override
	public GasTypeFilterMulti toMulti()
	{
		return new GasTypeFilterMultiIncluding(new GasType[] { filterType });
	}
}
