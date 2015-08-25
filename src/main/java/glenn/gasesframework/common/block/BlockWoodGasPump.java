package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasPump;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWoodGasPump extends BlockGasPump
{
	public BlockWoodGasPump()
	{
		super(Material.wood, GasesFramework.configurations.piping.woodMaterial.maxPressure);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityWoodGasPump();
	}
}
