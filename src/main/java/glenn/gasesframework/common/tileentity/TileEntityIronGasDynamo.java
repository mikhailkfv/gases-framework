package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasDynamo extends TileEntityGasDynamo
{
	public TileEntityIronGasDynamo()
	{
		super(
			GasesFramework.configurations.blocks.ironGasDynamo.maxEnergy,
			GasesFramework.configurations.blocks.ironGasDynamo.maxEnergyTransfer,
			GasesFramework.configurations.blocks.ironGasDynamo.maxFuel,
			GasesFramework.configurations.blocks.ironGasDynamo.fuelPerTick,
			GasesFramework.configurations.blocks.ironGasDynamo.energyPerFuel
		);
	}
}