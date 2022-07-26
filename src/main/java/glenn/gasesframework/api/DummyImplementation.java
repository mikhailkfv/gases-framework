package glenn.gasesframework.api;

import java.util.Random;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.pipetype.PipeType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A dummy implementation for the Gases Framework that is used when the mod is
 * not installed.
 * 
 * @author Erlend
 */
public class DummyImplementation implements IGFImplementation
{
	@Override
	public boolean canFillWithGas(World world, int x, int y, int z, GasType type)
	{
		return false;
	}

	@Override
	public boolean tryFillWithGas(World world, Random random, int x, int y, int z, GasType type)
	{
		return false;
	}

	@Override
	public void placeGas(World world, int x, int y, int z, PartialGasStack gasStack)
	{
	}

	@Override
	public void placeGas(World world, int x, int y, int z, GasType type, int volume)
	{
	}

	@Override
	public boolean tryPumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		return false;
	}

	@Override
	public boolean tryPushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		return false;
	}

	@Override
	public void placePipe(World world, int x, int y, int z, PipeType pipeType, GasType gasType)
	{
	}

	@Override
	public void ignite(World world, int x, int y, int z, Random random)
	{
	}

	@Override
	public void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking)
	{
	}

	@Override
	public void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{
	}

	@Override
	public PartialGasStack getGas(IBlockAccess blockAccess, int x, int y, int z)
	{
		return null;
	}

	@Override
	public GasType getGasType(IBlockAccess blockAccess, int x, int y, int z)
	{
		return null;
	}

	@Override
	public GasType getGasTypeInPipe(IBlockAccess blockAccess, int x, int y, int z)
	{
		return null;
	}

	@Override
	public PipeType getPipeType(IBlockAccess blockAccess, int x, int y, int z)
	{
		return null;
	}

	@Override
	public int getGasVolume(IBlockAccess blockAccess, int x, int y, int z)
	{
		return 0;
	}

	@Override
	public int getRenderedGasTypeFilterBlockRenderType()
	{
		return 0;
	}
}
