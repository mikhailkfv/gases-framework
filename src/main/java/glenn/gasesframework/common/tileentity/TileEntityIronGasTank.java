package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasTank extends TileEntityGasTank
{
	public TileEntityIronGasTank()
	{
		super(GasesFramework.configurations.blocks.ironGasTank.storageMultiplier);
	}
}
