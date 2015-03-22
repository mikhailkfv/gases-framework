package glenn.gasesframework.api.block;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public abstract interface IGasInterface
{
	/**
	 * Should pipes connect to this block?
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side - The side of this block the pipe can connect to.
	 * @return
	 */
	boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side);
}