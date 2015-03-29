package glenn.gasesframework.common;

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

public class ContainerGasFurnace extends Container
{
    private TileEntityGasFurnace furnace;
    private int lastCookTime;
    private int lastCookSpeed;
    private int lastBurnTime;
    
    private boolean forceUpdate;

    public ContainerGasFurnace(InventoryPlayer par1InventoryPlayer, TileEntityGasFurnace par2TileEntityFurnace)
    {
        this.furnace = par2TileEntityFurnace;
        this.addSlotToContainer(new Slot(par2TileEntityFurnace, 0, 56, 17));
        this.addSlotToContainer(new SlotFurnace(par1InventoryPlayer.player, par2TileEntityFurnace, 1, 116, 35));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
        }
        
        forceUpdate = true;
    }

    @Override
    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, (int)this.furnace.furnaceCookTime);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.furnace.furnaceBurnTime);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            
            if (this.lastCookTime != this.furnace.furnaceCookTime | forceUpdate)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.furnace.furnaceCookTime);
            }
            
            if (this.lastCookSpeed != this.furnace.furnaceCookSpeed | forceUpdate)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.furnace.furnaceCookSpeed);
            }
            
            if (this.lastBurnTime != this.furnace.furnaceBurnTime | forceUpdate)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.furnace.furnaceBurnTime);
            }
        }

        this.lastCookTime = this.furnace.furnaceCookTime;
        this.lastCookSpeed = this.furnace.furnaceCookSpeed;
        this.lastBurnTime = this.furnace.furnaceBurnTime;
        
        forceUpdate = false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            this.furnace.furnaceCookTime = par2;
        }
        
        if (par1 == 1)
        {
            this.furnace.furnaceCookSpeed = par2;
        }

        if (par1 == 2)
        {
            this.furnace.furnaceBurnTime = par2;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.furnace.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == 1)
            {
            	//Taking from the furnace output slot
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (slotIndex > 2)
            {
            	//Taking from an item somewhere in the player's inventory to be placed somewhere in the furnace
                if (TileEntityGasFurnace.getSpecialFurnaceRecipe(itemstack1) != null || FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null)
                {
                	//This is smeltable
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= 2 && slotIndex < 29)
                {
                	//This is from the player's main inventory
                    if (!this.mergeItemStack(itemstack1, 29, 38, false))
                    {
                        return null;
                    }
                }
                else if (slotIndex >= 29 && slotIndex < 38 && !this.mergeItemStack(itemstack1, 2, 29, false))
                {
                	//This is from the player's action bar
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 2, 38, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
}