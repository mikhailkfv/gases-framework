package glenn.gasesframework.network.message;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.moddingutils.AbstractMessage;
import glenn.moddingutils.AbstractMessageHandler;
import glenn.moddingutils.AbstractSerialMessage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageSetTransposerMode extends AbstractSerialMessage
{
	public static class Handler extends AbstractMessageHandler<MessageSetTransposerMode, AbstractMessage>
	{
		@Override
		public AbstractMessage onMessage(MessageSetTransposerMode message, MessageContext ctx)
		{
			World world = GasesFramework.proxy.getPlayerEntity(ctx).worldObj;
			
			TileEntity tileEntity = world.getTileEntity(message.tileEntityX, message.tileEntityY, message.tileEntityZ);
			if(tileEntity != null && tileEntity instanceof TileEntityGasTransposer)
			{
				((TileEntityGasTransposer)tileEntity).setMode(message.mode);
			}
			
			return null;
		}
	}
	
	public int tileEntityX, tileEntityY, tileEntityZ;
	public int mode;
	
	public MessageSetTransposerMode(TileEntityGasTransposer tileEntity)
	{
		this.tileEntityX = tileEntity.xCoord;
		this.tileEntityY = tileEntity.yCoord;
		this.tileEntityZ = tileEntity.zCoord;
		this.mode = tileEntity.mode.ordinal();
	}
	
	public MessageSetTransposerMode()
	{}
}