package glenn.gasesframework.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.client.render.*;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasTank;
import glenn.moddingutils.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    public RenderVillagerGag renderVillagerGag;
	public RenderPlayerGag renderPlayerGag;

	@Override
	public void registerRenderers()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGasTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGasDynamo.class, new TileEntityGasDynamoRenderer());
		
		RenderingRegistry.registerBlockHandler(RenderBlockGas.RENDER_ID, new RenderBlockGas());
		RenderingRegistry.registerBlockHandler(RenderBlockLantern.RENDER_ID, new RenderBlockLantern());
		RenderingRegistry.registerBlockHandler(RenderBlockGasPipe.RENDER_ID, new RenderBlockGasPipe());
		RenderingRegistry.registerBlockHandler(RenderBlockGasTypeFilter.RENDER_ID, new RenderBlockGasTypeFilter());
		RenderingRegistry.registerBlockHandler(RenderBlockGasTank.RENDER_ID, new RenderBlockGasTank());
		RenderingRegistry.registerBlockHandler(RenderBlockInfiniteGasPump.RENDER_ID, new RenderBlockInfiniteGasPump());
		RenderingRegistry.registerBlockHandler(RenderRotatedBlock.RENDER_ID, new RenderRotatedBlock());
		RenderingRegistry.registerEntityRenderingHandler(Entity.class, renderVillagerGag = new RenderVillagerGag());
		RenderingRegistry.registerEntityRenderingHandler(Entity.class, renderPlayerGag = new RenderPlayerGag());
	}
	
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
		
		if(GasesFramework.configurations.updateChecker.enable)
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
