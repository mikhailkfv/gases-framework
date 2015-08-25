package glenn.moddingutils.blockrotation;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class AbstractRenderRotatedBlock implements ISimpleBlockRenderingHandler
{
	public static boolean renderingInventoryBlock = false;
	
	public abstract BlockRotation getRotation(IBlockAccess world, int x, int y, int z);

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		renderingInventoryBlock = true;
		renderer.renderBlockAsItem(block, metadata, 1.0f);
		renderingInventoryBlock = false;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		BlockRotation rotation = getRotation(world, x, y, z);
		
		renderer.uvRotateBottom = rotation.getUvRotation(ForgeDirection.DOWN);
		renderer.uvRotateTop = rotation.getUvRotation(ForgeDirection.UP);
		renderer.uvRotateNorth = rotation.getUvRotation(ForgeDirection.NORTH);
		renderer.uvRotateSouth = rotation.getUvRotation(ForgeDirection.SOUTH);
		renderer.uvRotateWest = rotation.getUvRotation(ForgeDirection.WEST);
		renderer.uvRotateEast = rotation.getUvRotation(ForgeDirection.EAST);

		boolean flag = renderer.renderStandardBlock(block, x, y, z);

		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateEast = 0;
		
		return flag;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}
}
