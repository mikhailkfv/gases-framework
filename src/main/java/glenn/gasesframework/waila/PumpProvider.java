package glenn.gasesframework.waila;

import glenn.gasesframework.common.tileentity.TileEntityGasPump;

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

public class PumpProvider implements IWailaDataProvider
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
		TileEntityGasPump pump = (TileEntityGasPump)accessor.getTileEntity();
		if (pump.filterType == null)
		{
			currenttip.add(StatCollector.translateToLocal("tile.gf_gasPump.waila.body.filter.none"));
		}
		else
		{
			String gasName = StatCollector.translateToLocal(pump.filterType.getUnlocalizedName() + ".name");
			currenttip.add(StatCollector.translateToLocalFormatted("tile.gf_gasPump.waila.body.filter." + (pump.excludes ? "excluding" : "including"), gasName));
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