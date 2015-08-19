package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityWoodGasFurnace extends TileEntityGasFurnace
{
	public TileEntityWoodGasFurnace()
	{
		super(
			GasesFramework.configurations.blocks.woodGasFurnace.smokeEmissionInterval,
			GasesFramework.configurations.blocks.woodGasFurnace.maxFuel,
			GasesFramework.configurations.blocks.woodGasFurnace.temperaturePerFuel,
			GasesFramework.configurations.blocks.woodGasFurnace.temperatureFalloff
		);
	}
}
