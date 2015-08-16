package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

/**
 * A filter that will accept null, air, or any gas type that is not in a list.
 * @author Erlend
 *
 */
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
		return super.accept(gasType) || !filterTypes.contains(gasType);
	}
}
