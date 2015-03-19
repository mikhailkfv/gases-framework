package glenn.gasesframework.waila;

import glenn.gasesframework.common.block.BlockGasPipe;
import glenn.gasesframework.common.block.BlockGasPump;
import glenn.gasesframework.common.block.BlockGasTank;
import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;

public class GasesFrameworkWaila
{
	private static final PipeProvider pipeProvider = new PipeProvider();
	private static final PumpProvider pumpProvider = new PumpProvider();
	private static final TankProvider tankProvider = new TankProvider();
	
	private static IWailaRegistrar wailaRegistrar;
	public static void callbackRegister(IWailaRegistrar registrar)
	{
		wailaRegistrar = registrar;
		wailaRegistrar.registerBodyProvider(pipeProvider, BlockGasPipe.class);
		wailaRegistrar.registerBodyProvider(pumpProvider, BlockGasPump.class);
		wailaRegistrar.registerBodyProvider(tankProvider, BlockGasTank.class);
	}
	
	public static void init()
	{
		FMLInterModComms.sendMessage("Waila", "register", "glenn.gasesframework.waila.GasesFrameworkWaila.callbackRegister");
	}
}