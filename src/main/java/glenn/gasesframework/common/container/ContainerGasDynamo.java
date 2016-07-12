package glenn.gasesframework.common.container;

import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerGasDynamo extends Container
{
	public static final int GUI_ID = 2;

	private TileEntityGasDynamo tileEntity;

	public ContainerGasDynamo(InventoryPlayer inventoryPlayer, TileEntityGasDynamo tileEntity)
	{
		this.tileEntity = tileEntity;
		int i;
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.tileEntity.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex >= 0 && slotIndex < 27)
			{
				// This is from the player's main inventory
				if (!this.mergeItemStack(itemstack1, 27, 36, false))
				{
					return null;
				}
			}
			else if (slotIndex >= 27 && slotIndex < 36)
			{
				// This is from the player's action bar
				if (!this.mergeItemStack(itemstack1, 0, 27, false))
				{
					return null;
				}
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(entityPlayer, itemstack1);
		}

		return itemstack;
	}
}