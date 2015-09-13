package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockWoodGasFurnace extends BlockGasFurnace
{

	public BlockWoodGasFurnace(boolean isActive)
	{
		super(Material.wood, isActive);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityWoodGasFurnace();
	}

	@Override
	protected Block getIdleBlock()
	{
		return GasesFramework.blocks.woodGasFurnaceIdle;
	}

	@Override
	protected Block getActiveBlock()
	{
		return GasesFramework.blocks.woodGasFurnaceActive;
	}

}
