package glenn.gasesframework;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy
{
	public void registerRenderers()
	{
		
	}
	
	public void registerEventHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new GasesFrameworkServerEvents());
	}
}