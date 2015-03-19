package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ForgeCommonEvents
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
	
	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event)
	{
		GasesFramework.worldGenerator.onChunkLoad(event);
	}
	
	@SubscribeEvent
	public void onChunkSave(ChunkDataEvent.Save event)
	{
		GasesFramework.worldGenerator.onChunkSave(event);
	}
	
	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		GasesFramework.worldGenerator.onChunkUnload(event);
	}
}