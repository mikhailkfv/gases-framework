package glenn.gasesframework.network.message;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.block.IGasFilter;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.moddingutils.network.AbstractMessage;
import glenn.moddingutils.network.AbstractMessageHandler;
import glenn.moddingutils.network.AbstractSerialMessage;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSetBlockGasTypeFilter extends AbstractSerialMessage
{
	public static class Handler extends AbstractMessageHandler<MessageSetBlockGasTypeFilter, AbstractMessage>
	{
		@Override
		public AbstractMessage onMessage(MessageSetBlockGasTypeFilter message, MessageContext ctx)
		{
			World world = GasesFramework.proxy.getPlayerEntity(ctx).worldObj;
			
			Block block = world.getBlock(message.blockX, message.blockY, message.blockZ);
			if(block instanceof IGasFilter)
			{
				((IGasFilter)block).setFilter(world, message.blockX, message.blockY, message.blockZ, message.getSide(), message.getFilter());
			}
			
			return null;
		}
	}

	public int blockX, blockY, blockZ;
	public int side;
	public NBTTagCompound filter;
	
	public MessageSetBlockGasTypeFilter(int blockX, int blockY, int blockZ, ForgeDirection side, GasTypeFilter filter)
	{
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.side = side.ordinal();
		this.filter = new NBTTagCompound();
		filter.writeToNBT(this.filter);
	}
	
	public MessageSetBlockGasTypeFilter()
	{
		
	}
	
	public ForgeDirection getSide()
	{
		return ForgeDirection.getOrientation(side);
	}
	
	public GasTypeFilter getFilter()
	{
		return GasTypeFilter.readFromNBT(filter);
	}
}
