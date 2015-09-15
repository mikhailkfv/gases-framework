package glenn.gasesframework.api.block;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An abstract base interface which many machinery related interfaces extend.
 * Implementing this interface is unnecessary.
 * @author Erlend
 */
public interface IGasInterface
{
	/**
	 * Should pipes connect to this block?
	 * @param blockaccess
	 * @param x
	 * @param y
	 * @param z
	 * @param side - The local side of this block the pipe can connect to.
	 * @return
	 */
	boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side);
}