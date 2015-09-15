package glenn.gasesframework.api;

import java.util.Random;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A dummy implementation for the Gases Framework that is used when the mod is not installed.
 * @author Erlend
 */
public class DummyImplementation implements IGasesFrameworkImplementation
{
	@Override
	public void addSpecialFurnaceRecipe(ItemStack ingredient, ItemStack result, int cookTime, int exp)
	{}

	@Override
	public boolean canFillWithGas(World world, int x, int y, int z, GasType type)
	{
		return false;
	}

	@Override
	public boolean fillWithGas(World world, Random random, int x, int y, int z, GasType type)
	{
		return false;
	}

	@Override
	public void placeGas(World world, int x, int y, int z, GasType type, int volume)
	{}

	@Override
	public boolean pumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		return false;
	}

	@Override
	public boolean pushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		return false;
	}

	@Override
	public void ignite(World world, int x, int y, int z, Random random)
	{}

	@Override
	public void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking)
	{}

	@Override
	public void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{}

	@Override
	public GasType getGasType(IBlockAccess blockAccess, int x, int y, int z)
	{
		return null;
	}

	@Override
	public GasType getGasPipeType(IBlockAccess blockAccess, int x, int y, int z)
	{
		return null;
	}

	@Override
	public int getGasVolume(IBlockAccess blockAccess, int x, int y, int z)
	{
		return 0;
	}

	@Override
	public float getGasExplosionPowerFactor()
	{
		return 0;
	}

	@Override
	public int getRenderedGasTypeFilterBlockRenderType()
	{
		return 0;
	}
}
