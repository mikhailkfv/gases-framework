package glenn.gasesframework.client.render;

import glenn.gasesframework.api.GFAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.block.BlockInfiniteGasPump;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasPump;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBlockInfiniteGasPump implements ISimpleBlockRenderingHandler
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;

		float red = 1.0F;
		float green = 1.0F;
		float blue = 1.0F;

		if (renderer.useInventoryTint)
		{
			int var6 = block.getRenderColor(metadata);

			red = (float) (var6 >> 16 & 255) / 255.0F;
			green = (float) (var6 >> 8 & 255) / 255.0F;
			blue = (float) (var6 & 255) / 255.0F;
		}

		renderer.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		GL11.glColor4f(red, green, blue, 1.0F);

		tessellator.startDrawingQuads();

		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));

		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	private void getUVs(double[] us, double[] vs, IIcon icon)
	{
		for (int i = 0; i < 4; i++)
		{
			switch (i)
			{
				case 0:
					us[i] = icon.getInterpolatedU(16.0D);
					vs[i] = icon.getInterpolatedV(0.0D);
					break;
				case 1:
					us[i] = icon.getInterpolatedU(0.0D);
					vs[i] = icon.getInterpolatedV(0.0D);
					break;
				case 2:
					us[i] = icon.getInterpolatedU(0.0D);
					vs[i] = icon.getInterpolatedV(16.0D);
					break;
				case 3:
					us[i] = icon.getInterpolatedU(16.0D);
					vs[i] = icon.getInterpolatedV(16.0D);
					break;
			}
		}
	}

	private void renderDown(Tessellator tessellator, double[] us, double[] vs, IIcon icon, float r, float g, float b)
	{
		tessellator.setColorOpaque_F(0.6F * r, 0.6F * g, 0.6F * b);
		getUVs(us, vs, icon);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, us[1], vs[1]);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, us[2], vs[2]);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, us[3], vs[3]);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, us[0], vs[0]);
	}

	private void renderUp(Tessellator tessellator, double[] us, double[] vs, IIcon icon, float r, float g, float b)
	{
		tessellator.setColorOpaque_F(1.0F * r, 1.0F * g, 1.0F * b);
		getUVs(us, vs, icon);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, us[1], vs[1]);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, us[2], vs[2]);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, us[3], vs[3]);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, us[0], vs[0]);
	}

	private void renderNorth(Tessellator tessellator, double[] us, double[] vs, IIcon icon, float r, float g, float b)
	{
		tessellator.setColorOpaque_F(0.8F * r, 0.8F * g, 0.8F * b);
		getUVs(us, vs, icon);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, us[0], vs[0]);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, us[1], vs[1]);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, us[2], vs[2]);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, us[3], vs[3]);
	}

	private void renderSouth(Tessellator tessellator, double[] us, double[] vs, IIcon icon, float r, float g, float b)
	{
		tessellator.setColorOpaque_F(0.8F * r, 0.8F * g, 0.8F * b);
		getUVs(us, vs, icon);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, us[2], vs[2]);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, us[3], vs[3]);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, us[0], vs[0]);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, us[1], vs[1]);
	}

	private void renderEast(Tessellator tessellator, double[] us, double[] vs, IIcon icon, float r, float g, float b)
	{
		tessellator.setColorOpaque_F(0.8F * r, 0.8F * g, 0.8F * b);
		getUVs(us, vs, icon);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, us[3], vs[3]);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, us[0], vs[0]);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, us[1], vs[1]);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, us[2], vs[2]);
	}

	private void renderWest(Tessellator tessellator, double[] us, double[] vs, IIcon icon, float r, float g, float b)
	{
		tessellator.setColorOpaque_F(0.8F * r, 0.8F * g, 0.8F * b);
		getUVs(us, vs, icon);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, us[3], vs[3]);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, us[0], vs[0]);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, us[1], vs[1]);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, us[2], vs[2]);
	}

	private void renderFace(BlockInfiniteGasPump block, IBlockAccess blockAccess, int x, int y, int z, Tessellator tessellator, double[] us, double[] vs, ForgeDirection faceDirection, IIcon icon, float r, float g, float b)
	{
		switch (faceDirection)
		{
			case DOWN:
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z));
				renderDown(tessellator, us, vs, icon, r, g, b);
				break;
			case UP:
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z));
				renderUp(tessellator, us, vs, icon, r, g, b);
				break;
			case NORTH:
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1));
				renderNorth(tessellator, us, vs, icon, r, g, b);
				break;
			case SOUTH:
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1));
				renderSouth(tessellator, us, vs, icon, r, g, b);
				break;
			case WEST:
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z));
				renderWest(tessellator, us, vs, icon, r, g, b);
				break;
			case EAST:
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z));
				renderEast(tessellator, us, vs, icon, r, g, b);
				break;
			default:

		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block bblock, int modelId, RenderBlocks renderer)
	{
		BlockInfiniteGasPump block = (BlockInfiniteGasPump) bblock;
		TileEntityInfiniteGasPump tileEntity = (TileEntityInfiniteGasPump) blockAccess.getTileEntity(x, y, z);

		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);

		double[] us = new double[4];
		double[] vs = new double[4];

		for (ForgeDirection faceDirection : ForgeDirection.VALID_DIRECTIONS)
		{
			IIcon icon;
			GasType type = tileEntity.getType(faceDirection);

			if (type != null && type != GFAPI.gasTypeAir)
			{
				int color = type.color;
				float gasRed = 0.25F + (float) ((color >> 24) & 0xFF) / 510.0F;
				float gasGreen = 0.25F + (float) ((color >> 16) & 0xFF) / 510.0F;
				float gasBlue = 0.25F + (float) ((color >> 8) & 0xFF) / 510.0F;

				renderFace(block, blockAccess, x, y, z, tessellator, us, vs, faceDirection, block.indicatorIcon, gasRed, gasGreen, gasBlue);
				icon = block.typeIcon;
			}
			else
			{
				icon = block.getBlockTextureFromSide(faceDirection.ordinal());
			}

			renderFace(block, blockAccess, x, y, z, tessellator, us, vs, faceDirection, icon, 1.0F, 1.0F, 1.0F);
		}

		tessellator.addTranslation(-x, -y, -z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int i)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return RENDER_ID;
	}
}
