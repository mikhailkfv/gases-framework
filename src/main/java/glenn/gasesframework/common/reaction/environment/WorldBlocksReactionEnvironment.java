package glenn.gasesframework.common.reaction.environment;

import glenn.moddingutils.DVec;
import net.minecraft.world.World;

public class WorldBlocksReactionEnvironment extends WorldReactionEnvironment
{
	protected final int bx, by, bz;

	public WorldBlocksReactionEnvironment(World world, int ax, int ay, int az, int bx, int by, int bz)
	{
		super(world, ax, ay, az);
		this.bx = bx;
		this.by = by;
		this.bz = bz;
	}

	@Override
	protected DVec getCenter()
	{
		return new DVec((ax + bx) / 2.0D, (ay + by) / 2.0D, (az + bz) / 2.0D);
	}
}
