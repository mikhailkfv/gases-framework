package glenn.gasesframework.client.render;

import glenn.gasesframework.api.GFAPI;
import org.lwjgl.opengl.GL11;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.tileentity.TileEntityGasTank;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer
{
	private ResourceLocation texture = new ResourceLocation("gasesframework:textures/misc/gas_tanked.png");
	private Tessellator tessellator;

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d0, double d1, double d2, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d0, (float) d1, (float) d2);

		renderTankAt((TileEntityGasTank) tileEntity, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);

		GL11.glPopMatrix();
	}

	public void renderTankAt(TileEntityGasTank tileEntity, int i, int j, int k)
	{
		double gasHeight = tileEntity.getRelativeGasStored();
		World world = tileEntity.getWorldObj();
		Block block = tileEntity.getBlockType();
		int mixedBrightness = block.getMixedBrightnessForBlock(world, i, j, k);

		if (gasHeight > 0.0D)
		{
			bindTexture(texture);

			GasType gasType = tileEntity.getGasTypeStored();
			if (gasType == null)
			{
				gasType = GFAPI.gasTypeAir;
			}

			int color = gasType.color;
			float red = (float) ((color >> 24) & 0xFF) / 255.0F;
			float green = (float) ((color >> 16) & 0xFF) / 255.0F;
			float blue = (float) ((color >> 8) & 0xFF) / 255.0F;
			float alpha = (float) (color & 0xFF) / 255.0f;

			tessellator = Tessellator.instance;

			double minX = 2.0D / 16.0D;
			double maxX = 14.0D / 16.0D;

			double minY = 2.0D / 16.0D;
			double maxY = (2.0D + gasHeight * 12.0D) / 16.0D;

			double minZ = 2.0D / 16.0D;
			double maxZ = 14.0D / 16.0D;

			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(red, green, blue, 1.0F);

			if (GasesFramework.configurations.blocks.gasTanks.fancyTank)
			{
				double scale = 12.0D / 16.0D;

				tessellator.startDrawing(GL11.GL_QUAD_STRIP);
				tessellator.setBrightness(mixedBrightness);
				tessellator.setNormal(0.0F, 0.0F, -1.0F);
				for (int l = 1; l < 8; l++)
				{
					double d = minY + tileEntity.ps[l][0] * scale;
					tessellator.addVertexWithUV(l / 8.0D, minY, 0.0D, l / 8.0D, minY);
					tessellator.addVertexWithUV(l / 8.0D, d, 0.0D, l / 8.0D, d);
				}
				tessellator.draw();

				tessellator.startDrawing(GL11.GL_QUAD_STRIP);
				tessellator.setBrightness(mixedBrightness);
				tessellator.setNormal(0.0F, 0.0F, 1.0F);
				for (int l = 1; l < 8; l++)
				{
					double d = minY + tileEntity.ps[l][8] * scale;
					tessellator.addVertexWithUV(l / 8.0D, d, 1.0D, l / 8.0D, d);
					tessellator.addVertexWithUV(l / 8.0D, minY, 1.0D, l / 8.0D, minY);
				}
				tessellator.draw();

				tessellator.startDrawing(GL11.GL_QUAD_STRIP);
				tessellator.setBrightness(mixedBrightness);
				tessellator.setNormal(-1.0F, 0.0F, 0.0F);
				for (int l = 1; l < 8; l++)
				{
					double d = minY + tileEntity.ps[0][l] * scale;
					tessellator.addVertexWithUV(0.0D, d, l / 8.0D, l / 8.0D, d);
					tessellator.addVertexWithUV(0.0D, minY, l / 8.0D, l / 8.0D, minY);
				}
				tessellator.draw();

				tessellator.startDrawing(GL11.GL_QUAD_STRIP);
				tessellator.setBrightness(mixedBrightness);
				tessellator.setNormal(1.0F, 0.0F, 0.0F);
				for (int l = 1; l < 8; l++)
				{
					double d = minY + tileEntity.ps[8][l] * scale;
					tessellator.addVertexWithUV(1.0D, minY, l / 8.0D, l / 8.0D, minY);
					tessellator.addVertexWithUV(1.0D, d, l / 8.0D, l / 8.0D, d);
				}
				tessellator.draw();

				for (int x = 0; x < 8; x++)
				{
					tessellator.startDrawing(GL11.GL_QUAD_STRIP);
					tessellator.setBrightness(mixedBrightness);
					tessellator.setNormal(0.0F, 1.0F, 0.0F);
					for (int y = 0; y < 9; y++)
					{
						tessellator.addVertexWithUV((x + 1) / 8.0D, minY + tileEntity.ps[x + 1][y] * scale, y / 8.0D, (x + 1) / 8.0D, y / 8.0D);
						tessellator.addVertexWithUV(x / 8.0D, minY + tileEntity.ps[x][y] * scale, y / 8.0D, x / 8.0D, y / 8.0D);
					}
					tessellator.draw();
				}
			}
			else
			{
				tessellator.startDrawingQuads();
				tessellator.setBrightness(mixedBrightness);

				tessellator.setNormal(0.0F, 0.0F, -1.0F);
				tessellator.addVertexWithUV(minX, minY, 0.0D, maxX, minY);
				tessellator.addVertexWithUV(minX, maxY, 0.0D, maxX, maxY);
				tessellator.addVertexWithUV(maxX, maxY, 0.0D, minX, maxY);
				tessellator.addVertexWithUV(maxX, minY, 0.0D, minX, minY);

				tessellator.setNormal(0.0F, 0.0F, 1.0F);
				tessellator.addVertexWithUV(maxX, minY, 1.0D, maxX, minY);
				tessellator.addVertexWithUV(maxX, maxY, 1.0D, maxX, maxY);
				tessellator.addVertexWithUV(minX, maxY, 1.0D, minX, maxY);
				tessellator.addVertexWithUV(minX, minY, 1.0D, minX, minY);

				tessellator.setNormal(-1.0F, 0.0F, 0.0F);
				tessellator.addVertexWithUV(0.0D, minY, maxZ, minZ, minY);
				tessellator.addVertexWithUV(0.0D, maxY, maxZ, minZ, maxY);
				tessellator.addVertexWithUV(0.0D, maxY, minZ, maxZ, maxY);
				tessellator.addVertexWithUV(0.0D, minY, minZ, maxZ, minY);

				tessellator.setNormal(-1.0F, 0.0F, 0.0F);
				tessellator.addVertexWithUV(1.0D, minY, minZ, minZ, minY);
				tessellator.addVertexWithUV(1.0D, maxY, minZ, minZ, maxY);
				tessellator.addVertexWithUV(1.0D, maxY, maxZ, maxZ, maxY);
				tessellator.addVertexWithUV(1.0D, minY, maxZ, maxZ, minY);

				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(1.0D, maxY, 1.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV(1.0D, maxY, 0.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(0.0D, maxY, 0.0D, 0.0D, 0.0D);
				tessellator.addVertexWithUV(0.0D, maxY, 1.0D, 0.0D, 1.0D);

				tessellator.draw();
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}