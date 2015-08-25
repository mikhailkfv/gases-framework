package glenn.gasesframework.common.block;

import glenn.gasesframework.common.tileentity.TileEntityGasCollector;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockGasCollector extends BlockDirectionalGasPropellor
{
	public BlockGasCollector(Material material, int maxPressure)
	{
		super(material, false, maxPressure);
	}
}