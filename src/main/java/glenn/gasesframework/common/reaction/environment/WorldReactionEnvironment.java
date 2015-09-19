package glenn.gasesframework.common.reaction.environment;

import java.util.Random;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.PartialGasStack;
import glenn.gasesframework.api.reaction.environment.IReactionEnvironment;
import glenn.moddingutils.DVec;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class WorldReactionEnvironment implements IReactionEnvironment
{
	protected final World world;
	protected final int ax, ay, az;
	protected final int bx, by, bz;

	public WorldReactionEnvironment(World world, int ax, int ay, int az, int bx, int by, int bz)
	{
		this.world = world;
		this.ax = ax;
		this.ay = ay;
		this.az = az;
		this.bx = bx;
		this.by = by;
		this.bz = bz;
	}

	@Override
	public PartialGasStack getA()
	{
		return GasesFramework.implementation.getGas(world, ax, ay, az);
	}

	@Override
	public void setA(PartialGasStack a)
	{
		GasesFramework.implementation.placeGas(world, ax, ay, az, a);
	}

	@Override
	public void igniteA()
	{
		GasesFramework.implementation.ignite(world, ax, ay, az, world.rand);
	}

	@Override
	public void dropItem(ItemStack itemstack)
	{
		double x = (ax + bx) / 2.0D;
		double y = (ay + by) / 2.0D;
		double z = (az + bz) / 2.0D;
		DVec vec = DVec.randomNormalizedVec(world.rand).scale(0.1D);

		EntityItem entityItem = new EntityItem(world, x, y, z, itemstack);
		entityItem.addVelocity(vec.x, vec.y, vec.z);
		world.spawnEntityInWorld(entityItem);
	}

	@Override
	public void explode(float power, boolean isFlaming, boolean isSmoking)
	{
		double x = (ax + bx) / 2.0D;
		double y = (ay + by) / 2.0D;
		double z = (az + bz) / 2.0D;

		world.newExplosion(null, x, y, z, power, isFlaming, isSmoking);
	}

	@Override
	public Random getRandom()
	{
		return world.rand;
	}
}
