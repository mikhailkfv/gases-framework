package glenn.gasesframework.common.block;

import glenn.gasesframework.common.tileentity.TileEntityWoodGasDynamo;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWoodGasDynamo extends BlockGasDynamo
{
	public BlockWoodGasDynamo()
	{
		super(Material.wood);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityWoodGasDynamo();
	}

}
