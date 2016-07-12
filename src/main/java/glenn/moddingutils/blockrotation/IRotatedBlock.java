package glenn.moddingutils.blockrotation;

import net.minecraft.world.IBlockAccess;

public interface IRotatedBlock
{
	BlockRotation getBlockRotationAsItem(int metadata);

	BlockRotation getBlockRotation(IBlockAccess blockAccess, int x, int y, int z);

	void swapRotatedBlockRenderType();
}
