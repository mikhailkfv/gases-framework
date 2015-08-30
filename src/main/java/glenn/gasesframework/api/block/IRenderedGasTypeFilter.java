package glenn.gasesframework.api.block;

import glenn.gasesframework.api.filter.GasTypeFilter;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks that use the Gases Framework's default filter rendering (see gas pumps and collectors).
 * When using this interface, blocks will have additional textures applied to its sides.
 * 
 * Simply implementing this interface is insufficient to make it work.
 * The block must override {@link net.minecraft.block.Block#getRenderType() getRenderType()} to return
 * {@link glenn.gasesframework.api.GasesFrameworkAPI#getRenderedGasTypeFilterBlockRenderType() getRenderedGasTypeFilterBlockRenderType()} INITIALLY.
 * It must, however, swap to return the underlying render type when {@link #swapRenderedGasTypeFilterRenderType() swapRenderedGasTypeFilterRenderType()} is called,
 * and swap back again when repeated.
 * @author Erlend
 *
 */
public interface IRenderedGasTypeFilter
{
	/**
	 * Get the filter to be rendered on this side of the block. If null, no filter will be rendered.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @param side - Local side
	 * @return
	 */
	public GasTypeFilter getRenderedFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side);
	
	/**
	 * Swap the result of {@link net.minecraft.block.Block#getRenderType() getRenderType()}
	 * between {@link glenn.gasesframework.api.GasesFrameworkAPI#getRenderedGasTypeFilterBlockRenderType() getRenderedGasTypeFilterBlockRenderID()}
	 * and the underlying block renderer, commonly 0.
	 */
	public void swapRenderedGasTypeFilterRenderType();
}
