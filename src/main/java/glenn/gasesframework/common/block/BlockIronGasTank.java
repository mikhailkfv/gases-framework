package glenn.gasesframework.common.block;

import glenn.gasesframework.common.tileentity.TileEntityIronGasTank;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIronGasTank extends BlockGasTank
{
	public BlockIronGasTank()
	{
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityIronGasTank();
	}
}
