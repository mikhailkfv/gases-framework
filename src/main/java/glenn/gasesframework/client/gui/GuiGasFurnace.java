package glenn.gasesframework.client.gui;

import glenn.gasesframework.common.container.ContainerGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiGasFurnace extends GuiContainer
{
	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("gasesframework:textures/gui/container/furnace_gas.png");
	private TileEntityGasFurnace tileEntity;
	
	public GuiGasFurnace(InventoryPlayer inventoryPlayer, TileEntityGasFurnace par2TileEntityYourFurnace)
	{
		super(new ContainerGasFurnace(inventoryPlayer, par2TileEntityYourFurnace));
		this.tileEntity = par2TileEntityYourFurnace;
	}
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String invName = this.tileEntity.hasCustomInventoryName() ? this.tileEntity.getInventoryName() : I18n.format(this.tileEntity.getInventoryName());
		this.fontRendererObj.drawString(invName, this.xSize / 2 - this.fontRendererObj.getStringWidth(invName) / 2, 6, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
	}
	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		
		//GL11.glColor4f(1.0F, 1.0F, 1.0F, (this.furnaceInventory.furnaceBurnTime > 0 & this.furnaceInventory.canSmelt()) ? 1.0F : 0.5F);
		int i1 = this.tileEntity.getStage();
		this.drawTexturedModalRect(k + 55, l + 34, 176 + (this.tileEntity.furnaceBurnTime > 0 ? 0 : 18), 31 + i1 * 18, 18, 18);
		
		i1 = 54 * this.tileEntity.furnaceBurnTime / TileEntityGasFurnace.maxFurnaceBurnTime;
		this.drawTexturedModalRect(k + 29, l + 70 - i1, 212, 54 - i1, 26, i1);

		i1 = this.tileEntity.getCookProgressScaled(24);
		this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
	}
}