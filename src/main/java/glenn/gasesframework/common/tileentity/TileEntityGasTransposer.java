package glenn.gasesframework.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGasTransposer extends TileEntity implements ISidedInventory
{
	public enum Mode
	{
		INSERT {
			@Override
			public boolean isItemValidForSlot(int slot, ItemStack itemstack)
			{
				return slot == 0;
			}
			
			@Override
			public int[] getAccessibleSlotsFromSide(int side)
			{
				if(side == 0)
				{
					return new int[]{ 1 };
				}
				else
				{
					return new int[]{ 0 };
				}
			}
			
			@Override
			public boolean canExtractItem(int slot, ItemStack itemstack, int side)
			{
				return slot == 1;
			}
			
			@Override
			public String getUnlocalizedName()
			{
				return "container.gasTransposer.insert.name";
			}
		},
		EXTRACT {
			@Override
			public boolean isItemValidForSlot(int slot, ItemStack itemstack)
			{
				return slot == 1;
			}

			@Override
			public int[] getAccessibleSlotsFromSide(int side)
			{
				if(side == 0)
				{
					return new int[]{ 0 };
				}
				else
				{
					return new int[]{ 1 };
				}
			}

			@Override
			public boolean canExtractItem(int slot, ItemStack itemstack, int side)
			{
				return slot == 0;
			}
			
			@Override
			public String getUnlocalizedName()
			{
				return "container.gasTransposer.extract.name";
			}
		};
		
		public abstract boolean isItemValidForSlot(int slot, ItemStack itemstack);
		public abstract int[] getAccessibleSlotsFromSide(int side);
		public abstract boolean canExtractItem(int slot, ItemStack itemstack, int side);
		public abstract String getUnlocalizedName();
	}
	
	
	private ItemStack[] itemStacks = new ItemStack[2];
	private String customName;
	public Mode mode = Mode.INSERT;
	public int time = 0;
	public int totalTime = 50;
	
	public void toggleMode()
	{
		int modeOrdinal = (mode.ordinal() + 1) % Mode.values().length;
		mode = Mode.values()[modeOrdinal];
	}
	
	public int getScaledProgress(int max)
	{
		return max * time / totalTime;
	}
	
	public void setMode(int ordinal)
	{
		Mode[] modes = mode.values();
		if(ordinal >= 0 && ordinal < modes.length)
		{
			mode = modes[ordinal];
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		NBTTagList nbttaglist = tagCompound.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound itemCompound = nbttaglist.getCompoundTagAt(i);
            byte slot = itemCompound.getByte("Slot");

            if (slot >= 0 && slot < getSizeInventory())
            {
                itemStacks[slot] = ItemStack.loadItemStackFromNBT(itemCompound);
            }
        }
        
        try
        {
        	mode = Mode.valueOf(tagCompound.getString("Mode"));
        }
        catch(Exception e)
        {}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		
		NBTTagList itemTags = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++)
		{
			if (itemStacks[i] != null)
			{
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte)i);
				itemStacks[i].writeToNBT(itemTag);
				itemTags.appendTag(itemTag);
			}
		}
		tagCompound.setTag("Items", itemTags);
		
		tagCompound.setString("Mode", mode.toString());
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }
	
	@Override
    public void updateEntity()
    {
		if(!worldObj.isRemote)
		{
			time = (time + 1) % totalTime;
		}
    }
	
	@Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer)
    {
		if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != this)
		{
			return false;
		}
		else
		{
			return entityPlayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
		}
    }

	@Override
	public int getSizeInventory()
	{
		return itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return itemStacks[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stackInSlot = itemStacks[slot];
		if(stackInSlot != null)
		{
			if(stackInSlot.stackSize < amount)
			{
				itemStacks[slot] = null;
				return stackInSlot;
			}
			else
			{
				ItemStack result = stackInSlot.splitStack(amount);
				
				if(stackInSlot.stackSize <= 0)
				{
					itemStacks[slot] = null;
				}
				
				return result;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stackInSlot = itemStacks[slot];
		if(stackInSlot != null)
		{
			itemStacks[slot] = null;
			return stackInSlot;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		itemStacks[slot] = itemstack;
		
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName()
	{
		return hasCustomInventoryName() ? customName : mode.getUnlocalizedName();
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return customName != null && customName.length() > 0;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void openInventory()
	{}

	@Override
	public void closeInventory()
	{}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return mode.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return mode.getAccessibleSlotsFromSide(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return mode.canExtractItem(slot, itemstack, side);
	}
}