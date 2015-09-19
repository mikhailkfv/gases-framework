package glenn.gasesframework.common.reaction.environment;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.PartialGasStack;
import glenn.gasesframework.api.reaction.environment.IGasReactionEnvironment;
import net.minecraft.world.World;

public class WorldGasReactionEnvironment extends WorldReactionEnvironment implements IGasReactionEnvironment
{
	public WorldGasReactionEnvironment(World world, int ax, int ay, int az, int bx, int by, int bz)
	{
		super(world, ax, ay, az, bx, by, bz);
	}

	@Override
	public PartialGasStack getB()
	{
		return GasesFramework.implementation.getGas(world, bx, by, bz);
	}

	@Override
	public void setB(PartialGasStack b)
	{
		GasesFramework.implementation.placeGas(world, bx, by, bz, b);
	}

	@Override
	public void igniteB()
	{
		GasesFramework.implementation.ignite(world, bx, by, bz, world.rand);
	}
}
