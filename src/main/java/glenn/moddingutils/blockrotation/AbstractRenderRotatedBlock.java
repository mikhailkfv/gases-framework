package glenn.moddingutils.blockrotation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class AbstractRenderRotatedBlock implements ISimpleBlockRenderingHandler
{
	public abstract void beginRenderInventoryBlock();
	
	public abstract void endRenderInventoryBlock();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		IRotatedBlock rotatedBlock = (IRotatedBlock)block;
		BlockRotation rotation = rotatedBlock.getBlockRotationAsItem(metadata);
		
		GL11.glPushMatrix();
		GL11.glRotatef(rotation.yaw.getRotationDegrees(), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rotation.pitch.getRotationDegrees(), 1.0F, 0.0F, 0.0F);

		beginRenderInventoryBlock();
		renderer.renderBlockAsItem(block, BlockRotation.PASSIVE_ROTATION.ordinal(), 1.0f);
		endRenderInventoryBlock();
		
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		IRotatedBlock rotatedBlock = (IRotatedBlock)block;
		BlockRotation rotation = rotatedBlock.getBlockRotation(world, x, y, z);
		
		applyRotation(renderer, rotation);
		boolean flag = renderer.renderStandardBlock(block, x, y, z);
		resetRotation(renderer);
		
		return flag;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}
	
	private void applyRotation(RenderBlocks renderer, BlockRotation rotation)
	{
		renderer.uvRotateBottom = rotation.getUvRotation(ForgeDirection.DOWN);
		renderer.uvRotateTop = rotation.getUvRotation(ForgeDirection.UP);
		renderer.uvRotateNorth = rotation.getUvRotation(ForgeDirection.NORTH);
		renderer.uvRotateSouth = rotation.getUvRotation(ForgeDirection.SOUTH);
		renderer.uvRotateWest = rotation.getUvRotation(ForgeDirection.WEST);
		renderer.uvRotateEast = rotation.getUvRotation(ForgeDirection.EAST);
	}
	
	private void resetRotation(RenderBlocks renderer)
	{
		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateEast = 0;
	}
}
