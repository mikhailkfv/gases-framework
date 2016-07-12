package glenn.gasesframework.client.gui;

import org.lwjgl.opengl.GL11;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.container.ContainerGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.gasesframework.network.message.MessageSetTransposerMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiGasTransposer extends GuiContainer
{
	private static final ResourceLocation transposerGuiTextures = new ResourceLocation("gasesframework:textures/gui/container/transposer.png");
	private final TileEntityGasTransposer tileEntity;

	public GuiGasTransposer(InventoryPlayer inventoryPlayer, TileEntityGasTransposer tileEntity)
	{
		super(new ContainerGasTransposer(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int guiMinX = (width - xSize) / 2;
		int guiMinY = (height - ySize) / 2;

		buttonList.add(new GuiButton(0, guiMinX + 64, guiMinY + 60, 48, 20, I18n.format("container.gasTransposer.toggleMode")));
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 0)
		{
			tileEntity.toggleMode();
			GasesFramework.networkWrapper.sendToServer(new MessageSetTransposerMode(tileEntity));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String inventoryName = tileEntity.hasCustomInventoryName() ? tileEntity.getInventoryName() : I18n.format(tileEntity.getInventoryName());
		this.fontRendererObj.drawString(inventoryName, xSize / 2 - fontRendererObj.getStringWidth(inventoryName) / 2, 6, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(transposerGuiTextures);
		int guiMinX = (width - xSize) / 2;
		int guiMinY = (height - ySize) / 2;
		drawTexturedModalRect(guiMinX, guiMinY, 0, 0, xSize, ySize);

		int progress = tileEntity.getScaledProgress(38);
		int invertedProgress = 38 - progress;

		int color = tileEntity.mode.getGuiArrowColor(tileEntity);
		float red = (float) ((color >> 16) & 0xFF) / 255.0F;
		float green = (float) ((color >> 8) & 0xFF) / 255.0F;
		float blue = (float) (color & 0xFF) / 255.0F;

		if (tileEntity.mode == tileEntity.mode.INSERT)
		{
			drawTexturedModalRect(guiMinX + 69, guiMinY + 35, 176, 0, 38, 16);

			GL11.glColor3f(red, green, blue);
			drawTexturedModalRect(guiMinX + 69, guiMinY + 35, 214, 0, progress, 16);
		}
		else if (tileEntity.mode == tileEntity.mode.EXTRACT)
		{
			drawTexturedModalRect(guiMinX + 69, guiMinY + 35, 176, 16, 38, 16);

			GL11.glColor3f(red, green, blue);
			drawTexturedModalRect(guiMinX + 69 + invertedProgress, guiMinY + 35, 214 + invertedProgress, 16, progress, 16);
		}
	}
}