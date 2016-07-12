package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.network.message.MessageGasEffects;
import glenn.gasesframework.network.message.MessageSetTransposerMode;
import glenn.moddingutils.network.AbstractMessage;
import glenn.moddingutils.network.AbstractMessageHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public abstract class CommonProxy
{
	public void registerRenderers()
	{

	}

	public void registerEventHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new ForgeCommonEvents());
		FMLCommonHandler.instance().bus().register(new FMLCommonEvents());
	}

	public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, int discriminator)
	{
		GasesFramework.networkWrapper.registerMessage(messageHandler, requestMessageType, discriminator, Side.SERVER);
	}

	public EntityPlayer getPlayerEntity(MessageContext ctx)
	{
		return ctx.getServerHandler().playerEntity;
	}
}