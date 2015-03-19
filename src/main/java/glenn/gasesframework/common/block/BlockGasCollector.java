package glenn.gasesframework.common.block;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.tileentity.TileEntityGasCollector;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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