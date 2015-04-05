package glenn.gasesframework.common.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer.Mode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGasTransposer extends Container
{
	private static class SlotTransposer extends Slot
	{
		private final TileEntityGasTransposer tileEntity;
		
		public SlotTransposer(TileEntityGasTransposer tileEntity, int slotIndex, int x, int y)
		{
			super(tileEntity, slotIndex, x, y);
			this.tileEntity = tileEntity;
		}
		
		@Override
		public boolean isItemValid(ItemStack itemStack)
		{
			return getSlotIndex() == tileEntity.mode.inputSlot && tileEntity.mode.isValidInput(itemStack);
		}
	}
	
	public static final int GUI_ID = 1;
	
	public TileEntityGasTransposer tileEntity;
	private GasType lastContainedType;
	private TileEntityGasTransposer.Mode lastMode;
	private int lastTime;
	private int lastTotalTime;
	
	private boolean forceUpdate = true;
	
	public ContainerGasTransposer(InventoryPlayer inventoryPlayer, TileEntityGasTransposer tileEntity)
	{
		this.tileEntity = tileEntity;
		
		addSlotToContainer(new SlotTransposer(tileEntity, 0, 44, 35));
		addSlotToContainer(new SlotTransposer(tileEntity, 1, 116, 35));
		
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
			tileEntity.containedType = GasType.getGasTypeByID(val);
		case 1:
			tileEntity.setMode(val);
			break;
		case 2:
			tileEntity.time = val;
			break;
		case 3:
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
			
			if(forceUpdate || lastContainedType != tileEntity.containedType)
			{
				icrafting.sendProgressBarUpdate(this, 0, tileEntity.containedType != null ? tileEntity.containedType.gasID : -1);
			}
			if(forceUpdate || lastMode != tileEntity.mode)
			{
				icrafting.sendProgressBarUpdate(this, 1, tileEntity.mode.ordinal());
			}
			if(forceUpdate || lastTime != tileEntity.time)
			{
				icrafting.sendProgressBarUpdate(this, 2, tileEntity.time);
			}
			if(forceUpdate || lastTotalTime != tileEntity.totalTime)
			{
				icrafting.sendProgressBarUpdate(this, 3, tileEntity.totalTime);
			}
		}
		
		this.lastContainedType = tileEntity.containedType;
		this.lastMode = tileEntity.mode;
		this.lastTime = tileEntity.time;
		this.lastTotalTime = tileEntity.totalTime;
		this.forceUpdate = false;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(slotIndex);
		
		if(slot != null && slot.getHasStack())
		{
			ItemStack slotContents = slot.getStack();
			itemstack = slotContents.copy();
			
			if(slotIndex < 2)
			{
				//Left or right slot, should be placed in player's inventory
				if(!mergeItemStack(slotContents, 2, 38, true))
				{
					return null;
				}
				slot.onSlotChange(slotContents, itemstack);
			}
			else
			{
				//Player inventory slots
				int inputSlot = tileEntity.mode.inputSlot;
				
				if(tileEntity.mode.isValidInput(slotContents))
				{
					//This input can apply to the input slot of the gas transposer
					if(!mergeItemStack(slotContents, inputSlot, inputSlot + 1, false))
					{
						return null;
					}
				}
				else if(slotIndex < 29)
				{
					//This is from the player's main inventory and should be placed in the action bar
					if(!mergeItemStack(slotContents, 29, 38, false))
					{
						return null;
					}
				}
				else if(slotIndex < 38)
				{
					//This is from the player's action bar and should be placed in the main inventory
					if(!mergeItemStack(slotContents, 2, 29, false))
					{
						return null;
					}
				}
			}
			
			if(slotContents.stackSize <= 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
			
			if(itemstack.stackSize == slotContents.stackSize)
			{
				return null;
			}
			
			slot.onPickupFromSlot(entityPlayer, slotContents);
		}
		
		return itemstack;
	}
}