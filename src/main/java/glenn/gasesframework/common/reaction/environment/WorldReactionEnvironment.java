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

	public WorldReactionEnvironment(World world, int ax, int ay, int az)
	{
		this.world = world;
		this.ax = ax;
		this.ay = ay;
		this.az = az;
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
		DVec c = getCenter();
		DVec v = DVec.randomNormalizedVec(world.rand).scale(0.1D);

		EntityItem entityItem = new EntityItem(world, c.x, c.y, c.z, itemstack);
		entityItem.addVelocity(v.x, v.y, v.z);
		world.spawnEntityInWorld(entityItem);
	}

	@Override
	public void explode(float power, boolean isFlaming, boolean isSmoking)
	{
		DVec c = getCenter();
		world.newExplosion(null, c.x, c.y, c.z, power, isFlaming, isSmoking);
	}

	@Override
	public Random getRandom()
	{
		return world.rand;
	}

	@Override
	public World getWorld()
	{
		return world;
	}

	@Override
	public void playSound(String name, float volume, float pitch)
	{
		DVec c = getCenter();
		world.playSound(c.x, c.y, c.z, name, volume, pitch, false);
	}

	protected abstract DVec getCenter();
}
