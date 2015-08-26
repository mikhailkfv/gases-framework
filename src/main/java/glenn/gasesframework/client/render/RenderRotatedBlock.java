package glenn.gasesframework.client.render;

import cpw.mods.fml.client.registry.RenderingRegistry;
import glenn.moddingutils.blockrotation.AbstractRenderRotatedBlock;
import glenn.moddingutils.blockrotation.BlockRotation;
import net.minecraft.world.IBlockAccess;

public class RenderRotatedBlock extends AbstractRenderRotatedBlock
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	public static boolean isRenderingInventoryBlock = false;
	
	@Override
	public int getRenderId()
	{
		return RENDER_ID;
	}
	
	@Override
	public void beginRenderInventoryBlock()
	{
		isRenderingInventoryBlock = true;
	}

	@Override
	public void endRenderInventoryBlock()
	{
		isRenderingInventoryBlock = false;
	}
}
