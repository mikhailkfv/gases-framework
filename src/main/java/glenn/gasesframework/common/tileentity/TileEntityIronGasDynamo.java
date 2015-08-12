package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasDynamo extends TileEntityGasDynamo
{
	public TileEntityIronGasDynamo()
	{
		super(
			GasesFramework.configurations.blocks.gasDynamo.maxEnergy,
			GasesFramework.configurations.blocks.gasDynamo.maxEnergyTransfer,
			GasesFramework.configurations.blocks.gasDynamo.maxFuel,
			GasesFramework.configurations.blocks.gasDynamo.fuelPerTick
		);
	}
}