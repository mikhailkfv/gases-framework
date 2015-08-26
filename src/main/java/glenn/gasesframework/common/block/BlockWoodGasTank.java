package glenn.gasesframework.common.block;

import glenn.gasesframework.common.tileentity.TileEntityWoodGasTank;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWoodGasTank extends BlockGasTank
{
	public BlockWoodGasTank()
	{
		super(Material.wood);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityWoodGasTank();
	}
}
