package glenn.gasesframework.client;

import glenn.gasesframework.client.render.RenderBlockGas;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.client.render.RenderBlockGasPump;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.client.render.RenderBlockLantern;
import glenn.gasesframework.client.render.TileEntityTankRenderer;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.tileentity.TileEntityTank;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
		
		RenderingRegistry.registerBlockHandler(RenderBlockGas.RENDER_ID, new RenderBlockGas());
		RenderingRegistry.registerBlockHandler(RenderBlockLantern.RENDER_ID, new RenderBlockLantern());
		RenderingRegistry.registerBlockHandler(RenderBlockGasPipe.RENDER_ID, new RenderBlockGasPipe());
		RenderingRegistry.registerBlockHandler(RenderBlockGasPump.RENDER_ID, new RenderBlockGasPump());
		RenderingRegistry.registerBlockHandler(RenderBlockGasTank.RENDER_ID, new RenderBlockGasTank());
	}
	
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
	}
}
