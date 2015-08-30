package glenn.gasesframework.api.block;

import glenn.gasesframework.api.filter.GasTypeFilter;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks that can interact with samplers to get and set filters.
 * @author Erlend
 *
 */
public interface IGasTypeFilter
{
	/**
	 * Get the block's filter on the specific side.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @return
	 */
	public GasTypeFilter getFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side);

	/**
	 * Set the block's filter on the specific side.
	 * Return the new filter.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param filter
	 * @return
	 */
	public GasTypeFilter setFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side, GasTypeFilter filter);
}
