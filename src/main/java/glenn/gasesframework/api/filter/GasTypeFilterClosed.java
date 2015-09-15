package glenn.gasesframework.api.filter;

/**
 * A filter that does not accept any gas type except air or null.
 * @author Erlend
 */
public class GasTypeFilterClosed extends GasTypeFilterSimple
{
	public static final byte TYPE = 5;
	
	@Override
	public byte getType()
	{
		return TYPE;
	}

	@Override
	public boolean equals(GasTypeFilterSimple other)
	{
		return other instanceof GasTypeFilterClosed;
	}
}