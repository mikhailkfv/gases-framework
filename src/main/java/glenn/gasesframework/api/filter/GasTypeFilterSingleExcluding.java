package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

/**
 * A filter that only accepts null, air or any gas type that is not a specific gas type.
 * @author Erlend
 *
 */
public class GasTypeFilterSingleExcluding extends GasTypeFilterSingle
{
	public static final byte TYPE = 2;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}

	public GasTypeFilterSingleExcluding(GasType filterType)
	{
		super(filterType);
	}

	@Override
	public boolean accept(GasType gasType)
	{
		return super.accept(gasType) || gasType != filterType;
	}

	@Override
	public boolean equals(GasTypeFilterSimple other)
	{
		if (other instanceof GasTypeFilterSingleExcluding)
		{
			return ((GasTypeFilterSingleExcluding)other).filterType == filterType;
		}
		else
		{
			return false;
		}
	}

	@Override
	public GasTypeFilterMulti toMulti()
	{
		return new GasTypeFilterMultiExcluding(new GasType[] { filterType });
	}
}
