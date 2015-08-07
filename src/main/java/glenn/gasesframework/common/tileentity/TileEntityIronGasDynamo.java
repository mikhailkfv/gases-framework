package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasDynamo extends TileEntityGasDynamo
{
	public TileEntityIronGasDynamo()
	{
		super(
			GasesFramework.configurations.gasDynamo_maxEnergy,
			GasesFramework.configurations.gasDynamo_maxEnergyTransfer,
			GasesFramework.configurations.gasDynamo_maxFuel,
			GasesFramework.configurations.gasDynamo_fuelPerTick
		);
	}
}