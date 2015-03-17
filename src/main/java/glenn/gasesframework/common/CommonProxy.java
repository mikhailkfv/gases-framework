package glenn.gasesframework.common;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public void registerRenderers()
	{
		
	}
	
	public void registerEventHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new ForgeCommonEvents());
		FMLCommonHandler.instance().bus().register(new FMLCommonEvents());
	}
}