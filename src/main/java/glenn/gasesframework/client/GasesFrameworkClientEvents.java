package glenn.gasesframework.client;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import glenn.gasesframework.ExtendedGasEffects;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.block.BlockGasPipe;
import glenn.gasesframework.tileentity.TileEntityPump;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.FOVUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GasesFrameworkClientEvents
{
	@SubscribeEvent
	public void onFogDensity(FogDensity event)
	{
		ExtendedGasEffectsBase gasEffects = ExtendedGasEffectsBase.get(event.entity);
		
		float f = gasEffects.get(ExtendedGasEffectsBase.BLINDNESS_WATCHER) / 250.0f;
		
		if(f > 0.0f)
		{
			event.density = f * f + 0.01f;
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onFogColor(FogColors event)
	{
		ExtendedGasEffectsBase gasEffects = ExtendedGasEffectsBase.get(event.entity);
		
		float f = 1.0f / (gasEffects.get(ExtendedGasEffectsBase.BLINDNESS_WATCHER) / 30.0f + 1.0f);
		event.red *= f;
		event.green *= f;
		event.blue *= f;
	}
	
	@SubscribeEvent
	public void onFOVUpdate(FOVUpdateEvent event)
	{
		float f = ExtendedGasEffectsBase.get(event.entity).get(ExtendedGasEffectsBase.BLINDNESS_WATCHER) / 500.0f;
		
		event.newfov = event.fov * (2.0f - f * f) / 2.0f;
	}
	
	private final ResourceLocation flow_indicator = new ResourceLocation("gasesframework", "textures/misc/flow_indicator.png");
	private final ResourceLocation flow_indicator_reverse = new ResourceLocation("gasesframework", "textures/misc/flow_indicator_reverse.png");
	private final ResourceLocation flow_indicator_dual = new ResourceLocation("gasesframework", "textures/misc/flow_indicator_dual.png");
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		int x = event.target.blockX;
		int y = event.target.blockY;
		int z = event.target.blockZ;
		double xd = x - (event.player.lastTickPosX + (event.player.posX - event.player.lastTickPosX) * (double)event.partialTicks);
        double yd = y - (event.player.lastTickPosY + (event.player.posY - event.player.lastTickPosY) * (double)event.partialTicks);
        double zd = z - (event.player.lastTickPosZ + (event.player.posZ - event.player.lastTickPosZ) * (double)event.partialTicks);
		World world = event.player.worldObj;
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGasPipe)
		{
			BlockGasPipe gasPipe = (BlockGasPipe)block;
			
			ItemStack currentPlayerItem = event.player.getCurrentEquippedItem();
			if(GasesFrameworkAPI.gasTypeAir.pipeBlock != null && currentPlayerItem != null && currentPlayerItem.getItem() == Item.getItemFromBlock(GasesFrameworkAPI.gasTypeAir.pipeBlock))
			{
				final byte[] pumpingDirections = gasPipe.getPossiblePropellingDirections(world, x, y, z);
				
				final boolean[] flowSides = new boolean[6];
				final boolean[] reverseFlowSides = new boolean[6];
				final boolean[] dualFlowSides = new boolean[6];
				for(int side = 0; side < 6; side++)
				{
					switch(pumpingDirections[side])
					{
					case 1:
						flowSides[side] = true;
						break;
					case 2:
						reverseFlowSides[side] = true;
						break;
					case 3:
						dualFlowSides[side] = true;
						break;
					}
				}
				
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				GL11.glTranslated(xd, yd, zd);
	
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
				
				drawOverlay(flowSides, flow_indicator);
				drawOverlay(reverseFlowSides, flow_indicator_reverse);
				drawOverlay(dualFlowSides, flow_indicator_dual);
				
				GL11.glPopMatrix();
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void drawOverlay(boolean[] sideConnections, ResourceLocation texture)
	{
		final double d0 = 11.0D / 16.0D;
		final double d1 = 1.0D - d0;
		final double uvd0 = 19.0D / 32.0D;
		
		double[] sideUVs = new double[6];
		double[] sides = new double[6];
		for(int side = 0; side < 6; side++)
		{
			double offsetUV = sideConnections[side] ? 1.0D : uvd0;
			double offset = sideConnections[side] ? 1.5D : d0;
			
			if((side & 1) == 0)
			{
				sideUVs[side] = offsetUV;
				sides[side] = offset;
			}
			else
			{
				sideUVs[side] = 1.0D - offsetUV;
				sides[side] = 1.0D - offset;
			}
		}
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setBrightness(15);
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		
		tessellator.addVertexWithUV(sides[3], sides[1], d0, sideUVs[3], sideUVs[1]);
		tessellator.addVertexWithUV(sides[2], sides[1], d0, sideUVs[2], sideUVs[1]);
		tessellator.addVertexWithUV(sides[2], sides[0], d0, sideUVs[2], sideUVs[0]);
		tessellator.addVertexWithUV(sides[3], sides[0], d0, sideUVs[3], sideUVs[0]);

		tessellator.addVertexWithUV(sides[3], sides[0], d1, sideUVs[3], sideUVs[0]);
		tessellator.addVertexWithUV(sides[2], sides[0], d1, sideUVs[2], sideUVs[0]);
		tessellator.addVertexWithUV(sides[2], sides[1], d1, sideUVs[2], sideUVs[1]);
		tessellator.addVertexWithUV(sides[3], sides[1], d1, sideUVs[3], sideUVs[1]);
		
		tessellator.addVertexWithUV(d0, sides[1], sides[5], sideUVs[1], sideUVs[5]);
		tessellator.addVertexWithUV(d0, sides[0], sides[5], sideUVs[0], sideUVs[5]);
		tessellator.addVertexWithUV(d0, sides[0], sides[4], sideUVs[0], sideUVs[4]);
		tessellator.addVertexWithUV(d0, sides[1], sides[4], sideUVs[1], sideUVs[4]);
		
		tessellator.addVertexWithUV(d1, sides[1], sides[4], sideUVs[1], sideUVs[4]);
		tessellator.addVertexWithUV(d1, sides[0], sides[4], sideUVs[0], sideUVs[4]);
		tessellator.addVertexWithUV(d1, sides[0], sides[5], sideUVs[0], sideUVs[5]);
		tessellator.addVertexWithUV(d1, sides[1], sides[5], sideUVs[1], sideUVs[5]);
		
		tessellator.addVertexWithUV(sides[3], d0, sides[5], sideUVs[5], sideUVs[3]);
		tessellator.addVertexWithUV(sides[3], d0, sides[4], sideUVs[4], sideUVs[3]);
		tessellator.addVertexWithUV(sides[2], d0, sides[4], sideUVs[4], sideUVs[2]);
		tessellator.addVertexWithUV(sides[2], d0, sides[5], sideUVs[5], sideUVs[2]);
		
		tessellator.addVertexWithUV(sides[2], d1, sides[5], sideUVs[5], sideUVs[2]);
		tessellator.addVertexWithUV(sides[2], d1, sides[4], sideUVs[4], sideUVs[2]);
		tessellator.addVertexWithUV(sides[3], d1, sides[4], sideUVs[4], sideUVs[3]);
		tessellator.addVertexWithUV(sides[3], d1, sides[5], sideUVs[5], sideUVs[3]);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		tessellator.draw();
	}
}