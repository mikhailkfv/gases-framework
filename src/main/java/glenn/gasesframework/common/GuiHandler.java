package glenn.gasesframework.common;

import glenn.gasesframework.client.gui.GuiGasDynamo;
import glenn.gasesframework.client.gui.GuiGasFurnace;
import glenn.gasesframework.client.gui.GuiGasTransposer;
import glenn.gasesframework.common.container.ContainerGasDynamo;
import glenn.gasesframework.common.container.ContainerGasFurnace;
import glenn.gasesframework.common.container.ContainerGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		switch(id)
		{
		case ContainerGasFurnace.GUI_ID:
			return new ContainerGasFurnace(player.inventory, (TileEntityGasFurnace)tileEntity);
		case ContainerGasTransposer.GUI_ID:
			return new ContainerGasTransposer(player.inventory, (TileEntityGasTransposer)tileEntity);
		case ContainerGasDynamo.GUI_ID:
			return new ContainerGasDynamo(player.inventory, (TileEntityGasDynamo)tileEntity);
		}
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		switch(id)
		{
		case ContainerGasFurnace.GUI_ID:
			return new GuiGasFurnace(player.inventory, (TileEntityGasFurnace)tileEntity);
		case ContainerGasTransposer.GUI_ID:
			return new GuiGasTransposer(player.inventory, (TileEntityGasTransposer)tileEntity);
		case ContainerGasDynamo.GUI_ID:
			return new GuiGasDynamo(player.inventory, (TileEntityGasDynamo)tileEntity);
		}
		
		return null;
	}
}