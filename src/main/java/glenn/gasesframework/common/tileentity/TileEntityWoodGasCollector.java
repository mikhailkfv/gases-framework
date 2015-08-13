package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityWoodGasCollector extends TileEntityGasCollector
{
	public TileEntityWoodGasCollector()
	{
		super(GasesFramework.configurations.piping.woodMaterial.pumpRate, GasesFramework.configurations.piping.woodMaterial.collectionRange);
	}
}
