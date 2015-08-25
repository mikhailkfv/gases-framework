package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasPump extends TileEntityGasPump
{
	public TileEntityIronGasPump()
	{
		super(GasesFramework.configurations.piping.ironMaterial.pumpRate);
	}
}
