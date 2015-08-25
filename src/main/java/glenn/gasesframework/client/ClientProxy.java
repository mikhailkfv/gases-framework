package glenn.gasesframework.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.client.render.RenderBlockGas;
import glenn.gasesframework.client.render.RenderBlockGasDynamo;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.client.render.RenderBlockGasPump;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.client.render.RenderBlockInfiniteGasPump;
import glenn.gasesframework.client.render.RenderBlockLantern;
import glenn.gasesframework.client.render.TileEntityGasDynamoRenderer;
import glenn.gasesframework.client.render.TileEntityTankRenderer;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasTank;
import glenn.moddingutils.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGasTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGasDynamo.class, new TileEntityGasDynamoRenderer());
		
		RenderingRegistry.registerBlockHandler(RenderBlockGas.RENDER_ID, new RenderBlockGas());
		RenderingRegistry.registerBlockHandler(RenderBlockLantern.RENDER_ID, new RenderBlockLantern());
		RenderingRegistry.registerBlockHandler(RenderBlockGasPipe.RENDER_ID, new RenderBlockGasPipe());
		RenderingRegistry.registerBlockHandler(RenderBlockGasPump.RENDER_ID, new RenderBlockGasPump());
		RenderingRegistry.registerBlockHandler(RenderBlockGasTank.RENDER_ID, new RenderBlockGasTank());
		RenderingRegistry.registerBlockHandler(RenderBlockInfiniteGasPump.RENDER_ID, new RenderBlockInfiniteGasPump());
		RenderingRegistry.registerBlockHandler(RenderBlockGasDynamo.RENDER_ID, new RenderBlockGasDynamo());
	}
	
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
		
		if(GasesFramework.configurations.other_enableUpdateCheck)
		{
			MinecraftForge.EVENT_BUS.register(new UpdateChecker("https://www.jamieswhiteshirt.com/trackable/gasesFramework.php", "Gases Framework", GasesFramework.MODID, GasesFramework.VERSION, GasesFramework.TARGETVERSION));
		}
	}
	
	@Override
	public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, int discriminator)
	{
		super.registerMessage(messageHandler, requestMessageType, discriminator);
		GasesFramework.networkWrapper.registerMessage(messageHandler, requestMessageType, discriminator, Side.CLIENT);
	}
	
	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx)
	{
		return ctx.side == Side.CLIENT ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx);
	}
}
