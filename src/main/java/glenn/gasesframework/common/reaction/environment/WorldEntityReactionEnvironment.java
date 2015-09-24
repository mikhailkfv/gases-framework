package glenn.gasesframework.common.reaction.environment;

import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;
import glenn.moddingutils.DVec;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class WorldEntityReactionEnvironment extends WorldReactionEnvironment implements IEntityReactionEnvironment
{
	public final Entity b;

	public WorldEntityReactionEnvironment(World world, int ax, int ay, int az, Entity b)
	{
		super(world, ax, ay, az);
		this.b = b;
	}

	@Override
	public Entity getB()
	{
		return b;
	}

	@Override
	protected DVec getCenter()
	{
		return new DVec((ax + b.posX) / 2.0D, (ay + b.posY) / 2.0D, (az + b.posZ) / 2.0D);
	}
}
