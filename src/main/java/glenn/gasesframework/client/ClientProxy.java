package glenn.gasesframework.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import glenn.gasesframework.CommonProxy;
import glenn.gasesframework.client.render.RenderBlockGas;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.client.render.RenderBlockGasPump;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.client.render.RenderBlockLantern;
import glenn.gasesframework.client.render.TileEntityTankRenderer;
import glenn.gasesframework.tileentity.TileEntityTank;

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
	
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new GasesFrameworkClientEvents());
	}
}
