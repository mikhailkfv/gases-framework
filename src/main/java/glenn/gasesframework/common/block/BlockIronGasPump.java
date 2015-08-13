package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.tileentity.TileEntityIronGasPump;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIronGasPump extends BlockGasPump
{
	public BlockIronGasPump()
	{
		super(Material.iron, GasesFramework.configurations.piping.ironMaterial.maxPressure);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityIronGasPump();
	}
}
