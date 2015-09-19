package glenn.gasesframework.common.reaction.environment;

import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class WorldEntityReactionEnvironment extends WorldReactionEnvironment implements IEntityReactionEnvironment
{
	public final Entity b;

	public WorldEntityReactionEnvironment(World world, int ax, int ay, int az, Entity b)
	{
		super(world, ax, ay, az, 0, 0, 0);
		this.b = b;
	}

	@Override
	public Entity getB()
	{
		return b;
	}
}
