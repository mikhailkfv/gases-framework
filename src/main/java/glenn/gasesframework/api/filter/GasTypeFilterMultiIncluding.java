package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

public class GasTypeFilterMultiIncluding extends GasTypeFilterMulti
{
	public static final byte TYPE = 3;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}
	
	public GasTypeFilterMultiIncluding(GasType[] filterTypes)
	{
		super(filterTypes);
	}

	@Override
	public boolean accept(GasType gasType)
	{
		return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir || filterTypes.contains(gasType);
	}
}
