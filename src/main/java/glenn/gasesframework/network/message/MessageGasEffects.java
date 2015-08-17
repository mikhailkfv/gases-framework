package glenn.gasesframework.network.message;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.moddingutils.network.AbstractMessage;
import glenn.moddingutils.network.AbstractMessageHandler;
import glenn.moddingutils.network.AbstractSerialMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageGasEffects extends AbstractSerialMessage
{
	public static class Handler extends AbstractMessageHandler<MessageGasEffects, AbstractMessage>
	{
		@Override
		public AbstractMessage onMessage(MessageGasEffects message, MessageContext ctx)
		{
			World world = GasesFramework.proxy.getPlayerEntity(ctx).worldObj;
			
			Entity entity = world.getEntityByID(message.entityID);
			
			if(entity != null && entity instanceof EntityLivingBase)
			{
				ExtendedGasEffectsBase extendedGasEffects = ExtendedGasEffectsBase.get((EntityLivingBase)entity);
				
				extendedGasEffects.set(ExtendedGasEffectsBase.EffectType.BLINDNESS, message.blindness);
				extendedGasEffects.set(ExtendedGasEffectsBase.EffectType.SUFFOCATION, message.suffocation);
				extendedGasEffects.set(ExtendedGasEffectsBase.EffectType.SLOWNESS, message.slowness);
			}
			
			return null;
		}
	}
	
	public int entityID;
	public short blindness, suffocation, slowness;
	
	public MessageGasEffects(EntityLivingBase entity, short blindness, short suffocation, short slowness)
	{
		this.entityID = entity.getEntityId();
		this.blindness = blindness;
		this.suffocation = suffocation;
		this.slowness = slowness;
	}
	
	public MessageGasEffects()
	{}
}