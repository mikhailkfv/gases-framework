package glenn.gasesframework.common.block;

import glenn.gasesframework.common.tileentity.TileEntityIronGasDynamo;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIronGasDynamo extends BlockGasDynamo
{
	public BlockIronGasDynamo()
	{
		super(Material.iron);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityIronGasDynamo();
	}

}
