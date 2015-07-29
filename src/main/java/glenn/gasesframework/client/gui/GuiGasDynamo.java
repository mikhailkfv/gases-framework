package glenn.gasesframework.client.gui;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.container.ContainerGasDynamo;
import glenn.gasesframework.common.container.ContainerGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class GuiGasDynamo extends GuiContainer
{
	private static final ResourceLocation dynamoGuiTexture = new ResourceLocation("gasesframework:textures/gui/container/dynamo.png");
	private TileEntityGasDynamo tileEntity;
	
	public GuiGasDynamo(InventoryPlayer inventoryPlayer, TileEntityGasDynamo tileEntity)
	{
		super(new ContainerGasDynamo(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
	}
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String invName = this.tileEntity.hasCustomInventoryName() ? this.tileEntity.getInventoryName() : I18n.format(this.tileEntity.getInventoryName());
		this.fontRendererObj.drawString(invName, this.xSize / 2 - this.fontRendererObj.getStringWidth(invName) / 2, 6, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);

		String fuelAmount = I18n.format("container.gasDynamo.fuel.amount", this.tileEntity.getFuelStored(), this.tileEntity.getMaxFuelStored());
		String energyAmount = I18n.format("container.gasDynamo.energy.amount", this.tileEntity.getEnergyStored(ForgeDirection.UNKNOWN), this.tileEntity.getMaxEnergyStored(ForgeDirection.UNKNOWN));
		this.fontRendererObj.drawString(fuelAmount, 115 - this.fontRendererObj.getStringWidth(fuelAmount), 50, 0x404040);
		this.fontRendererObj.drawString(energyAmount, 115 - this.fontRendererObj.getStringWidth(energyAmount), 60, 0x404040);
	}
	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(dynamoGuiTexture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		
		int count = 54 * this.tileEntity.getFuelStored() / this.tileEntity.getMaxFuelStored();
		this.drawTexturedModalRect(k + 123, l + 16 + 54 - count, 176, 54 - count, 10, count);
		
		count = 54 * this.tileEntity.getEnergyStored(ForgeDirection.UNKNOWN) / this.tileEntity.getMaxEnergyStored(ForgeDirection.UNKNOWN);
		this.drawTexturedModalRect(k + 141, l + 16 + 54 - count, 186, 54 - count, 10, count);
	}
}