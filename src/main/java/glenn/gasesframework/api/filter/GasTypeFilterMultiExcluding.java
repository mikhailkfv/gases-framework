package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

public class GasTypeFilterMultiExcluding extends GasTypeFilterMulti
{
	public static final byte TYPE = 4;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}
	
	public GasTypeFilterMultiExcluding(GasType[] filterTypes)
	{
		super(filterTypes);
	}

	@Override
	public boolean accept(GasType gasType)
	{
		return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir || !filterTypes.contains(gasType);
	}
}
