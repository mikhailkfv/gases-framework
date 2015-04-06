package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.network.message.MessageGasEffects;
import glenn.gasesframework.network.message.MessageSetTransposerMode;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

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
	
	public void registerMessages()
	{
		GasesFramework.networkWrapper.registerMessage(MessageGasEffects.Handler.class, MessageGasEffects.class, 0, Side.CLIENT);
		GasesFramework.networkWrapper.registerMessage(MessageSetTransposerMode.Handler.class, MessageSetTransposerMode.class, 1, Side.SERVER);
	}
}