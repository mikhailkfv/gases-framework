package glenn.gasesframework.common.block;

import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasDrain;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockInfiniteGasDrain extends Block implements ITileEntityProvider, IGasReceptor
{
	public BlockInfiniteGasDrain()
	{
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_)
	{
		return new TileEntityInfiniteGasDrain();
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		TileEntityInfiniteGasDrain tileEntity = (TileEntityInfiniteGasDrain) world.getTileEntity(x, y, z);
		return tileEntity.isActive();
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		return canReceiveGas(world, x, y, z, side, gasType);
	}
}