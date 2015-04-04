package glenn.moddingutils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractMessageHandler<REQ extends AbstractMessage, REPLY extends AbstractMessage> implements IMessageHandler<REQ, REPLY>
{
	@Override
	public abstract REPLY onMessage(REQ message, MessageContext ctx);
}