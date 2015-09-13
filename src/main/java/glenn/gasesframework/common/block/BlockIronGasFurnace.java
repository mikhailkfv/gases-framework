package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.tileentity.TileEntityIronGasFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIronGasFurnace extends BlockGasFurnace
{

	public BlockIronGasFurnace(boolean isActive)
	{
		super(Material.iron, isActive);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityIronGasFurnace();
	}

	@Override
	protected Block getIdleBlock()
	{
		return GasesFramework.blocks.gasFurnaceIdle;
	}

	@Override
	protected Block getActiveBlock()
	{
		return GasesFramework.blocks.gasFurnaceActive;
	}

}
