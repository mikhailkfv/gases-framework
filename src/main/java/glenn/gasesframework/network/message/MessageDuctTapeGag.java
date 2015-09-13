package glenn.gasesframework.network.message;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.DuctTapeGag;
import glenn.moddingutils.network.AbstractMessage;
import glenn.moddingutils.network.AbstractMessageHandler;
import glenn.moddingutils.network.AbstractSerialMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class MessageDuctTapeGag extends AbstractSerialMessage
{
	public static class Handler extends AbstractMessageHandler<MessageDuctTapeGag, AbstractMessage>
	{
		@Override
		public AbstractMessage onMessage(MessageDuctTapeGag message, MessageContext ctx)
		{
			World world = GasesFramework.proxy.getPlayerEntity(ctx).worldObj;

			Entity entity = world.getEntityByID(message.entityID);

			if (entity instanceof EntityLivingBase)
			{
				EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
				if(message.gagged)
				{
					DuctTapeGag.gag(entityLivingBase);
				}
				else
				{
					DuctTapeGag.ungag(entityLivingBase);
				}
			}

			return null;
		}
	}

	public int entityID;
	public boolean gagged;

	public MessageDuctTapeGag(EntityLivingBase entity, boolean gagged)
	{
		this.entityID = entity.getEntityId();
		this.gagged = gagged;
	}

	public MessageDuctTapeGag()
	{}
}
