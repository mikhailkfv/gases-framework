package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.gastype.GasType;

/**
 * A filter that will accept null, air, or any gas type that is in a list.
 */
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
		return super.accept(gasType) || filterTypes.contains(gasType);
	}
}
