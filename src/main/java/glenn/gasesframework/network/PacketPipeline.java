package glenn.gasesframework.network;

import glenn.gasesframework.GasesFramework;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;

@ChannelHandler.Sharable
public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, AbstractPacket>
{
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	private IdentityHashMap<Class<? extends AbstractPacket>, Integer> packetsMap;
	private final LinkedList<Class<? extends AbstractPacket>> packetsList = new LinkedList<Class<? extends AbstractPacket>>();
	private boolean isPostInitialized = false;
	
	public void registerPacket(Class<? extends AbstractPacket> clazz)
	{
		if(isPostInitialized)
		{
			throw new RuntimeException("Attempted to register packet " + clazz.getName() + " when the PacketPipeline was already post initialized.");
		}
		
		if(packetsList.contains(clazz))
		{
			throw new RuntimeException("Attempted to register packet " + clazz.getName() + " when it was already registered.");
		}
		
		packetsList.add(clazz);
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, List<Object> out) throws Exception
	{
		ByteBuf buffer = Unpooled.buffer();
		Class<? extends AbstractPacket> clazz = msg.getClass();
		
		Integer discriminator = packetsMap.get(clazz);
		
		if(discriminator == null)
		{
			throw new RuntimeException("Packet named " + clazz.getName() + " is not registered.");
		}
		
		buffer.writeByte(discriminator);
		msg.encodeInto(ctx, buffer);
		FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(proxyPacket);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception
	{
		ByteBuf buffer = msg.payload();
		int discriminator = buffer.readByte();
		Class<? extends AbstractPacket> clazz = packetsList.get(discriminator);
		
		if(clazz == null)
		{
			throw new RuntimeException("No packet was registered for discriminator " + discriminator + ".");
		}
		
		AbstractPacket packet = clazz.newInstance();
		packet.decodeinto(ctx, buffer.slice());
		
		EntityPlayer player;
		switch(FMLCommonHandler.instance().getEffectiveSide())
		{
		case CLIENT:
			player = Minecraft.getMinecraft().thePlayer;
			packet.handleClientSide(player);
			break;
		case SERVER:
			INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
			player = ((NetHandlerPlayServer)netHandler).playerEntity;
			packet.handleServerSide(player);
			break;
		}
		
		out.add(packet);
	}
	
	public void initialize()
	{
		this.channels = NetworkRegistry.INSTANCE.newChannel(GasesFramework.MODID, this);
	}
	
	public void postInitialize()
	{
		isPostInitialized = true;
		
		Collections.sort(packetsList, new Comparator<Class<? extends AbstractPacket>>(){
			@Override
			public int compare(Class<? extends AbstractPacket> a, Class<? extends AbstractPacket> b)
			{
				int res = String.CASE_INSENSITIVE_ORDER.compare(a.getCanonicalName(), b.getCanonicalName());
				if(res == 0)
				{
					res = a.getCanonicalName().compareTo(b.getCanonicalName());
				}
				
				return res;
			}
		});
		
		packetsMap = new IdentityHashMap<Class<? extends AbstractPacket>, Integer>();
		for(int i = 0; i < packetsList.size(); i++)
		{
			packetsMap.put(packetsList.get(i), Integer.valueOf(i));
		}
	}
	
	public void sendToAll(AbstractPacket message)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeAndFlush(message);
	}
	
	public void sendToPlayer(AbstractPacket message, EntityPlayer player)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeAndFlush(message);
	}
	
	public void sendToAllAround(AbstractPacket message, int dimensionID, double x, double y, double z, double range)
	{
		sendToAllAround(message, new NetworkRegistry.TargetPoint(dimensionID, x, y, z, range));
	}
	
	public void sendToAllAround(AbstractPacket message, NetworkRegistry.TargetPoint point)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		channels.get(Side.SERVER).writeAndFlush(message);
	}
	
	public void sendToAllInDimension(AbstractPacket message, int dimensionID)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionID);
		channels.get(Side.SERVER).writeAndFlush(message);
	}
	
	public void sendToServer(AbstractPacket message)
	{
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeAndFlush(message);
	}
}