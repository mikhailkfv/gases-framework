package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasCollector extends TileEntityGasCollector
{
	public TileEntityIronGasCollector()
	{
		super(GasesFramework.configurations.piping.ironMaterial.pumpRate, GasesFramework.configurations.piping.ironMaterial.collectionRange);
	}
}
