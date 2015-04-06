package glenn.gasesframework.client.gui;

import glenn.gasesframework.common.container.ContainerGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiGasFurnace extends GuiContainer
{
	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("gasesframework:textures/gui/container/furnace_gas.png");
	private TileEntityGasFurnace furnaceInventory;
	
	public GuiGasFurnace(InventoryPlayer par1InventoryPlayer, TileEntityGasFurnace par2TileEntityYourFurnace)
	{
		super(new ContainerGasFurnace(par1InventoryPlayer, par2TileEntityYourFurnace));
		this.furnaceInventory = par2TileEntityYourFurnace;
	}
	/**
	         * Draw the foreground layer for the GuiContainer (everything in front of the items)
	         */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRendererObj.drawString(StatCollector.translateToLocal("Gas Furnace"), 40, 6, 4210752);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
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
	    int i1 = this.furnaceInventory.getStage();
        this.drawTexturedModalRect(k + 55, l + 34, 176 + (this.furnaceInventory.furnaceBurnTime > 0 ? 0 : 18), 31 + i1 * 18, 18, 18);
        
        i1 = 54 * this.furnaceInventory.furnaceBurnTime / TileEntityGasFurnace.maxFurnaceBurnTime;
        this.drawTexturedModalRect(k + 29, l + 70 - i1, 212, 54 - i1, 26, i1);

	    i1 = this.furnaceInventory.getCookProgressScaled(24);
	    this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
	}
}