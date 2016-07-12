package glenn.gasesframework.client.render.filter;

import glenn.gasesframework.api.filter.GasTypeFilterSingle;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public abstract class GasTypeFilterSingleRenderer extends GasTypeFilterRenderer
{
	protected final GasTypeFilterSingle filter;
	protected final IIcon outlineIcon;

	protected GasTypeFilterSingleRenderer(GasTypeFilterSingle filter, IIcon outlineIcon)
	{
		this.filter = filter;
		this.outlineIcon = outlineIcon;
	}

	@Override
	public boolean renderFilterDown(IBlockAccess blockAccess, int x, int y, int z)
	{
		if (super.renderFilterDown(blockAccess, x, y, z))
		{
			if (outlineIcon != null)
			{
				renderFaceDown(x, y, z, 1.0F, 1.0F, 1.0F, outlineIcon);
			}
			setGasTypeColor(filter.getFilterType());
			renderFaceDown(x, y, z, typeColorR, typeColorG, typeColorB, typeIndicatorIcon);
			return true;
		}

		return false;
	}

	@Override
	public boolean renderFilterUp(IBlockAccess blockAccess, int x, int y, int z)
	{
		if (super.renderFilterUp(blockAccess, x, y, z))
		{
			if (outlineIcon != null)
			{
				renderFaceUp(x, y, z, 1.0F, 1.0F, 1.0F, outlineIcon);
			}
			setGasTypeColor(filter.getFilterType());
			renderFaceUp(x, y, z, typeColorR, typeColorG, typeColorB, typeIndicatorIcon);
			return true;
		}

		return false;
	}

	@Override
	public boolean renderFilterNorth(IBlockAccess blockAccess, int x, int y, int z)
	{
		if (super.renderFilterNorth(blockAccess, x, y, z))
		{
			if (outlineIcon != null)
			{
				renderFaceNorth(x, y, z, 1.0F, 1.0F, 1.0F, outlineIcon);
			}
			setGasTypeColor(filter.getFilterType());
			renderFaceNorth(x, y, z, typeColorR, typeColorG, typeColorB, typeIndicatorIcon);
			return true;
		}

		return false;
	}

	@Override
	public boolean renderFilterSouth(IBlockAccess blockAccess, int x, int y, int z)
	{
		if (super.renderFilterSouth(blockAccess, x, y, z))
		{
			if (outlineIcon != null)
			{
				renderFaceSouth(x, y, z, 1.0F, 1.0F, 1.0F, outlineIcon);
			}
			setGasTypeColor(filter.getFilterType());
			renderFaceSouth(x, y, z, typeColorR, typeColorG, typeColorB, typeIndicatorIcon);
			return true;
		}

		return false;
	}

	@Override
	public boolean renderFilterWest(IBlockAccess blockAccess, int x, int y, int z)
	{
		if (super.renderFilterWest(blockAccess, x, y, z))
		{
			if (outlineIcon != null)
			{
				renderFaceWest(x, y, z, 1.0F, 1.0F, 1.0F, outlineIcon);
			}
			setGasTypeColor(filter.getFilterType());
			renderFaceWest(x, y, z, typeColorR, typeColorG, typeColorB, typeIndicatorIcon);
			return true;
		}

		return false;
	}

	@Override
	public boolean renderFilterEast(IBlockAccess blockAccess, int x, int y, int z)
	{
		if (super.renderFilterEast(blockAccess, x, y, z))
		{
			if (outlineIcon != null)
			{
				renderFaceEast(x, y, z, 1.0F, 1.0F, 1.0F, outlineIcon);
			}
			setGasTypeColor(filter.getFilterType());
			renderFaceEast(x, y, z, typeColorR, typeColorG, typeColorB, typeIndicatorIcon);
			return true;
		}

		return false;
	}
}
