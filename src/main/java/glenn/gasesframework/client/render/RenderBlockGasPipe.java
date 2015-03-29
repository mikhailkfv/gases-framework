package glenn.gasesframework.client.render;

import glenn.gasesframework.api.block.IGasInterface;
import glenn.gasesframework.common.block.BlockGasPipe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBlockGasPipe implements ISimpleBlockRenderingHandler
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	@Override
	public void renderInventoryBlock(Block bblock, int metadata, int modelID,RenderBlocks renderer)
	{
		BlockGasPipe block = (BlockGasPipe)bblock;
		BlockGasPipe.SubType subType = block.subTypes[metadata];
		
		Tessellator tessellator = Tessellator.instance;

        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.4F, 1.4F, 1.4F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        
		double d1 = 6.0F / 16.0F;
		double d2 = 10.0F / 16.0F;

        tessellator.startDrawingQuads();
    	
        IIcon icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
        double uvMinU = icon.getMinU();
        double uvMaxU = icon.getMaxU();
        double uvMinV = icon.getMinV();
        double uvMaxV = icon.getMaxV();
        
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, d2, 1.0D, uvMinU, uvMinV);
		tessellator.addVertexWithUV(1.0D, d2, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(1.0D, d2, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(0.0D, d2, 0.0D, uvMinU, uvMaxV);
    	
        icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
        uvMinU = icon.getMinU();
        uvMaxU = icon.getMaxU();
        uvMinV = icon.getMinV();
        uvMaxV = icon.getMaxV();

        tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, d1, 0.0D, uvMinU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, d1, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, d1, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(0.0D, d1, 1.0D, uvMinU, uvMinV);
		
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
        uvMinU = icon.getMinU();
        uvMaxU = icon.getMaxU();
        uvMinV = icon.getMinV();
        uvMaxV = icon.getMaxV();
        
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(d1, 0.0D, 1.0D, uvMinU, uvMinV);
		tessellator.addVertexWithUV(d1, 1.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(d1, 1.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(d1, 0.0D, 0.0D, uvMinU, uvMaxV);
		
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
        uvMinU = icon.getMinU();
        uvMaxU = icon.getMaxU();
        uvMinV = icon.getMinV();
        uvMaxV = icon.getMaxV();

        tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(d2, 0.0D, 0.0D, uvMinU, uvMaxV);
		tessellator.addVertexWithUV(d2, 1.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(d2, 1.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(d2, 0.0D, 1.0D, uvMinU, uvMinV);
		
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
        uvMinU = icon.getMinU();
        uvMaxU = icon.getMaxU();
        uvMinV = icon.getMinV();
        uvMaxV = icon.getMaxV();

        tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0D, 1.0D, d1, uvMinU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 1.0D, d1, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 0.0D, d1, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(0.0D, 0.0D, d1, uvMinU, uvMaxV);
		
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
        uvMinU = icon.getMinU();
        uvMaxU = icon.getMaxU();
        uvMinV = icon.getMinV();
        uvMaxV = icon.getMaxV();

        tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, d2, uvMinU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 0.0D, d2, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 1.0D, d2, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(0.0D, 1.0D, d2, uvMinU, uvMinV);
		
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.endIcon;
        uvMinU = icon.getMinU();
        uvMaxU = icon.getMaxU();
        uvMinV = icon.getMinV();
        uvMaxV = icon.getMaxV();
    	
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uvMinU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uvMinU, uvMaxV);
		
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uvMinU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uvMinU, uvMinV);
	
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uvMinU, uvMinV);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uvMinU, uvMaxV);

        tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uvMinU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uvMinU, uvMinV);

        tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, uvMinU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, uvMinU, uvMaxV);

        tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, uvMinU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, uvMaxU, uvMaxV);
		tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, uvMaxU, uvMinV);
		tessellator.addVertexWithUV(0.0D, 1.0D, 1.0D, uvMinU, uvMinV);

        tessellator.draw();

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		
        GL11.glDisable(GL11.GL_ALPHA_TEST);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block bblock, int modelId, RenderBlocks renderer)
	{
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		
		BlockGasPipe block = (BlockGasPipe)bblock;
		BlockGasPipe.SubType subType = block.subTypes[metadata];
		
        int brightness = block.getMixedBrightnessForBlock(blockAccess, x, y, z);
		Tessellator tessellator = Tessellator.instance;
		
		int color = block.type.color;
		float gasRed = (float)((color >> 24) & 0xFF) / 255.0F;
		float gasGreen = (float)((color >> 16) & 0xFF) / 255.0F;
		float gasBlue = (float)((color >> 8) & 0xFF) / 255.0F;
		float gasAlpha = (float)(color & 0xFF) / 255.0f;
		
		final boolean[] sidePipe = new boolean[6];
		final boolean[] sideOpaque = new boolean[6];
		
		for(int i = 0; i < 6; i++)
		{
			ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[i];
			int x1 = x + side.offsetX;
			int y1 = y + side.offsetY;
			int z1 = z + side.offsetZ;
			
			Block directionBlock = blockAccess.getBlock(x1, y1, z1);
			if(directionBlock != Blocks.air)
			{
				sidePipe[i] = directionBlock instanceof IGasInterface ? ((IGasInterface)directionBlock).connectToPipe(blockAccess, x1, y1, z1, side.getOpposite()) : false;
				sideOpaque[i] = directionBlock.isOpaqueCube() & !sidePipe[i];
			}
		}
		
		double d1 = 6.0F / 16.0F;
		double d2 = 10.0F / 16.0F;
		double d3 = 4.5F / 16.0F;
		double d4 = 11.5F / 16.0F;
		
		boolean collectionAll = sidePipe[0] || sidePipe[1] || sidePipe[2] || sidePipe[3] || sidePipe[4] || sidePipe[5];
		boolean collectionY = sidePipe[2] || sidePipe[3] || sidePipe[4] || sidePipe[5];
		boolean collectionX = sidePipe[0] || sidePipe[1] || sidePipe[2] || sidePipe[3];
		boolean collectionZ = sidePipe[0] || sidePipe[1] || sidePipe[4] || sidePipe[5];
		
		double minX = (sidePipe[4] | !collectionX) & collectionAll ? 0.0F : d1;
		double maxX = (sidePipe[5] | !collectionX) & collectionAll ? 1.0F : d2;
		double minY = (sidePipe[0] | !collectionY) & collectionAll ? 0.0F : d1;
		double maxY = (sidePipe[1] | !collectionY) & collectionAll ? 1.0F : d2;
		double minZ = (sidePipe[2] | !collectionZ) & collectionAll ? 0.0F : d1;
		double maxZ = (sidePipe[3] | !collectionZ) & collectionAll ? 1.0F : d2;

    	tessellator.setBrightness(brightness);
    	tessellator.addTranslation((float)x, (float)y, (float)z);
    	
		IIcon icon;
		double uvMinU;
		double uvMaxU;
		double uvMinV;
		double uvMaxV;
    	
    	tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    	
    	if(collectionY | !collectionAll)
    	{
    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
    		uvMinU = icon.getInterpolatedU(minX * 16.0D);
    		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
    		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
    		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);
    		
    		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
    		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, d1, minZ, uvMinU, uvMinV);
    		tessellator.addVertexWithUV(maxX, d1, minZ, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(maxX, d1, maxZ, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(minX, d1, maxZ, uvMinU, uvMaxV);
    		
    		if(!subType.isSolid && block.type.isVisible())
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.gasContentIcon;
        		uvMinU = icon.getInterpolatedU(minX * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
        		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);
        		
        		tessellator.setColorOpaque_F(0.6F * gasRed, 0.6F * gasGreen, 0.6F * gasBlue);
        		tessellator.addVertexWithUV(minX, d1, minZ, uvMinU, uvMinV);
        		tessellator.addVertexWithUV(maxX, d1, minZ, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(maxX, d1, maxZ, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(minX, d1, maxZ, uvMinU, uvMaxV);
    		}
    		
    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
    		uvMinU = icon.getInterpolatedU(minX * 16.0D);
    		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
    		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
    		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);

    		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    		tessellator.setNormal(0.0F, 1.0F, 0.0F);
    		tessellator.addVertexWithUV(minX, d2, maxZ, uvMinU, uvMaxV);
    		tessellator.addVertexWithUV(maxX, d2, maxZ, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(maxX, d2, minZ, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(minX, d2, minZ, uvMinU, uvMinV);
    		
    		if(!subType.isSolid && block.type.isVisible())
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.gasContentIcon;
        		uvMinU = icon.getInterpolatedU(minX * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
        		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);

        		tessellator.setColorOpaque_F(1.0F * gasRed, 1.0F * gasGreen, 1.0F * gasBlue);
        		tessellator.addVertexWithUV(minX, d2, maxZ, uvMinU, uvMaxV);
        		tessellator.addVertexWithUV(maxX, d2, maxZ, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(maxX, d2, minZ, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(minX, d2, minZ, uvMinU, uvMinV);
    		}
    	}

    	if(collectionX | !collectionAll)
    	{
    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
    		uvMinU = icon.getInterpolatedU(minY * 16.0D);
    		uvMaxU = icon.getInterpolatedU(maxY * 16.0D);
    		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
    		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);
    		
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(d1, minY, maxZ, uvMinU, uvMaxV);
    		tessellator.addVertexWithUV(d1, maxY, maxZ, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(d1, maxY, minZ, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(d1, minY, minZ, uvMinU, uvMinV);
    		
    		if(!subType.isSolid && block.type.isVisible())
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.gasContentIcon;
        		uvMinU = icon.getInterpolatedU(minY * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxY * 16.0D);
        		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);
        		
        		tessellator.setColorOpaque_F(0.8F * gasRed, 0.8F * gasGreen, 0.8F * gasBlue);
        		tessellator.addVertexWithUV(d1, minY, maxZ, uvMinU, uvMaxV);
        		tessellator.addVertexWithUV(d1, maxY, maxZ, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(d1, maxY, minZ, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(d1, minY, minZ, uvMinU, uvMinV);
    		}

    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
    		uvMinU = icon.getInterpolatedU(minY * 16.0D);
    		uvMaxU = icon.getInterpolatedU(maxY * 16.0D);
    		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
    		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);
    		
    		tessellator.setNormal(1.0F, 0.0F, 0.0F);
    		tessellator.addVertexWithUV(d2, minY, minZ, uvMinU, uvMinV);
    		tessellator.addVertexWithUV(d2, maxY, minZ, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(d2, maxY, maxZ, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(d2, minY, maxZ, uvMinU, uvMaxV);
    		
    		if(!subType.isSolid && block.type.isVisible())
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.gasContentIcon;
        		uvMinU = icon.getInterpolatedU(minY * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxY * 16.0D);
        		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);

        		tessellator.setColorOpaque_F(0.8F * gasRed, 0.8F * gasGreen, 0.8F * gasBlue);
        		tessellator.addVertexWithUV(d2, minY, minZ, uvMinU, uvMinV);
        		tessellator.addVertexWithUV(d2, maxY, minZ, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(d2, maxY, maxZ, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(d2, minY, maxZ, uvMinU, uvMaxV);
    		}
    	}
    	
    	if(collectionZ | !collectionAll)
    	{
    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
    		uvMinU = icon.getInterpolatedU(minX * 16.0D);
    		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
    		uvMinV = icon.getInterpolatedV(minY * 16.0D);
    		uvMaxV = icon.getInterpolatedV(maxY * 16.0D);
    		
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    		tessellator.addVertexWithUV(minX, maxY, d1, uvMinU, uvMaxV);
    		tessellator.addVertexWithUV(maxX, maxY, d1, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(maxX, minY, d1, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(minX, minY, d1, uvMinU, uvMinV);
    		
    		if(!subType.isSolid && block.type.isVisible())
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.gasContentIcon;
        		uvMinU = icon.getInterpolatedU(minX * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
        		uvMinV = icon.getInterpolatedV(minY * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxY * 16.0D);
        		
        		tessellator.setColorOpaque_F(0.8F * gasRed, 0.8F * gasGreen, 0.8F * gasBlue);
        		tessellator.addVertexWithUV(minX, maxY, d1, uvMinU, uvMaxV);
        		tessellator.addVertexWithUV(maxX, maxY, d1, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(maxX, minY, d1, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(minX, minY, d1, uvMinU, uvMinV);
    		}

    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.solidIcon;
    		uvMinU = icon.getInterpolatedU(minX * 16.0D);
    		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
    		uvMinV = icon.getInterpolatedV(minY * 16.0D);
    		uvMaxV = icon.getInterpolatedV(maxY * 16.0D);
    		
    		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    		tessellator.addVertexWithUV(minX, minY, d2, uvMinU, uvMinV);
    		tessellator.addVertexWithUV(maxX, minY, d2, uvMaxU, uvMinV);
    		tessellator.addVertexWithUV(maxX, maxY, d2, uvMaxU, uvMaxV);
    		tessellator.addVertexWithUV(minX, maxY, d2, uvMinU, uvMaxV);
    		
    		if(!subType.isSolid && block.type.isVisible())
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.gasContentIcon;
        		uvMinU = icon.getInterpolatedU(minX * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
        		uvMinV = icon.getInterpolatedV(minY * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxY * 16.0D);

        		tessellator.setColorOpaque_F(0.8F * gasRed, 0.8F * gasGreen, 0.8F * gasBlue);
        		tessellator.addVertexWithUV(minX, minY, d2, uvMinU, uvMinV);
        		tessellator.addVertexWithUV(maxX, minY, d2, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(maxX, maxY, d2, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(minX, maxY, d2, uvMinU, uvMaxV);
    		}
    	}
    	
    	if(collectionAll)
    	{
    		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.endIcon;
    		uvMinU = icon.getInterpolatedU(6.0D);
    		uvMaxU = icon.getInterpolatedU(10.0D);
    		uvMinV = icon.getInterpolatedV(6.0D);
    		uvMaxV = icon.getInterpolatedV(10.0D);
        	
    		if(maxY == 1.0D)
    		{
        		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        		tessellator.setNormal(0.0F, 1.0F, 0.0F);
    			tessellator.addVertexWithUV(d1, maxY, d2, uvMinU, uvMaxV);
        		tessellator.addVertexWithUV(d2, maxY, d2, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(d2, maxY, d1, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(d1, maxY, d1, uvMinU, uvMinV);
    		}
    		
    		if(minY == 0.0D)
    		{
        		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
        		tessellator.setNormal(0.0F, -1.0F, 0.0F);
    			tessellator.addVertexWithUV(d1, minY, d1, uvMinU, uvMinV);
        		tessellator.addVertexWithUV(d2, minY, d1, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(d2, minY, d2, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(d1, minY, d2, uvMinU, uvMaxV);
    		}
    	
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		if(minX == 0.0D)
    		{
        		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
    			tessellator.addVertexWithUV(minX, d1, d2, uvMinU, uvMaxV);
        		tessellator.addVertexWithUV(minX, d2, d2, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(minX, d2, d1, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(minX, d1, d1, uvMinU, uvMinV);
    		}
    		
    		if(maxX == 1.0D)
    		{
        		tessellator.setNormal(1.0F, 0.0F, 0.0F);
    			tessellator.addVertexWithUV(maxX, d1, d1, uvMinU, uvMinV);
        		tessellator.addVertexWithUV(maxX, d2, d1, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(maxX, d2, d2, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(maxX, d1, d2, uvMinU, uvMaxV);
    		}
    	
    		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
    		if(minZ == 0.0D)
    		{
        		tessellator.setNormal(0.0F, 0.0F, -1.0F);
    			tessellator.addVertexWithUV(d1, d2, minZ, uvMinU, uvMaxV);
        		tessellator.addVertexWithUV(d2, d2, minZ, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(d2, d1, minZ, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(d1, d1, minZ, uvMinU, uvMinV);
    		}
    		
    		if(maxZ == 1.0D)
    		{
        		tessellator.setNormal(0.0F, 0.0F, 1.0F);
    			tessellator.addVertexWithUV(d1, d1, maxZ, uvMinU, uvMinV);
        		tessellator.addVertexWithUV(d2, d1, maxZ, uvMaxU, uvMinV);
        		tessellator.addVertexWithUV(d2, d2, maxZ, uvMaxU, uvMaxV);
        		tessellator.addVertexWithUV(d1, d2, maxZ, uvMinU, uvMaxV);
    		}
    		
    		if(((x ^ y ^ z) & 1) > 0)
    		{
    			icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : subType.connectorsIcon;
            	
            	minX = sideOpaque[4] ? 0.0F : d1;
        		maxX = sideOpaque[5] ? 1.0F : d2;
        		minY = sideOpaque[0] ? 0.0F : d1;
        		maxY = sideOpaque[1] ? 1.0F : d2;
        		minZ = sideOpaque[2] ? 0.0F : d1;
        		maxZ = sideOpaque[3] ? 1.0F : d2;
        		
        		uvMinU = icon.getInterpolatedU(minX * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
        		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);
        		
        		if(sidePipe[0] | !collectionY)
            	{
            		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
            		tessellator.setNormal(0.0F, -1.0F, 0.0F);
            		tessellator.addVertexWithUV(minX, d3, minZ, uvMinU, uvMinV);
            		tessellator.addVertexWithUV(maxX, d3, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(maxX, d3, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(minX, d3, maxZ, uvMinU, uvMaxV);

            		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            		tessellator.setNormal(0.0F, 1.0F, 0.0F);
            		tessellator.addVertexWithUV(minX, d3, maxZ, uvMinU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, d3, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, d3, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(minX, d3, minZ, uvMinU, uvMinV);
            	}
        		if(sidePipe[1] | !collectionY)
            	{
            		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
            		tessellator.setNormal(0.0F, -1.0F, 0.0F);
            		tessellator.addVertexWithUV(minX, d4, minZ, uvMinU, uvMinV);
            		tessellator.addVertexWithUV(maxX, d4, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(maxX, d4, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(minX, d4, maxZ, uvMinU, uvMaxV);

            		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            		tessellator.setNormal(0.0F, 1.0F, 0.0F);
            		tessellator.addVertexWithUV(minX, d4, maxZ, uvMinU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, d4, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, d4, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(minX, d4, minZ, uvMinU, uvMinV);
            	}
        		
        		uvMinU = icon.getInterpolatedU(minX * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxX * 16.0D);
        		uvMinV = icon.getInterpolatedV(minY * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxY * 16.0D);
            	
            	if(sidePipe[2] | !collectionZ)
            	{
            		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
            		tessellator.setNormal(0.0F, 0.0F, -1.0F);
            		tessellator.addVertexWithUV(minX, maxY, d3, uvMinU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, maxY, d3, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, minY, d3, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(minX, minY, d3, uvMinU, uvMinV);

            		tessellator.setNormal(0.0F, 0.0F, 1.0F);
            		tessellator.addVertexWithUV(minX, minY, d3, uvMinU, uvMinV);
            		tessellator.addVertexWithUV(maxX, minY, d3, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(maxX, maxY, d3, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(minX, maxY, d3, uvMinU, uvMaxV);
            	}
            	if(sidePipe[3] | !collectionZ)
            	{
            		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
            		tessellator.setNormal(0.0F, 0.0F, -1.0F);
            		tessellator.addVertexWithUV(minX, maxY, d4, uvMinU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, maxY, d4, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(maxX, minY, d4, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(minX, minY, d4, uvMinU, uvMinV);

            		tessellator.setNormal(0.0F, 0.0F, 1.0F);
            		tessellator.addVertexWithUV(minX, minY, d4, uvMinU, uvMinV);
            		tessellator.addVertexWithUV(maxX, minY, d4, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(maxX, maxY, d4, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(minX, maxY, d4, uvMinU, uvMaxV);
            	}
        		
        		uvMinU = icon.getInterpolatedU(minY * 16.0D);
        		uvMaxU = icon.getInterpolatedU(maxY * 16.0D);
        		uvMinV = icon.getInterpolatedV(minZ * 16.0D);
        		uvMaxV = icon.getInterpolatedV(maxZ * 16.0D);

            	if(sidePipe[4] | !collectionX)
            	{
            		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
            		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            		tessellator.addVertexWithUV(d3, minY, maxZ, uvMinU, uvMaxV);
            		tessellator.addVertexWithUV(d3, maxY, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(d3, maxY, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(d3, minY, minZ, uvMinU, uvMinV);

            		tessellator.setNormal(1.0F, 0.0F, 0.0F);
            		tessellator.addVertexWithUV(d3, minY, minZ, uvMinU, uvMinV);
            		tessellator.addVertexWithUV(d3, maxY, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(d3, maxY, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(d3, minY, maxZ, uvMinU, uvMaxV);
            	}
        		if(sidePipe[5] | !collectionX)
            	{
            		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
            		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            		tessellator.addVertexWithUV(d4, minY, maxZ, uvMinU, uvMaxV);
            		tessellator.addVertexWithUV(d4, maxY, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(d4, maxY, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(d4, minY, minZ, uvMinU, uvMinV);

            		tessellator.setNormal(1.0F, 0.0F, 0.0F);
            		tessellator.addVertexWithUV(d4, minY, minZ, uvMinU, uvMinV);
            		tessellator.addVertexWithUV(d4, maxY, minZ, uvMaxU, uvMinV);
            		tessellator.addVertexWithUV(d4, maxY, maxZ, uvMaxU, uvMaxV);
            		tessellator.addVertexWithUV(d4, minY, maxZ, uvMinU, uvMaxV);
            	}
    		}
    	}
    	
    	tessellator.addTranslation((float)-x, (float)-y, (float)-z);
		
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