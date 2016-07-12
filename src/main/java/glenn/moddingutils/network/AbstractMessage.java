package glenn.moddingutils.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractMessage implements IMessage
{
	@Override
	public abstract void toBytes(ByteBuf buffer);

	@Override
	public abstract void fromBytes(ByteBuf buffer);
}