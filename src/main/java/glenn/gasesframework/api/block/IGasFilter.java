package glenn.gasesframework.api.block;

import glenn.gasesframework.api.filter.GasTypeFilter;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks that can interact with samplers to get and set filters.
 * @author Erlend
 *
 */
public interface IGasFilter
{
	/**
	 * Get the block's filter on the specific side.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @return
	 */
	public GasTypeFilter getFilter(World world, int x, int y, int z, ForgeDirection side);

	/**
	 * Set the block's filter on the specific side.
	 * Return the new filter.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param filter
	 * @return
	 */
	public GasTypeFilter setFilter(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter);
}
