package glenn.gasesframework.waila;

import glenn.gasesframework.common.tileentity.TileEntityGasTank;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
		TileEntityGasTank tileEntity = (TileEntityGasTank)accessor.getTileEntity();
		if (tileEntity.isEmpty())
		{
			currenttip.add(I18n.format("tile.gf_gasTank.waila.body.empty"));
		}
		else
		{
			String gasName = I18n.format(tileEntity.getGasTypeStored().getUnlocalizedName() + ".name");
			String amount = 100.0D * tileEntity.getRelativeGasStored() + "%";
			currenttip.add(I18n.format("tile.gf_gasTank.waila.body.filled", gasName, amount));
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