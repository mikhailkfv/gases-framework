package glenn.gasesframework.api.block;

import glenn.gasesframework.api.IGFImplementation;
import glenn.gasesframework.api.filter.GasTypeFilter;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks that use the Gases Framework's default filter
 * rendering (see gas pumps and collectors). When using this interface, blocks
 * will have additional textures applied to its sides. Simply implementing this
 * interface is insufficient to make it work. The block must override
 * {@link net.minecraft.block.Block#getRenderType() getRenderType()} to return
 * {@link IGFImplementation#getRenderedGasTypeFilterBlockRenderType()
 * getRenderedGasTypeFilterBlockRenderType()} INITIALLY. It must, however, swap
 * to return the underlying render type when
 * {@link #swapRenderedGasTypeFilterRenderType()
 * swapRenderedGasTypeFilterRenderType()} is called, and swap back again when
 * repeated.
 * 
 * @author Erlend
 */
public interface IRenderedGasTypeFilter
{
	/**
	 * Get the filter to be rendered on this side of the block. If null, no
	 * filter will be rendered.
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
	 * @return The filter that is to be rendered on this side, or null
	 */
	GasTypeFilter getRenderedFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side);

	/**
	 * Swap the result of {@link net.minecraft.block.Block#getRenderType()
	 * getRenderType()} between
	 * {@link IGFImplementation#getRenderedGasTypeFilterBlockRenderType()
	 * getRenderedGasTypeFilterBlockRenderID()} and the underlying block
	 * renderer, commonly 0.
	 */
	void swapRenderedGasTypeFilterRenderType();
}
