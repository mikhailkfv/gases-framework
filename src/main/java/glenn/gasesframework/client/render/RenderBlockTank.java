package glenn.gasesframework.client.render;

import glenn.gasesframework.common.block.BlockGasTank;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBlockTank implements ISimpleBlockRenderingHandler
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block bblock, int metadata, int modelID, RenderBlocks renderer)
	{
		BlockGasTank block = (BlockGasTank) bblock;
		Tessellator tessellator = Tessellator.instance;

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		double uMin = block.side.getInterpolatedU(0.0D);
		double uMax = block.side.getInterpolatedU(16.0D);
		double vMin = block.side.getInterpolatedV(0.0D);
		double vMax = block.side.getInterpolatedV(16.0D);

		tessellator.startDrawingQuads();

		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMax, vMax);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMin, vMax);

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMin, vMax);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMax, vMax);

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMax, vMax);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMin, vMax);

		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMin, vMax);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMax, vMax);

		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMax, vMax);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMin, vMax);

		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMin, vMax);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMax, vMax);

		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMax, vMax);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMin, vMax);

		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMin, vMax);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMax, vMax);

		uMin = block.top.getInterpolatedU(0.0D);
		uMax = block.top.getInterpolatedU(16.0D);
		vMin = block.top.getInterpolatedV(0.0D);
		vMax = block.top.getInterpolatedV(16.0D);

		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMax, vMax);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMin, vMax);

		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMax, vMax);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMin, vMax);

		uMin = block.inside.getInterpolatedU(0.0D);
		uMax = block.inside.getInterpolatedU(16.0D);
		vMin = block.inside.getInterpolatedV(0.0D);
		vMax = block.inside.getInterpolatedV(16.0D);

		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 2.0D / 16.0D, 1.0D, uMin, vMax);
		tessellator.addVertexWithUV(1.0D, 2.0D / 16.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 2.0D / 16.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 2.0D / 16.0D, 0.0D, uMax, vMax);

		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 14.0D / 16.0D, 0.0D, uMin, vMax);
		tessellator.addVertexWithUV(1.0D, 14.0D / 16.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 14.0D / 16.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 14.0D / 16.0D, 1.0D, uMax, vMax);

		tessellator.draw();

		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block bblock, int modelId, RenderBlocks renderer)
	{
		BlockGasTank block = (BlockGasTank) bblock;
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);

		double uMin = block.side.getInterpolatedU(0.0D);
		double uMax = block.side.getInterpolatedU(16.0D);
		double vMin = block.side.getInterpolatedV(0.0D);
		double vMax = block.side.getInterpolatedV(16.0D);

		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		if (block.shouldSideBeRendered(blockAccess, x, y, z - 1, 2))
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z - 1));
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMax, vMax);
			tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMax, vMin);
			tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMin, vMin);
			tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMin, vMax);

			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMin, vMax);
			tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMin, vMin);
			tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMax, vMin);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMax, vMax);
		}

		if (block.shouldSideBeRendered(blockAccess, x, y, z + 1, 3))
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z + 1));
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMax, vMax);
			tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMax, vMin);
			tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMin, vMin);
			tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMin, vMax);

			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMin, vMax);
			tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMin, vMin);
			tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMax, vMin);
			tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMax, vMax);
		}

		if (block.shouldSideBeRendered(blockAccess, x - 1, y, z, 4))
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x - 1, y, z));
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMax, vMax);
			tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMax, vMin);
			tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMin, vMin);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMin, vMax);

			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMin, vMax);
			tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMin, vMin);
			tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMax, vMin);
			tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMax, vMax);
		}

		if (block.shouldSideBeRendered(blockAccess, x + 1, y, z, 5))
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x + 1, y, z));
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMax, vMax);
			tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMax, vMin);
			tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMin, vMin);
			tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMin, vMax);

			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMin, vMax);
			tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMin, vMin);
			tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMax, vMin);
			tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMax, vMax);
		}

		uMin = block.top.getInterpolatedU(0.0D);
		uMax = block.top.getInterpolatedU(16.0D);
		vMin = block.top.getInterpolatedV(0.0D);
		vMax = block.top.getInterpolatedV(16.0D);

		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		if (block.shouldSideBeRendered(blockAccess, x, y - 1, z, 0))
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z));
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uMax, vMax);
			tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uMax, vMin);
			tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uMin, vMin);
			tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uMin, vMax);
		}

		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		if (block.shouldSideBeRendered(blockAccess, x, y + 1, z, 1))
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y + 1, z));
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uMax, vMax);
			tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uMax, vMin);
			tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uMin, vMin);
			tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uMin, vMax);
		}

		uMin = block.inside.getInterpolatedU(0.0D);
		uMax = block.inside.getInterpolatedU(16.0D);
		vMin = block.inside.getInterpolatedV(0.0D);
		vMax = block.inside.getInterpolatedV(16.0D);

		tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 2.0D / 16.0D, 1.0D, uMin, vMax);
		tessellator.addVertexWithUV(1.0D, 2.0D / 16.0D, 1.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 2.0D / 16.0D, 0.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 2.0D / 16.0D, 0.0D, uMax, vMax);

		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 14.0D / 16.0D, 0.0D, uMin, vMax);
		tessellator.addVertexWithUV(1.0D, 14.0D / 16.0D, 0.0D, uMin, vMin);
		tessellator.addVertexWithUV(1.0D, 14.0D / 16.0D, 1.0D, uMax, vMin);
		tessellator.addVertexWithUV(0.0D, 14.0D / 16.0D, 1.0D, uMax, vMax);

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