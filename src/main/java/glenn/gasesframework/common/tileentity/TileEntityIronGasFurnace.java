package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;

public class TileEntityIronGasFurnace extends TileEntityGasFurnace
{
	public TileEntityIronGasFurnace()
	{
		super(GasesFramework.configurations.blocks.ironGasFurnace.smokeEmissionInterval, GasesFramework.configurations.blocks.ironGasFurnace.maxFuel, GasesFramework.configurations.blocks.ironGasFurnace.temperaturePerFuel, GasesFramework.configurations.blocks.ironGasFurnace.temperatureFalloff);
	}
}
