package glenn.gasesframework.server;

import glenn.gasesframework.client.render.RenderBlockGas;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.client.render.RenderBlockGasPump;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.client.render.RenderBlockLantern;
import glenn.gasesframework.client.render.TileEntityTankRenderer;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.common.tileentity.TileEntityGasTank;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ServerProxy extends CommonProxy
{
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
	}
}