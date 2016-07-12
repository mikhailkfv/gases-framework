package glenn.gasesframework.api.block;

import glenn.gasesframework.api.filter.GasTypeFilter;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks that can interact with samplers to get and set
 * filters.
 */
public interface IGasTypeFilter
{
	/**
	 * Get the block's filter on this side.
	 * 
	 * @param blockAccess
	 *            The block access
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param z
	 *            Z coordinate
	 * @param side
	 *            The local side
	 * @return The filter on this side, or null
	 */
	GasTypeFilter getFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side);

	/**
	 * Set the block's filter on this side.
	 * 
	 * @param blockAccess
	 *            The block access
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param z
	 *            Z coordinate
	 * @param side
	 *            The local side
	 * @param filter
	 *            The filter
	 * @return The new filter on this side
	 */
	GasTypeFilter setFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side, GasTypeFilter filter);
}
