package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.gastype.GasType;

/**
 * Reaction for mixing two specific gases.
 */
public class GasReactionMix extends GasReactionMixBase
{
	public final GasType typeA;
	public final GasType typeB;
	public final GasType mixedType;

	public GasReactionMix(GasType typeA, GasType typeB, GasType mixedType)
	{
		this.typeA = typeA;
		this.typeB = typeB;
		this.mixedType = mixedType;
	}

	@Override
	public GasType mix(GasType typeA, GasType typeB)
	{
		if((this.typeA == typeA && this.typeB == typeB) || (this.typeB == typeA && this.typeA == typeB))
		{
			return mixedType;
		}
		else
		{
			return null;
		}
	}
}
