package glenn.gasesframework.client.render;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.block.BlockGas;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderBlockGas implements ISimpleBlockRenderingHandler
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	private IIcon icon;
	private Tessellator tessellator;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
	
		float red = 1.0F;
		float green = 1.0F;
		float blue = 1.0F;
		float alpha = 1.0F;
	
		if (renderer.useInventoryTint)
	    {
	        int color = block.getRenderColor(metadata);
	
	        red = (float)((color >> 24) & 0xFF) / 255.0F;
			green = (float)((color >> 16) & 0xFF) / 255.0F;
			blue = (float)((color >> 8) & 0xFF) / 255.0F;
			//alpha = (float)(color & 0xFF) / 255.0f;
	    }
	
	    renderer.setRenderBoundsFromBlock(block);
	    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
	    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	    GL11.glColor4f(red, green, blue, alpha);
	
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
	
	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int i, int j, int k, Block bblock, int modelId, RenderBlocks renderer)
	{
		BlockGas block = (BlockGas)bblock;
		
		int metadata = blockAccess.getBlockMetadata(i, j, k);
		Block sideBlock;
		int sideBlockMetadata;
	
		icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : renderer.getBlockIcon(block);
	    int brightness = block.getMixedBrightnessForBlock(blockAccess, i, j, k);
		tessellator = Tessellator.instance;
		int color = block.colorMultiplier(blockAccess, i, j, k);
		
		float red = (float)((color >> 24) & 0xFF) / 255.0F;
		float green = (float)((color >> 16) & 0xFF) / 255.0F;
		float blue = (float)((color >> 8) & 0xFF) / 255.0F;
		float alpha = (float)(color & 0xFF) / 255.0f;
	
		double minX = block.sideIndent(blockAccess, i - 1, j, k);
		double maxX = 1.0D - block.sideIndent(blockAccess, i + 1, j, k);
		
		double minY = block.type.getMinY(blockAccess, i, j, k, metadata) + block.sideIndent(blockAccess, i, j - 1, k);
		double maxY = block.type.getMaxY(blockAccess, i, j, k, metadata) - block.sideIndent(blockAccess, i, j + 1, k);
		
		double minZ = block.sideIndent(blockAccess, i, j, k - 1);
		double maxZ = 1.0D - block.sideIndent(blockAccess, i, j, k + 1);
		
		double sideMinY;
		double sideMaxY;
	
		tessellator.setBrightness(brightness);
		tessellator.addTranslation((float)i, (float)j, (float)k);

		//tessellator.setColorOpaque_F(red * 0.9F, green * 0.9F, blue * 0.9F);
		tessellator.setColorRGBA_F(red * 0.9F, green * 0.9F, blue * 0.9F, alpha);
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 3))
		{
			sideBlock = blockAccess.getBlock(i, j, k - 1);
			if(sideBlock == block)
			{
	    		sideBlockMetadata = blockAccess.getBlockMetadata(i, j, k - 1);
				sideMinY = ((BlockGas)sideBlock).type.getMinY(blockAccess, i, j, k - 1, sideBlockMetadata);
				sideMaxY = ((BlockGas)sideBlock).type.getMaxY(blockAccess, i, j, k - 1, sideBlockMetadata);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
				    tessellator.setNormal(0.0F, 0.0F, -1.0F);
					vertexAutoMap(minX, minY, minZ, maxX, minY);
			    	vertexAutoMap(minX, maxY, minZ, maxX, maxY);
			    	vertexAutoMap(maxX, maxY, minZ, minX, maxY);
			    	vertexAutoMap(maxX, minY, minZ, minX, minY);
				    tessellator.setNormal(0.0F, 0.0F, 1.0F);
			    	vertexAutoMap(maxX, minY, minZ, minX, minY);
			    	vertexAutoMap(maxX, maxY, minZ, minX, maxY);
			    	vertexAutoMap(minX, maxY, minZ, maxX, maxY);
					vertexAutoMap(minX, minY, minZ, maxX, minY);
				}
				else
				{
					if(minY < sideMinY)
	    			{
					    tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    				vertexAutoMap(minX, minY, minZ, maxX, minY);
	    		    	vertexAutoMap(minX, sideMinY, minZ, maxX, sideMinY);
	    		    	vertexAutoMap(maxX, sideMinY, minZ, minX, sideMinY);
	    		    	vertexAutoMap(maxX, minY, minZ, minX, minY);
	    			    tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    		    	vertexAutoMap(maxX, minY, minZ, minX, minY);
	    		    	vertexAutoMap(maxX, sideMinY, minZ, minX, sideMinY);
	    		    	vertexAutoMap(minX, sideMinY, minZ, maxX, sideMinY);
	    				vertexAutoMap(minX, minY, minZ, maxX, minY);
	    			}
	    			
	    			if(maxY > sideMaxY)
	    			{
	    			    tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    				vertexAutoMap(minX, sideMaxY, minZ, maxX, sideMaxY);
	    		    	vertexAutoMap(minX, maxY, minZ, maxX, maxY);
	    		    	vertexAutoMap(maxX, maxY, minZ, minX, maxY);
	    		    	vertexAutoMap(maxX, sideMaxY, minZ, minX, sideMaxY);
	    			    tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    		    	vertexAutoMap(maxX, sideMaxY, minZ, minX, sideMaxY);
	    		    	vertexAutoMap(maxX, maxY, minZ, minX, maxY);
	    		    	vertexAutoMap(minX, maxY, minZ, maxX, maxY);
	    				vertexAutoMap(minX, sideMaxY, minZ, maxX, sideMaxY);
	    			}
				}
			}
			else
			{
			    tessellator.setNormal(0.0F, 0.0F, -1.0F);
				vertexAutoMap(minX, minY, minZ, maxX, minY);
		    	vertexAutoMap(minX, maxY, minZ, maxX, maxY);
		    	vertexAutoMap(maxX, maxY, minZ, minX, maxY);
		    	vertexAutoMap(maxX, minY, minZ, minX, minY);
			    tessellator.setNormal(0.0F, 0.0F, 1.0F);
		    	vertexAutoMap(maxX, minY, minZ, minX, minY);
		    	vertexAutoMap(maxX, maxY, minZ, minX, maxY);
		    	vertexAutoMap(minX, maxY, minZ, maxX, maxY);
				vertexAutoMap(minX, minY, minZ, maxX, minY);
			}
			
		}
		
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 2))
		{
			sideBlock = blockAccess.getBlock(i, j, k + 1);
			if(sideBlock == block)
			{
	    		sideBlockMetadata = blockAccess.getBlockMetadata(i, j, k + 1);
				sideMinY = ((BlockGas)sideBlock).type.getMinY(blockAccess, i, j, k + 1, sideBlockMetadata);
				sideMaxY = ((BlockGas)sideBlock).type.getMaxY(blockAccess, i, j, k + 1, sideBlockMetadata);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
				    tessellator.setNormal(0.0F, 0.0F, 1.0F);
					vertexAutoMap(maxX, minY, maxZ, maxX, minY);
		    		vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
		    		vertexAutoMap(minX, maxY, maxZ, minX, maxY);
		    		vertexAutoMap(minX, minY, maxZ, minX, minY);
				    tessellator.setNormal(0.0F, 0.0F, -1.0F);
		    		vertexAutoMap(minX, minY, maxZ, minX, minY);
		    		vertexAutoMap(minX, maxY, maxZ, minX, maxY);
		    		vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
					vertexAutoMap(maxX, minY, maxZ, maxX, minY);
				}
				else
				{
					if(minY < sideMinY)
	    			{
					    tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    				vertexAutoMap(maxX, minY, maxZ, maxX, minY);
	    	    		vertexAutoMap(maxX, sideMinY, maxZ, maxX, sideMinY);
	    	    		vertexAutoMap(minX, sideMinY, maxZ, minX, sideMinY);
	    	    		vertexAutoMap(minX, minY, maxZ, minX, minY);
					    tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	    		vertexAutoMap(minX, minY, maxZ, minX, minY);
	    	    		vertexAutoMap(minX, sideMinY, maxZ, minX, sideMinY);
	    	    		vertexAutoMap(maxX, sideMinY, maxZ, maxX, sideMinY);
	    				vertexAutoMap(maxX, minY, maxZ, maxX, minY);
	    			}
	    			
	    			if(maxY > sideMaxY)
	    			{
					    tessellator.setNormal(0.0F, 0.0F, 1.0F);
	    				vertexAutoMap(maxX, sideMaxY, maxZ, maxX, sideMaxY);
	    	    		vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
	    	    		vertexAutoMap(minX, maxY, maxZ, minX, maxY);
	    	    		vertexAutoMap(minX, sideMaxY, maxZ, minX, sideMaxY);
					    tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    	    		vertexAutoMap(minX, sideMaxY, maxZ, minX, sideMaxY);
	    	    		vertexAutoMap(minX, maxY, maxZ, minX, maxY);
	    	    		vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
	    				vertexAutoMap(maxX, sideMaxY, maxZ, maxX, sideMaxY);
	    			}
				}
			}
			else
			{
			    tessellator.setNormal(0.0F, 0.0F,-1.0F);
	    		vertexAutoMap(maxX, minY, maxZ, maxX, minY);
	    		vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
	    		vertexAutoMap(minX, maxY, maxZ, minX, maxY);
	    		vertexAutoMap(minX, minY, maxZ, minX, minY);
			    tessellator.setNormal(0.0F, 0.0F, -1.0F);
	    		vertexAutoMap(minX, minY, maxZ, minX, minY);
	    		vertexAutoMap(minX, maxY, maxZ, minX, maxY);
	    		vertexAutoMap(maxX, maxY, maxZ, maxX, maxY);
	    		vertexAutoMap(maxX, minY, maxZ, maxX, minY);
			}
		}
		
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 5))
		{
			sideBlock = blockAccess.getBlock(i - 1, j, k);
			if(sideBlock == block)
			{
	    		sideBlockMetadata = blockAccess.getBlockMetadata(i - 1, j, k);
				sideMinY = ((BlockGas)sideBlock).type.getMinY(blockAccess, i - 1, j, k, sideBlockMetadata);
				sideMaxY = ((BlockGas)sideBlock).type.getMaxY(blockAccess, i - 1, j, k, sideBlockMetadata);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
				    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
					vertexAutoMap(minX, minY, minZ, minZ, maxY);
		    		vertexAutoMap(minX, maxY, minZ, minZ, minY);
		    		vertexAutoMap(minX, maxY, maxZ, maxZ, minY);
		    		vertexAutoMap(minX, minY, maxZ, maxZ, maxY);
				    tessellator.setNormal(1.0F, 0.0F, 0.0F);
		    		vertexAutoMap(minX, minY, maxZ, maxZ, maxY);
		    		vertexAutoMap(minX, maxY, maxZ, maxZ, minY);
		    		vertexAutoMap(minX, maxY, minZ, minZ, minY);
					vertexAutoMap(minX, minY, minZ, minZ, maxY);
				}
				else
				{
					if(minY < sideMinY)
	    			{
					    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    				vertexAutoMap(minX, minY, minZ, minZ, sideMinY);
	    	    		vertexAutoMap(minX, sideMinY, minZ, minZ, minY);
	    	    		vertexAutoMap(minX, sideMinY, maxZ, maxZ, minY);
	    	    		vertexAutoMap(minX, minY, maxZ, maxZ, sideMinY);
					    tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	    		vertexAutoMap(minX, minY, maxZ, maxZ, sideMinY);
	    	    		vertexAutoMap(minX, sideMinY, maxZ, maxZ, minY);
	    	    		vertexAutoMap(minX, sideMinY, minZ, minZ, minY);
	    				vertexAutoMap(minX, minY, minZ, minZ, sideMinY);
	    			}
	    			
	    			if(maxY > sideMaxY)
	    			{
					    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    				vertexAutoMap(minX, sideMaxY, minZ, minZ, maxY);
	    	    		vertexAutoMap(minX, maxY, minZ, minZ, sideMaxY);
	    	    		vertexAutoMap(minX, maxY, maxZ, maxZ, sideMaxY);
	    	    		vertexAutoMap(minX, sideMaxY, maxZ, maxZ, maxY);
					    tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    	    		vertexAutoMap(minX, sideMaxY, maxZ, maxZ, maxY);
	    	    		vertexAutoMap(minX, maxY, maxZ, maxZ, sideMaxY);
	    	    		vertexAutoMap(minX, maxY, minZ, minZ, sideMaxY);
	    				vertexAutoMap(minX, sideMaxY, minZ, minZ, maxY);
	    			}
				}
			}
			else
			{
			    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    		vertexAutoMap(minX, minY, maxZ, maxZ, maxY);
	    		vertexAutoMap(minX, maxY, maxZ, maxZ, minY);
	    		vertexAutoMap(minX, maxY, minZ, minZ, minY);
	    		vertexAutoMap(minX, minY, minZ, minZ, maxY);
			    tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    		vertexAutoMap(minX, minY, minZ, minZ, maxY);
	    		vertexAutoMap(minX, maxY, minZ, minZ, minY);
	    		vertexAutoMap(minX, maxY, maxZ, maxZ, minY);
	    		vertexAutoMap(minX, minY, maxZ, maxZ, maxY);
			}
		}
		
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 4))
		{
			sideBlock = blockAccess.getBlock(i + 1, j, k);
			if(sideBlock == block)
			{
	    		sideBlockMetadata = blockAccess.getBlockMetadata(i + 1, j, k);
				sideMinY = ((BlockGas)sideBlock).type.getMinY(blockAccess, i + 1, j, k, sideBlockMetadata);
				sideMaxY = ((BlockGas)sideBlock).type.getMaxY(blockAccess, i + 1, j, k, sideBlockMetadata);
				
				if((minY <= sideMinY & minY <= sideMaxY & maxY <= sideMinY & maxY <= sideMinY) | (minY >= sideMinY & minY >= sideMaxY & maxY >= sideMinY & maxY >= sideMinY))
				{
				    tessellator.setNormal(1.0F, 0.0F, 0.0F);
					vertexAutoMap(maxX, minY, maxZ, minZ, maxY);
		    		vertexAutoMap(maxX, maxY, maxZ, minZ, minY);
		    		vertexAutoMap(maxX, maxY, minZ, maxZ, minY);
		    		vertexAutoMap(maxX, minY, minZ, maxZ, maxY);
				    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		    		vertexAutoMap(maxX, minY, minZ, maxZ, maxY);
		    		vertexAutoMap(maxX, maxY, minZ, maxZ, minY);
		    		vertexAutoMap(maxX, maxY, maxZ, minZ, minY);
					vertexAutoMap(maxX, minY, maxZ, minZ, maxY);
				}
				else
				{
					if(minY < sideMinY)
					{
					    tessellator.setNormal(1.0F, 0.0F, 0.0F);
						vertexAutoMap(maxX, minY, maxZ, minZ, sideMinY);
			    		vertexAutoMap(maxX, sideMinY, maxZ, minZ, minY);
			    		vertexAutoMap(maxX, sideMinY, minZ, maxZ, minY);
			    		vertexAutoMap(maxX, minY, minZ, maxZ, sideMinY);
					    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			    		vertexAutoMap(maxX, minY, minZ, maxZ, sideMinY);
			    		vertexAutoMap(maxX, sideMinY, minZ, maxZ, minY);
			    		vertexAutoMap(maxX, sideMinY, maxZ, minZ, minY);
						vertexAutoMap(maxX, minY, maxZ, minZ, sideMinY);
					}
					
					if(maxY > sideMaxY)
					{
					    tessellator.setNormal(1.0F, 0.0F, 0.0F);
						vertexAutoMap(maxX, sideMaxY, maxZ, minZ, maxY);
			    		vertexAutoMap(maxX, maxY, maxZ, minZ, sideMaxY);
			    		vertexAutoMap(maxX, maxY, minZ, maxZ, sideMaxY);
			    		vertexAutoMap(maxX, sideMaxY, minZ, maxZ, maxY);
					    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			    		vertexAutoMap(maxX, sideMaxY, minZ, maxZ, maxY);
			    		vertexAutoMap(maxX, maxY, minZ, maxZ, sideMaxY);
			    		vertexAutoMap(maxX, maxY, maxZ, minZ, sideMaxY);
						vertexAutoMap(maxX, sideMaxY, maxZ, minZ, maxY);
					}
				}
			}
			else
			{
			    tessellator.setNormal(1.0F, 0.0F, 0.0F);
	    		vertexAutoMap(maxX, minY, minZ, maxZ, maxY);
	    		vertexAutoMap(maxX, maxY, minZ, maxZ, minY);
	    		vertexAutoMap(maxX, maxY, maxZ, minZ, minY);
	    		vertexAutoMap(maxX, minY, maxZ, minZ, maxY);
			    tessellator.setNormal(-1.0F, 0.0F, 0.0F);
	    		vertexAutoMap(maxX, minY, maxZ, minZ, maxY);
	    		vertexAutoMap(maxX, maxY, maxZ, minZ, minY);
	    		vertexAutoMap(maxX, maxY, minZ, maxZ, minY);
	    		vertexAutoMap(maxX, minY, minZ, maxZ, maxY);
			}
		}
	
		//tessellator.setColorOpaque_F(red * 0.8F, green * 0.8F, blue * 0.8F);
		tessellator.setColorRGBA_F(red * 0.8F, green * 0.8F, blue * 0.8F, alpha);
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 1))
		{
		    tessellator.setNormal(0.0F, -1.0F, 0.0F);
			vertexAutoMap(maxX, minY, minZ, maxX, maxZ);
			vertexAutoMap(maxX, minY, maxZ, maxX, minZ);
			vertexAutoMap(minX, minY, maxZ, minX, minZ);
			vertexAutoMap(minX, minY, minZ, minX, maxZ);
		    tessellator.setNormal(0.0F, 1.0F, 0.0F);
			vertexAutoMap(minX, minY, minZ, minX, maxZ);
			vertexAutoMap(minX, minY, maxZ, minX, minZ);
			vertexAutoMap(maxX, minY, maxZ, maxX, minZ);
			vertexAutoMap(maxX, minY, minZ, maxX, maxZ);
		}
		
		//tessellator.setColorOpaque_F(red, green, blue);
		tessellator.setColorRGBA_F(red, green, blue, alpha);
		if(block.shouldSideBeRendered(blockAccess, i, j, k, 0))
		{
		    tessellator.setNormal(0.0F, 1.0F, 0.0F);
			vertexAutoMap(maxX, maxY, maxZ, maxX, maxZ);
			vertexAutoMap(maxX, maxY, minZ, maxX, minZ);
			vertexAutoMap(minX, maxY, minZ, minX, minZ);
			vertexAutoMap(minX, maxY, maxZ, minX, maxZ);
		    tessellator.setNormal(0.0F, -1.0F, 0.0F);
			vertexAutoMap(minX, maxY, maxZ, minX, maxZ);
			vertexAutoMap(minX, maxY, minZ, minX, minZ);
			vertexAutoMap(maxX, maxY, minZ, maxX, minZ);
			vertexAutoMap(maxX, maxY, maxZ, maxX, maxZ);
		}
	
		tessellator.addTranslation((float)-i, (float)-j, (float)-k);
	
		return true;
	}
	
	private void vertexAutoMap(double x, double y, double z, double u, double v)
	{
		tessellator.addVertexWithUV(x, y, z, icon.getInterpolatedU(u * 16.0D), icon.getInterpolatedV(v * 16.0D));
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