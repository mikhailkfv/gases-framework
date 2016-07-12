package glenn.gasesframework.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import glenn.gasesframework.api.block.IRenderedGasTypeFilter;
import glenn.gasesframework.client.render.filter.GasTypeFilterRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderBlockGasTypeFilter implements ISimpleBlockRenderingHandler
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		IRenderedGasTypeFilter renderedGasTypeFilter = (IRenderedGasTypeFilter) block;

		renderedGasTypeFilter.swapRenderedGasTypeFilterRenderType();
		if (renderer.renderBlockByRenderType(block, x, y, z))
		{
			if (renderer.overrideBlockTexture == null)
			{
				renderGasTypeFilters(blockAccess, x, y, z, renderedGasTypeFilter);
			}
			renderedGasTypeFilter.swapRenderedGasTypeFilterRenderType();
			return true;
		}
		renderedGasTypeFilter.swapRenderedGasTypeFilterRenderType();
		return false;
	}

	private void renderGasTypeFilters(IBlockAccess blockAccess, int x, int y, int z, IRenderedGasTypeFilter renderedGasTypeFilter)
	{
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			GasTypeFilterRenderer filterRenderer = GasTypeFilterRenderer.factory(renderedGasTypeFilter.getRenderedFilter(blockAccess, x, y, z, side));
			if (filterRenderer != null)
			{
				filterRenderer.renderFilter(blockAccess, x, y, z, side);
			}
		}
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		IRenderedGasTypeFilter renderedGasTypeFilter = (IRenderedGasTypeFilter) block;
		renderedGasTypeFilter.swapRenderedGasTypeFilterRenderType();
		renderer.renderBlockAsItem(block, metadata, 1.0F);
		renderedGasTypeFilter.swapRenderedGasTypeFilterRenderType();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return RENDER_ID;
	}
}
