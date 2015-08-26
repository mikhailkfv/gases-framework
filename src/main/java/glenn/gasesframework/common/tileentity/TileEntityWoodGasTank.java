package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityWoodGasTank extends TileEntityGasTank
{
	public TileEntityWoodGasTank()
	{
		super(GasesFramework.configurations.blocks.woodGasTank.storageMultiplier);
	}
}
