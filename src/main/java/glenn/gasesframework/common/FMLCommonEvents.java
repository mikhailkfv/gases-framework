package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class FMLCommonEvents
{
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event)
	{
		GasesFramework.worldGenerator.onServerTick(event);
	}
}