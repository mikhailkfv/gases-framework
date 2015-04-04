package glenn.gasesframework.common.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer.Mode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerGasTransposer extends Container
{
	public static final int GUI_ID = 1;
	
	public TileEntityGasTransposer tileEntity;
	private TileEntityGasTransposer.Mode lastMode;
	private int lastTime;
	private int lastTotalTime;
	
	public ContainerGasTransposer(InventoryPlayer inventoryPlayer, TileEntityGasTransposer tileEntity)
	{
		this.tileEntity = tileEntity;
		
		addSlotToContainer(new Slot(tileEntity, 0, 44, 35));
		addSlotToContainer(new Slot(tileEntity, 1, 116, 35));
		
		for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 9; y++)
            {
                addSlotToContainer(new Slot(inventoryPlayer, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
            }
        }

        for (int y = 0; y < 9; y++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, y, 8 + y * 18, 142));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tileEntity.isUseableByPlayer(player);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int val)
	{
		switch(id)
		{
		case 0:
			tileEntity.setMode(val);
			break;
		case 1:
			tileEntity.time = val;
			break;
		case 2:
			tileEntity.totalTime = val;
			break;
		}
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)this.crafters.get(i);
			
			if(lastMode != tileEntity.mode)
			{
				icrafting.sendProgressBarUpdate(this, 0, tileEntity.mode.ordinal());
			}
			if(lastTime != tileEntity.time)
			{
				icrafting.sendProgressBarUpdate(this, 1, tileEntity.time);
			}
			if(lastTotalTime != tileEntity.totalTime)
			{
				icrafting.sendProgressBarUpdate(this, 2, tileEntity.totalTime);
			}
		}
		
		this.lastMode = tileEntity.mode;
		this.lastTime = tileEntity.time;
		this.lastTotalTime = tileEntity.totalTime;
	}
}