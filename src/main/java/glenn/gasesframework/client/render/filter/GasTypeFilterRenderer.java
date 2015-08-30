package glenn.gasesframework.client.render.filter;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterSingle;
import glenn.gasesframework.api.filter.GasTypeFilterSingleExcluding;
import glenn.gasesframework.api.filter.GasTypeFilterSingleIncluding;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.SharedBlockIcons;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class GasTypeFilterRenderer
{
	protected final IIcon typeIndicatorIcon;
	public float typeIndicatorDesaturation = 0.25F;
	
	protected float typeColorR;
	protected float typeColorG;
	protected float typeColorB;
	
	public static GasTypeFilterRenderer factory(GasTypeFilter filter)
	{
		if (filter instanceof GasTypeFilterSingle)
		{
			GasTypeFilterSingle singleFilter = (GasTypeFilterSingle)filter;
			if (singleFilter instanceof GasTypeFilterSingleExcluding)
			{
				return new GasTypeFilterSingleExcludingRenderer((GasTypeFilterSingleExcluding)singleFilter);
			}
			else if (singleFilter instanceof GasTypeFilterSingleIncluding)
			{
				return new GasTypeFilterSingleIncludingRenderer((GasTypeFilterSingleIncluding)singleFilter);
			}
		}
		
		return null;
	}
	
	protected GasTypeFilterRenderer()
	{
		typeIndicatorIcon = SharedBlockIcons.circularTypeIndicatorIcon;
	}
	
	protected void prepareSide(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side)
	{
		Block block = blockAccess.getBlock(x, y, z);
		Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x + side.offsetX, y + side.offsetY, z + side.offsetZ));
		Tessellator.instance.setNormal(side.offsetX, side.offsetY, side.offsetZ);
	}
	
	protected void setGasTypeColor(GasType type)
	{
		float r = (type.color >> 24 & 255) / 255.0F;
		float g = (type.color >> 16 & 255) / 255.0F;
		float b = (type.color >> 8 & 255) / 255.0F;
		float grayscale = (Math.min(r, Math.min(g, b)) + Math.max(r, Math.max(g, b))) / 2.0F;
		
		typeColorR = grayscale * typeIndicatorDesaturation + r * (1.0F - typeIndicatorDesaturation);
		typeColorG = grayscale * typeIndicatorDesaturation + g * (1.0F - typeIndicatorDesaturation);
		typeColorB = grayscale * typeIndicatorDesaturation + b * (1.0F - typeIndicatorDesaturation);
	}
	
	public boolean renderFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side)
	{
		switch (side)
		{
		case DOWN:
			return renderFilterDown(blockAccess, x, y, z);
		case UP:
			return renderFilterUp(blockAccess, x, y, z);
		case NORTH:
			return renderFilterNorth(blockAccess, x, y, z);
		case SOUTH:
			return renderFilterSouth(blockAccess, x, y, z);
		case WEST:
			return renderFilterWest(blockAccess, x, y, z);
		case EAST:
			return renderFilterEast(blockAccess, x, y, z);
		default:
			return false;
		}
	}
	
	public boolean renderFilterDown(IBlockAccess blockAccess, int x, int y, int z)
	{
		prepareSide(blockAccess, x, y, z, ForgeDirection.DOWN);
		return true;
	}
	
	public boolean renderFilterUp(IBlockAccess blockAccess, int x, int y, int z)
	{
		prepareSide(blockAccess, x, y, z, ForgeDirection.UP);
		return true;
	}
	
	public boolean renderFilterNorth(IBlockAccess blockAccess, int x, int y, int z)
	{
		prepareSide(blockAccess, x, y, z, ForgeDirection.NORTH);
		return true;
	}
	
	public boolean renderFilterSouth(IBlockAccess blockAccess, int x, int y, int z)
	{
		prepareSide(blockAccess, x, y, z, ForgeDirection.SOUTH);
		return true;
	}
	
	public boolean renderFilterWest(IBlockAccess blockAccess, int x, int y, int z)
	{
		prepareSide(blockAccess, x, y, z, ForgeDirection.WEST);
		return true;
	}
	
	public boolean renderFilterEast(IBlockAccess blockAccess, int x, int y, int z)
	{
		prepareSide(blockAccess, x, y, z, ForgeDirection.EAST);
		return true;
	}

	protected void renderFaceDown(double x, double y, double z, float r, float g, float b, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		double minX = x;
		double maxX = x + 1.0D;
		double minY = y;
		double minZ = z;
		double maxZ = z + 1.0D;
		
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		tessellator.setColorOpaque_F(0.5F * r, 0.5F * g, 0.5F * b);
		tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
		tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
		tessellator.addVertexWithUV(maxX, minY, minZ, maxU, minV);
		tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
	}

	protected void renderFaceUp(double x, double y, double z, float r, float g, float b, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		double minX = x;
		double maxX = x + 1.0D;
		double maxY = y + 1.0D;
		double minZ = z;
		double maxZ = z + 1.0D;
		
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		tessellator.setColorOpaque_F(r, g, b);
		tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, maxV);
		tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
		tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
		tessellator.addVertexWithUV(minX, maxY, maxZ, minU, maxV);
	}
	
	protected void renderFaceNorth(double x, double y, double z, float r, float g, float b, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		double minX = x;
		double maxX = x + 1.0D;
		double minY = y;
		double maxY = y + 1.0D;
		double minZ = z;
		
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		tessellator.setColorOpaque_F(0.8F * r, 0.8F * g, 0.8F * b);
		tessellator.addVertexWithUV(minX, maxY, minZ, maxU, minV);
		tessellator.addVertexWithUV(maxX, maxY, minZ, minU, minV);
		tessellator.addVertexWithUV(maxX, minY, minZ, minU, maxV);
		tessellator.addVertexWithUV(minX, minY, minZ, maxU, maxV);
	}
	
	protected void renderFaceSouth(double x, double y, double z, float r, float g, float b, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		double minX = x;
		double maxX = x + 1.0D;
		double minY = y;
		double maxY = y + 1.0D;
		double maxZ = z + 1.0D;
		
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		tessellator.setColorOpaque_F(0.8F * r, 0.8F * g, 0.8F * b);
		tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);
		tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
		tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
		tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV);
	}
	
	protected void renderFaceWest(double x, double y, double z, float r, float g, float b, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		double minX = x;
		double minY = y;
		double maxY = y + 1.0D;
		double minZ = z;
		double maxZ = z + 1.0D;
		
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		tessellator.setColorOpaque_F(0.6F * r, 0.6F * g, 0.6F * b);
		tessellator.addVertexWithUV(minX, maxY, maxZ, maxU, minV);
		tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
		tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
		tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
	}
	
	protected void renderFaceEast(double x, double y, double z, float r, float g, float b, IIcon icon)
	{
		Tessellator tessellator = Tessellator.instance;
		double maxX = x + 1.0D;
		double minY = y;
		double maxY = y + 1.0D;
		double minZ = z;
		double maxZ = z + 1.0D;
		
		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		tessellator.setColorOpaque_F(0.6F * r, 0.6F * g, 0.6F * b);
		tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);
		tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
		tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
		tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, minV);
	}
}
