package glenn.gasesframework;

import glenn.gasesframework.api.ExtendedGasEffectsBase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class GasesFrameworkServerEvents
{
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if(event.entity instanceof EntityLivingBase)
		{
			EntityLivingBase entityLivingBase = (EntityLivingBase)event.entity;
			if(ExtendedGasEffects.get(entityLivingBase) == null)
			{
				ExtendedGasEffects.register(entityLivingBase);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		ExtendedGasEffects extendedGasEffects = (ExtendedGasEffects)ExtendedGasEffectsBase.get(event.entityLiving);
		extendedGasEffects.tick();
	}
}