package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

public class GasTypeFilterClosed extends GasTypeFilterSimple
{
	public static final byte TYPE = 5;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}

	@Override
	public boolean accept(GasType gasType)
	{
		return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir;
	}

	@Override
	public boolean equals(GasTypeFilterSimple other)
	{
		return other instanceof GasTypeFilterClosed;
	}
}