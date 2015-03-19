package glenn.gasesframework.waila;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.tileentity.TileEntityTank;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class TankProvider implements IWailaDataProvider
{
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntityTank tank = (TileEntityTank)accessor.getTileEntity();
		if (tank.containedType == null)
		{
			currenttip.add(StatCollector.translateToLocal("tile.gf_gasTank.waila.body.empty"));
		}
		else
		{
			String gasName = StatCollector.translateToLocal(tank.containedType.getUnlocalizedName() + ".name");
			String amount = 100 * tank.amount / tank.getGasCap() + "%";
			currenttip.add(StatCollector.translateToLocalFormatted("tile.gf_gasTank.waila.body.filled", gasName, amount));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
	{
		return tag;
	}
}