package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityWoodGasDynamo extends TileEntityGasDynamo
{
	public TileEntityWoodGasDynamo()
	{
		super(
			GasesFramework.configurations.blocks.woodGasDynamo.maxEnergy,
			GasesFramework.configurations.blocks.woodGasDynamo.maxEnergyTransfer,
			GasesFramework.configurations.blocks.woodGasDynamo.maxFuel,
			GasesFramework.configurations.blocks.woodGasDynamo.fuelPerTick,
			GasesFramework.configurations.blocks.woodGasDynamo.energyPerFuel
		);
	}
}