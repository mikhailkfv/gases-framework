package glenn.gasesframework.client;

import glenn.gasesframework.api.GFAPI;
import glenn.gasesframework.client.render.RenderPlayerGag;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.gasesframework.api.MaterialGas;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderVillagerGag;
import glenn.gasesframework.common.DuctTapeGag;
import glenn.gasesframework.common.block.BlockGasPipe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ForgeClientEvents
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogDensity(FogDensity event)
	{
		ExtendedGasEffectsBase gasEffects = ExtendedGasEffectsBase.get(event.entity);

		float f = gasEffects.get(ExtendedGasEffectsBase.EffectType.BLINDNESS) / 250.0f;

		if (f > 0.0f)
		{
			event.density = f * f + 0.01f;
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogColor(FogColors event)
	{
		ExtendedGasEffectsBase gasEffects = ExtendedGasEffectsBase.get(event.entity);

		float f = 1.0f / (gasEffects.get(ExtendedGasEffectsBase.EffectType.BLINDNESS) / 30.0f + 1.0f);
		event.red *= f;
		event.green *= f;
		event.blue *= f;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFOVUpdate(FOVUpdateEvent event)
	{
		float f = ExtendedGasEffectsBase.get(event.entity).get(ExtendedGasEffectsBase.EffectType.BLINDNESS) / 500.0f;

		event.newfov = event.fov * (2.0f - f * f) / 2.0f;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPreDrawScreen(RenderGameOverlayEvent event)
	{
		if (event.type == RenderGameOverlayEvent.ElementType.ALL)
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP player = mc.thePlayer;

			if (player != null && player.isInsideOfMaterial(MaterialGas.INSTANCE))
			{
				int blockX = MathHelper.floor_double(player.posX);
				int blockY = MathHelper.floor_double(player.posY + player.getEyeHeight());
				int blockZ = MathHelper.floor_double(player.posZ);

				GasType type = GasesFramework.implementation.getGasType(player.worldObj, blockX, blockY, blockZ);

				if (type.overlayImage != null && type.isVisible())
				{
					mc.getTextureManager().bindTexture(type.overlayImage);
					Tessellator tessellator = Tessellator.instance;
					float brightness = player.getBrightness(event.partialTicks);

					float red = brightness * (float) ((type.color >> 24) & 0xFF) / 255.0F;
					float green = brightness * (float) ((type.color >> 16) & 0xFF) / 255.0F;
					float blue = brightness * (float) ((type.color >> 8) & 0xFF) / 255.0F;
					float alpha = (float) (type.color & 0xFF) / 255.0f;

					GL11.glColor4f(red, green, blue, alpha);
					GL11.glEnable(GL11.GL_BLEND);
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					GL11.glPushMatrix();
					float f2 = 4.0F;
					float f3 = -1.0F;
					float f4 = 1.0F;
					float f5 = -1.0F;
					float f6 = 1.0F;
					float f7 = -0.5F;
					float f8 = -player.rotationYaw / 64.0F + (float) player.posX / 2.0f;
					float f9 = player.rotationPitch / 64.0F + (float) player.posZ / 2.0f;
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV((double) f3, (double) f5, (double) f7, (double) (f2 + f8), (double) (f2 + f9));
					tessellator.addVertexWithUV((double) f4, (double) f5, (double) f7, (double) (0.0F + f8), (double) (f2 + f9));
					tessellator.addVertexWithUV((double) f4, (double) f6, (double) f7, (double) (0.0F + f8), (double) (0.0F + f9));
					tessellator.addVertexWithUV((double) f3, (double) f6, (double) f7, (double) (f2 + f8), (double) (0.0F + f9));
					tessellator.draw();
					GL11.glPopMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_BLEND);
				}
			}
		}
	}

	private final ResourceLocation flow_indicator = new ResourceLocation("gasesframework", "textures/misc/flow_indicator.png");
	private final ResourceLocation flow_indicator_reverse = new ResourceLocation("gasesframework", "textures/misc/flow_indicator_reverse.png");
	private final ResourceLocation flow_indicator_dual = new ResourceLocation("gasesframework", "textures/misc/flow_indicator_dual.png");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre event)
	{
		if (event.map == Minecraft.getMinecraft().getTextureMapBlocks())
		{
			SharedBlockIcons.registerIcons(event.map);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event)
	{
		int x = event.target.blockX;
		int y = event.target.blockY;
		int z = event.target.blockZ;
		double xd = x - (event.player.lastTickPosX + (event.player.posX - event.player.lastTickPosX) * (double) event.partialTicks);
		double yd = y - (event.player.lastTickPosY + (event.player.posY - event.player.lastTickPosY) * (double) event.partialTicks);
		double zd = z - (event.player.lastTickPosZ + (event.player.posZ - event.player.lastTickPosZ) * (double) event.partialTicks);
		World world = event.player.worldObj;
		Block block = world.getBlock(x, y, z);
		if (block instanceof BlockGasPipe)
		{
			BlockGasPipe gasPipe = (BlockGasPipe) block;

			ItemStack currentPlayerItem = event.player.getCurrentEquippedItem();
			if (currentPlayerItem != null && currentPlayerItem.getItem() == Item.getItemFromBlock(GasesFramework.registry.getGasPipeBlock(GFAPI.gasTypeAir)))
			{
				final byte[] pumpingDirections = gasPipe.getPossiblePropellingDirections(world, x, y, z);

				final boolean[] flowSides = new boolean[6];
				final boolean[] reverseFlowSides = new boolean[6];
				final boolean[] dualFlowSides = new boolean[6];
				for (int side = 0; side < 6; side++)
				{
					switch (pumpingDirections[side])
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
		for (int side = 0; side < 6; side++)
		{
			double offsetUV = sideConnections[side] ? 1.0D : uvd0;
			double offset = sideConnections[side] ? 1.5D : d0;

			if ((side & 1) == 1)
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

		tessellator.addVertexWithUV(sides[4], sides[0], d0, sideUVs[4], sideUVs[0]);
		tessellator.addVertexWithUV(sides[5], sides[0], d0, sideUVs[5], sideUVs[0]);
		tessellator.addVertexWithUV(sides[5], sides[1], d0, sideUVs[5], sideUVs[1]);
		tessellator.addVertexWithUV(sides[4], sides[1], d0, sideUVs[4], sideUVs[1]);

		tessellator.addVertexWithUV(sides[4], sides[1], d1, sideUVs[4], sideUVs[1]);
		tessellator.addVertexWithUV(sides[5], sides[1], d1, sideUVs[5], sideUVs[1]);
		tessellator.addVertexWithUV(sides[5], sides[0], d1, sideUVs[5], sideUVs[0]);
		tessellator.addVertexWithUV(sides[4], sides[0], d1, sideUVs[4], sideUVs[0]);

		tessellator.addVertexWithUV(d0, sides[0], sides[2], sideUVs[0], sideUVs[2]);
		tessellator.addVertexWithUV(d0, sides[1], sides[2], sideUVs[1], sideUVs[2]);
		tessellator.addVertexWithUV(d0, sides[1], sides[3], sideUVs[1], sideUVs[3]);
		tessellator.addVertexWithUV(d0, sides[0], sides[3], sideUVs[0], sideUVs[3]);

		tessellator.addVertexWithUV(d1, sides[0], sides[3], sideUVs[0], sideUVs[3]);
		tessellator.addVertexWithUV(d1, sides[1], sides[3], sideUVs[1], sideUVs[3]);
		tessellator.addVertexWithUV(d1, sides[1], sides[2], sideUVs[1], sideUVs[2]);
		tessellator.addVertexWithUV(d1, sides[0], sides[2], sideUVs[0], sideUVs[2]);

		tessellator.addVertexWithUV(sides[4], d0, sides[2], sideUVs[2], sideUVs[4]);
		tessellator.addVertexWithUV(sides[4], d0, sides[3], sideUVs[3], sideUVs[4]);
		tessellator.addVertexWithUV(sides[5], d0, sides[3], sideUVs[3], sideUVs[5]);
		tessellator.addVertexWithUV(sides[5], d0, sides[2], sideUVs[2], sideUVs[5]);

		tessellator.addVertexWithUV(sides[5], d1, sides[2], sideUVs[2], sideUVs[5]);
		tessellator.addVertexWithUV(sides[5], d1, sides[3], sideUVs[3], sideUVs[5]);
		tessellator.addVertexWithUV(sides[4], d1, sides[3], sideUVs[3], sideUVs[4]);
		tessellator.addVertexWithUV(sides[4], d1, sides[2], sideUVs[2], sideUVs[4]);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		tessellator.draw();
	}

	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Post event)
	{
		if (DuctTapeGag.isGagged(event.entity))
		{
			EntityLivingBase entity = event.entity;
			RendererLivingEntity gagRenderer = null;
			if (event.renderer instanceof RenderVillager && !(event.renderer instanceof RenderVillagerGag))
			{
				gagRenderer = ((ClientProxy) GasesFramework.proxy).renderVillagerGag;
			}
			else if (event.renderer instanceof RenderPlayer && !(event.renderer instanceof RenderPlayerGag))
			{
				gagRenderer = ((ClientProxy) GasesFramework.proxy).renderPlayerGag;
			}

			if (gagRenderer != null)
			{
				gagRenderer.doRender((Entity) entity, event.x, event.y, event.z, entity.rotationYaw, 1.0F);
			}
		}
	}
}