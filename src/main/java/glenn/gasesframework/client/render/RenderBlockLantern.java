package glenn.gasesframework.client.render;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.block.BlockGasPipe;
import glenn.gasesframework.block.BlockLantern;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBlockLantern implements ISimpleBlockRenderingHandler
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	@Override
	public void renderInventoryBlock(Block bblock, int metadata, int modelID, RenderBlocks renderer)
	{
		BlockLantern block = (BlockLantern)bblock;
		Tessellator tessellator = Tessellator.instance;

		float red = 1.0F;
		float green = 1.0F;
		float blue = 1.0F;

		if (renderer.useInventoryTint)
        {
            int var6 = block.getRenderColor(metadata);

            red = (float)(var6 >> 16 & 255) / 255.0F;
            green = (float)(var6 >> 8 & 255) / 255.0F;
            blue = (float)(var6 & 255) / 255.0F;
        }

        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        GL11.glTranslatef(-0.5F, -0.4F, -0.5F);
        GL11.glColor4f(red, green, blue, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        double minX = 4.0 / 16.0D;
    	double minY = 0.0 / 16.0D;
    	double minZ = 4.0 / 16.0D;
    	double maxX = 12.0 / 16.0D;
    	double maxY = 10.0 / 16.0D;
    	double maxZ = 12.0 / 16.0D;

		IIcon icon = block.sideIcon;
		double uvMinU = icon.getInterpolatedU(4.0D);
    	double uvMaxU = icon.getInterpolatedU(12.0D);
    	double uvMinV = icon.getInterpolatedV(6.0D);
    	double uvMaxV = icon.getInterpolatedV(16.0D);

    	tessellator.startDrawingQuads();
    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
    	}

    	icon = block.topIcon;
    	uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(4.0D);
    	uvMaxV = icon.getInterpolatedV(12.0D);

		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    	{
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);

    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);

    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
    	}

    	minY = 2.0D / 16.0D;

    	{
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
    	}

    	double indent = 3.0D / 16.0D;
    	
    	icon = block.getIcon(0, 0);
    	uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(8.0D);
    	uvMaxV = icon.getInterpolatedV(14.0D);

    	minX = 4.0D / 16.0D;
    	maxX = 12.0D / 16.0D;
    	minY = 2.0D / 16.0D;
    	maxY = 8.0D / 16.0D;
    	minZ = 4.0D / 16.0D;
    	maxZ = 12.0D / 16.0D;

    	{
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ + indent, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ + indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ + indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ + indent, uvMinU, uvMaxV);

    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ + indent, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ + indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ + indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ + indent, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ - indent, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ - indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ - indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ - indent, uvMinU, uvMaxV);

    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ - indent, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ - indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ - indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ - indent, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(minX + indent, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, minY, maxZ, uvMaxU, uvMaxV);

    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX + indent, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, minY, minZ, uvMinU, uvMaxV);
    	}

    	{
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX - indent, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, minY, minZ, uvMaxU, uvMaxV);

    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX - indent, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, minY, maxZ, uvMinU, uvMaxV);
    	}
    	
    	icon = block.connectorsIcon;
    	uvMinU = icon.getInterpolatedU(5.0D);
    	uvMaxU = icon.getInterpolatedU(11.0D);
    	uvMinV = icon.getInterpolatedV(10.0D);
    	uvMaxV = icon.getInterpolatedV(16.0D);

    	minX = 5.0D / 16.0D;
    	maxX = 11.0D / 16.0D;
    	minY = 10.0D / 16.0D;
    	maxY = 16.0D / 16.0D;
    	minZ = 0.5D;
    	maxZ = 0.5D;

    	{
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);

    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
    	}

    	tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int i, int j, int k, Block bblock, int modelId, RenderBlocks renderer)
	{
		BlockLantern block = (BlockLantern)bblock;

        int brightness = block.getMixedBrightnessForBlock(blockAccess, i, j, k);
    	Tessellator tessellator = Tessellator.instance;

    	double minX = 4.0 / 16.0D;
    	double minY = 0.0 / 16.0D;
    	double minZ = 4.0 / 16.0D;
    	double maxX = 12.0 / 16.0D;
    	double maxY = 10.0 / 16.0D;
    	double maxZ = 12.0 / 16.0D;

    	tessellator.setBrightness(brightness);
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    	tessellator.addTranslation((float)i, (float)j, (float)k);

		IIcon icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(blockAccess, i, j, k, 5);
		double uvMinU = icon.getInterpolatedU(4.0D);
    	double uvMaxU = icon.getInterpolatedU(12.0D);
    	double uvMinV = icon.getInterpolatedV(6.0D);
    	double uvMaxV = icon.getInterpolatedV(16.0D);
    	{
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
    	}
    	
    	icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(blockAccess, i, j, k, 4);
		uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(6.0D);
    	uvMaxV = icon.getInterpolatedV(16.0D);
    	{
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
    	}
    	
    	icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(blockAccess, i, j, k, 3);
		uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(6.0D);
    	uvMaxV = icon.getInterpolatedV(16.0D);
    	{
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
    	}
    	
    	icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(blockAccess, i, j, k, 2);
		uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(6.0D);
    	uvMaxV = icon.getInterpolatedV(16.0D);
    	{
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
    	}
    	
    	icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(blockAccess, i, j, k, 1);
    	uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(4.0D);
    	uvMaxV = icon.getInterpolatedV(12.0D);
    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMaxV);
    	}
    	
    	icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(blockAccess, i, j, k, 0);
    	uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(4.0D);
    	uvMaxV = icon.getInterpolatedV(12.0D);

    	{
    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);
	    	
	    	minY = 2.0D / 16.0D;
	    	
	    	tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
    	}

    	double indent = 3.0D / 16.0D;
    	
    	icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(0, 0);
    	uvMinU = icon.getInterpolatedU(4.0D);
    	uvMaxU = icon.getInterpolatedU(12.0D);
    	uvMinV = icon.getInterpolatedV(8.0D);
    	uvMaxV = icon.getInterpolatedV(14.0D);

    	minX = 4.0D / 16.0D;
    	maxX = 12.0D / 16.0D;
    	minY = 2.0D / 16.0D;
    	maxY = 8.0D / 16.0D;
    	minZ = 4.0D / 16.0D;
    	maxZ = 12.0D / 16.0D;

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ + indent, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ + indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ + indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ + indent, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ + indent, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ + indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ + indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ + indent, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ - indent, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ - indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ - indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ - indent, uvMinU, uvMaxV);

    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ - indent, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ - indent, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ - indent, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ - indent, uvMaxU, uvMaxV);
    	}

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(minX + indent, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, minY, maxZ, uvMaxU, uvMaxV);

    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX + indent, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX + indent, minY, minZ, uvMinU, uvMaxV);
    	}

    	{
    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX - indent, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, minY, minZ, uvMaxU, uvMaxV);

    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX - indent, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX - indent, minY, maxZ, uvMinU, uvMaxV);
    	}
    	
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.connectorsIcon;

		boolean s1 = blockAccess.getBlock(i - 1, j, k).isOpaqueCube();
		boolean s2 = blockAccess.getBlock(i, j, k - 1).isOpaqueCube();
		boolean s3 = blockAccess.getBlock(i + 1, j, k).isOpaqueCube();
		boolean s4 = blockAccess.getBlock(i, j, k + 1).isOpaqueCube();
		
		if(!(blockAccess.getBlock(i, j + 1, k) instanceof BlockGasPipe))
		{
			float handleRot = 0.0F;
	    	boolean renderDoubleHandle = false;

	    	if(blockAccess.getBlock(i, j + 1, k).isOpaqueCube())
	    	{
	    		renderDoubleHandle = true;
	    		handleRot = 45.0F;
	    	} else if(!blockAccess.getBlock(i, j - 1, k).isOpaqueCube())
	    	{
	    		if(s1 & s2 & s3 & s4)
	    		{
	    			handleRot = 45.0F;
	    		} else if((s1 & s2) | (s3 & s4))
	    		{
	    			handleRot = 45.0F;
	    		} else if((s2 & s3) | (s4 & s1))
	    		{
	    			handleRot = -45.0F;
	    		} else if(!s2 & !s4)
	    		{
	    			handleRot = 0.0F;
	    		} else if(!s1 & !s3)
	    		{
	    			handleRot = 90.0F;
	    		}
	    	}

	    	{
	    		double s = Math.sin(handleRot * Math.PI / 180.0D);
	    		double c = Math.cos(handleRot * Math.PI / 180.0D);
	    		double x1 = (8.0D - s * 3.0D) / 16.0D;
	    		double x2 = 1.0D - x1;
	    		double z1 = (8.0D - c * 3.0D) / 16.0D;
	    		double z2 = 1.0D - z1;
	    		double y1 = 10.0D / 16.0D;
	    		double y2 = 16.0D / 16.0D;
	    		
	        	uvMinU = icon.getInterpolatedU(5.0D);
	        	uvMaxU = icon.getInterpolatedU(11.0D);
	        	uvMinV = icon.getInterpolatedV(10.0D);
	        	uvMaxV = icon.getInterpolatedV(16.0D);

	    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	    		tessellator.setNormal((float)c, 0.0F, (float)-s);
	    		tessellator.addVertexWithUV(x1, y1, z1, uvMaxU, uvMaxV);
		    	tessellator.addVertexWithUV(x1, y2, z1, uvMaxU, uvMinV);
		    	tessellator.addVertexWithUV(x2, y2, z2, uvMinU, uvMinV);
		    	tessellator.addVertexWithUV(x2, y1, z2, uvMinU, uvMaxV);

	    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	    		tessellator.setNormal((float)-c, 0.0F, (float)s);
		    	tessellator.addVertexWithUV(x2, y1, z2, uvMinU, uvMaxV);
		    	tessellator.addVertexWithUV(x2, y2, z2, uvMinU, uvMinV);
		    	tessellator.addVertexWithUV(x1, y2, z1, uvMaxU, uvMinV);
		    	tessellator.addVertexWithUV(x1, y1, z1, uvMaxU, uvMaxV);
	    	}

	    	if(renderDoubleHandle)
	    	{
	    		double s = Math.sin((handleRot + 90) * Math.PI / 180.0D);
	    		double c = Math.cos((handleRot + 90) * Math.PI / 180.0D);
	    		double x1 = (8.0D - s * 3.0D) / 16.0D;
	    		double x2 = 1.0D - x1;
	    		double z1 = (8.0D - c * 3.0D) / 16.0D;
	    		double z2 = 1.0D - z1;
	    		double y1 = 16.0D / 16.0D;
	    		double y2 = 12.0D / 16.0D;
	    		
	        	uvMinU = icon.getInterpolatedU(5.0D);
	        	uvMaxU = icon.getInterpolatedU(11.0D);
	        	uvMinV = icon.getInterpolatedV(6.0D);
	        	uvMaxV = icon.getInterpolatedV(10.0D);

	    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	    		tessellator.setNormal((float)c, 0.0F, (float)-s);
	    		tessellator.addVertexWithUV(x1, y1, z1, uvMaxU, uvMinV);
		    	tessellator.addVertexWithUV(x1, y2, z1, uvMaxU, uvMaxV);
		    	tessellator.addVertexWithUV(x2, y2, z2, uvMinU, uvMaxV);
		    	tessellator.addVertexWithUV(x2, y1, z2, uvMinU, uvMinV);

	    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	    		tessellator.setNormal((float)-c, 0.0F, (float)s);
		    	tessellator.addVertexWithUV(x2, y1, z2, uvMinU, uvMinV);
		    	tessellator.addVertexWithUV(x2, y2, z2, uvMinU, uvMaxV);
		    	tessellator.addVertexWithUV(x1, y2, z1, uvMaxU, uvMaxV);
		    	tessellator.addVertexWithUV(x1, y1, z1, uvMaxU, uvMinV);
	    	}
	    	else if(!blockAccess.getBlock(i, j - 1, k).isOpaqueCube())
	    	{
	    		uvMinV = icon.getInterpolatedV(0.0D);
	        	uvMaxV = icon.getInterpolatedV(1.0D);
	    		minY = 13.0D / 16.0D;
		        maxY = 14.0D / 16.0D;

		        if(s1 | s3)
	    		{
	    			uvMinU = icon.getInterpolatedU(0.0D);
		        	uvMaxU = icon.getInterpolatedU(16.0D);

		        	minZ = 7.5D / 16.0D;
		        	maxZ = 1.0D - minZ;

		        	if(s1)
		        	{
		    			uvMinU = icon.getInterpolatedU(0.0D);
		        		minX = 0.0D;
		        	} else
		        	{
		    			uvMinU = icon.getInterpolatedU(7.0D);
		        		minX = 6.0D / 16.0D;

		        		double uv2MinU = icon.getInterpolatedU(0.0D);
		        		double uv2MaxU = icon.getInterpolatedU(1.0D);
		        		double uv2MinV = icon.getInterpolatedV(0.0D);
		        		double uv2MaxV = icon.getInterpolatedV(1.0D);

		        		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		        		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		        		tessellator.addVertexWithUV(minX, minY, maxZ, uv2MinU, uv2MaxV);
		    	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uv2MinU, uv2MinV);
		    	    	tessellator.addVertexWithUV(minX, maxY, minZ, uv2MaxU, uv2MinV);
		    	    	tessellator.addVertexWithUV(minX, minY, minZ, uv2MaxU, uv2MaxV);
		        	}

		        	if(s3)
		        	{
		    			uvMaxU = icon.getInterpolatedU(16.0D);
		        		maxX = 1.0D;
		        	} else
		        	{
		    			uvMaxU = icon.getInterpolatedU(9.0D);
		        		maxX = 10.0D / 16.0D;

		        		double uv2MinU = icon.getInterpolatedU(0.0D);
		        		double uv2MaxU = icon.getInterpolatedU(1.0D);
		        		double uv2MinV = icon.getInterpolatedV(0.0D);
		        		double uv2MaxV = icon.getInterpolatedV(1.0D);

		        		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		        		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		        		tessellator.addVertexWithUV(maxX, minY, minZ, uv2MinU, uv2MaxV);
		    	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uv2MinU, uv2MinV);
		    	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uv2MaxU, uv2MinV);
		    	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uv2MaxU, uv2MaxV);
		        	}
					
					tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
			    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
			    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
			    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
			    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);
			    	
		        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
			    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMaxV);
			    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMinV);
		    		tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMinV);
			    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMaxV);

		        	tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
			    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMinV);
			    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMinV);
			    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);
			    	
		        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
			    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
			    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
			    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
	    		}

		        if(s2 | s4)
	    		{
	    			uvMinU = icon.getInterpolatedU(0.0D);
		        	uvMaxU = icon.getInterpolatedU(16.0D);

		        	minX = 7.5D / 16.0D;
		        	maxX = 1.0D - minX;

		        	if(s2)
		        	{
		    			uvMinU = icon.getInterpolatedU(0.0D);
		        		minZ = 0.0D;
		        	} else
		        	{
		    			uvMinU = icon.getInterpolatedU(7.0D);
		        		minZ = 6.0D / 16.0D;

		        		double uv2MinU = icon.getInterpolatedU(0.0D);
		        		double uv2MaxU = icon.getInterpolatedU(1.0D);
		        		double uv2MinV = icon.getInterpolatedV(0.0D);
		        		double uv2MaxV = icon.getInterpolatedV(1.0D);

			        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		        		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		        		tessellator.addVertexWithUV(minX, minY, minZ, uv2MaxU, uv2MaxV);
		    	    	tessellator.addVertexWithUV(minX, maxY, minZ, uv2MaxU, uv2MinV);
		    	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uv2MinU, uv2MinV);
		    	    	tessellator.addVertexWithUV(maxX, minY, minZ, uv2MinU, uv2MaxV);
		        	}

		        	if(s4)
		        	{
		    			uvMaxU = icon.getInterpolatedU(16.0D);
		        		maxZ = 1.0D;
		        	} else
		        	{
		    			uvMaxU = icon.getInterpolatedU(9.0D);
		        		maxZ = 10.0D / 16.0D;

		        		double uv2MinU = icon.getInterpolatedU(0.0D);
		        		double uv2MaxU = icon.getInterpolatedU(1.0D);
		        		double uv2MinV = icon.getInterpolatedV(0.0D);
		        		double uv2MaxV = icon.getInterpolatedV(1.0D);

			        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		        		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		        		tessellator.addVertexWithUV(maxX, minY, maxZ, uv2MaxU, uv2MaxV);
		    	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uv2MaxU, uv2MinV);
		    	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uv2MinU, uv2MinV);
		    	    	tessellator.addVertexWithUV(minX, minY, maxZ, uv2MinU, uv2MaxV);
		        	}
		        	
		        	tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	        		tessellator.setNormal(0.0F, 1.0F, 0.0F);
			    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMaxV);
			    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMaxV);
			    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
			    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);

		        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
	        		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	        		tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
	    	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);

		        	tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
	        		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
			    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
			    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMinV);
			    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMinV);

		        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
	        		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	        		tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
	    	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
	    		}
	    	}
		}
		
		uvMinU = icon.getInterpolatedU(0.0D);
		uvMinV = icon.getInterpolatedV(1.0D);
		uvMaxV = icon.getInterpolatedV(3.0D);
		
		minX = 7.0D / 16.0D;
		maxX = 9.0D / 16.0D;
		minZ = 7.0D / 16.0D;
		maxZ = 9.0D / 16.0D;
		
		if(blockAccess.getBlock(i, j + 1, k) instanceof BlockGasPipe)
		{
			uvMaxU = icon.getInterpolatedU(12.0D);
			minY = 10.0D / 16.0D;
			maxY = 22.0D / 16.0D;
			
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMinV);
	    	
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMinV);

    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMinV);

    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMinV);
		}
		
		if(blockAccess.getBlock(i, j - 1, k) instanceof BlockGasPipe)
		{
			uvMaxU = icon.getInterpolatedU(6.0D);
			minY = -6.0D / 16.0D;
			maxY = 0.0D / 16.0D;
			
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);

    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);

    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
		}
		
		uvMaxU = icon.getInterpolatedU(10.0D);
		
		minY = 7.0D / 16.0D;
		maxY = 9.0D / 16.0D;
		minZ = 7.0D / 16.0D;
		maxZ = 9.0D / 16.0D;
		
		if(blockAccess.getBlock(i + 1, j, k) instanceof BlockGasPipe)
		{
			minX = 12.0D / 16.0D;
			maxX = 22.0D / 16.0D;
			
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);
	    	
        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMaxV);

        	tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);
	    	
        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
		}
		
		if(blockAccess.getBlock(i - 1, j, k) instanceof BlockGasPipe)
		{
			minX = -6.0D / 16.0D;
			maxX = 4.0D / 16.0D;
			
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	
        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);
    		tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);

        	tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMinV);
	    	
        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMinV);
		}
		
		minX = 7.0D / 16.0D;
		maxX = 9.0D / 16.0D;
		
		if(blockAccess.getBlock(i, j, k + 1) instanceof BlockGasPipe)
		{
			minZ = 12.0D / 16.0D;
			maxZ = 22.0D / 16.0D;
			
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMaxV);

        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMaxV);

        	tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMaxU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMinU, uvMinV);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMaxV);

        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMinU, uvMaxV);
		}
		
		if(blockAccess.getBlock(i, j, k - 1) instanceof BlockGasPipe)
		{
			minZ = -6.0D / 16.0D;
			maxZ = 4.0D / 16.0D;
			
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMinV);

        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, maxY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, maxY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMinV);

        	tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(minX, minY, maxZ, uvMinU, uvMaxV);
	    	tessellator.addVertexWithUV(minX, minY, minZ, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMinV);

        	tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	tessellator.addVertexWithUV(maxX, maxY, maxZ, uvMinU, uvMinV);
	    	tessellator.addVertexWithUV(maxX, minY, maxZ, uvMinU, uvMaxV);
    		tessellator.addVertexWithUV(maxX, minY, minZ, uvMaxU, uvMaxV);
	    	tessellator.addVertexWithUV(maxX, maxY, minZ, uvMaxU, uvMinV);
		}

    	tessellator.addTranslation((float)-i, (float)-j, (float)-k);

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