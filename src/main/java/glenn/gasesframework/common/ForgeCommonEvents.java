package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasFilter;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.item.IFilterProvider;
import glenn.gasesframework.api.item.ISampler;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			ForgeDirection side = ForgeDirection.getOrientation(event.face);
			Block block = event.world.getBlock(event.x, event.y, event.z);
			ItemStack itemstack = event.entityPlayer.getHeldItem();
			Item item = itemstack.getItem();

			if (item instanceof IFilterProvider)
			{
				IFilterProvider filterProvider = (IFilterProvider)itemstack.getItem();
				
				if (block instanceof IGasFilter)
				{
					IGasFilter gasFilterBlock = (IGasFilter)block;
					GasTypeFilter filter = filterProvider.getFilter(itemstack);
					
					gasFilterBlock.setFilter(event.world, event.x, event.y, event.z, side, filter);
				}
			}

			if(item instanceof ISampler)
			{
				ISampler sampler = (ISampler)item;
				
				if (block instanceof ISample)
				{
					ISample sample = (ISample)block;
					sampler.setGasType(itemstack, sample.sampleInteraction(event.world, event.x, event.y, event.z, sampler.getGasType(itemstack), side));
				}
			}
		}
	}
}