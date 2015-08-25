package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityWoodGasPump extends TileEntityGasPump
{
	public TileEntityWoodGasPump()
	{
		super(GasesFramework.configurations.piping.woodMaterial.pumpRate);
	}
}
