package glenn.gasesframework.common.block;

import glenn.gasesframework.common.tileentity.TileEntityGasCollector;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockGasCollector extends BlockGasPump
{
	public BlockGasCollector()
	{
		super(false);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
	   return new TileEntityGasCollector();
	}
}