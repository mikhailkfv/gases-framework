package glenn.gasesframework.client.render;

import cpw.mods.fml.client.registry.RenderingRegistry;
import glenn.moddingutils.blockrotation.AbstractRenderRotatedBlock;
import glenn.moddingutils.blockrotation.BlockRotation;
import net.minecraft.world.IBlockAccess;

public class RenderBlockGasDynamo extends AbstractRenderRotatedBlock
{
	public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	@Override
	public int getRenderId()
	{
		return RENDER_ID;
	}

	@Override
	public BlockRotation getRotation(IBlockAccess world, int x, int y, int z)
	{
		return BlockRotation.getRotation(world.getBlockMetadata(x, y, z));
	}
}
