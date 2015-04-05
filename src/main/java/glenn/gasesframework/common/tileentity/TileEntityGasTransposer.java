package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.IGasTransposerExtractHandler;
import glenn.gasesframework.api.IGasTransposerHandler;
import glenn.gasesframework.api.IGasTransposerInsertHandler;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGasTransposer extends TileEntity implements ISidedInventory
{
	public static enum Mode
	{
		INSERT
		{
			private final LinkedList<IGasTransposerInsertHandler> handlers = new LinkedList<IGasTransposerInsertHandler>();
			
			@Override
			public void registerHandler(IGasTransposerHandler handler)
			{
				if(handler instanceof IGasTransposerInsertHandler)
				{
					handlers.add((IGasTransposerInsertHandler)handler);
				}
			}
			
			@Override
			public void validate(TileEntityGasTransposer tileEntity)
			{
				IGasTransposerInsertHandler handler = (IGasTransposerInsertHandler)tileEntity.currentHandler;
				if(handler != null)
				{
					if(tileEntity.itemStacks[0] == null || !handler.isValidInputItemStack(tileEntity.itemStacks[0]) || !handler.isValidInputGasType(tileEntity.containedType))
					{
						tileEntity.setHandler(null, 0);
					}
				}
			}
			
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
			
			@Override
			public void tick(TileEntityGasTransposer tileEntity)
			{
				
			}
			
			@Override
			public boolean complete(TileEntityGasTransposer tileEntity)
			{
				IGasTransposerInsertHandler handler = (IGasTransposerInsertHandler)tileEntity.currentHandler;
				if(handler.completeInsertion(tileEntity.itemStacks[0], tileEntity.itemStacks[1], tileEntity.containedType))
				{
					tileEntity.itemStacks[0] = handler.getInsertionInputStack(tileEntity.itemStacks[0], tileEntity.containedType);
					tileEntity.itemStacks[1] = handler.getInsertionOutputStack(tileEntity.itemStacks[1], tileEntity.containedType);
					tileEntity.containedType = null;
					return true;
				}
				else
				{
					return false;
				}
			}
			
			@Override
			public boolean canReceiveGas(TileEntityGasTransposer tileEntity, GasType gasType)
			{
				IGasTransposerInsertHandler handler = getHandlerForItemAndGasType(tileEntity.itemStacks[0], gasType);
				
				if(handler != null)
				{
					return handler != tileEntity.currentHandler;
				}
				else
				{
					return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir;
				}
			}
			
			@Override
			public boolean receiveGas(TileEntityGasTransposer tileEntity, GasType gasType)
			{
				IGasTransposerInsertHandler handler = getHandlerForItemAndGasType(tileEntity.itemStacks[0], gasType);
				
				if(handler != null)
				{
					if(handler != tileEntity.currentHandler)
					{
						tileEntity.setHandler(handler, handler.getInsertionTime());
						tileEntity.containedType = gasType;
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir;
				}
			}
			
			@Override
			public boolean canPropelGas(TileEntityGasTransposer tileEntity)
			{
				return false;
			}
			
			private Collection<IGasTransposerInsertHandler> getHandlersForItem(ItemStack itemstack)
			{
				LinkedList<IGasTransposerInsertHandler> itemHandlers = new LinkedList<IGasTransposerInsertHandler>();
				if(itemstack != null)
				{
					for(IGasTransposerInsertHandler handler : handlers)
					{
						if(handler.isValidInputItemStack(itemstack)) itemHandlers.push(handler);
					}
				}
				return itemHandlers;
			}
			
			private IGasTransposerInsertHandler getHandlerForItemAndGasType(ItemStack itemstack, GasType gasType)
			{
				for(IGasTransposerInsertHandler handler : getHandlersForItem(itemstack))
				{
					if(handler.isValidInputGasType(gasType)) return handler;
				}
				
				return null;
			}
		},
		EXTRACT
		{
			private final LinkedList<IGasTransposerExtractHandler> handlers = new LinkedList<IGasTransposerExtractHandler>();
			
			@Override
			public void registerHandler(IGasTransposerHandler handler)
			{
				if(handler instanceof IGasTransposerExtractHandler)
				{
					handlers.add((IGasTransposerExtractHandler)handler);
				}
			}
			
			@Override
			public void validate(TileEntityGasTransposer tileEntity)
			{
				IGasTransposerExtractHandler handler = (IGasTransposerExtractHandler)tileEntity.currentHandler;
				if(handler != null)
				{
					if(tileEntity.itemStacks[1] == null || handler.getOutputGasType(tileEntity.itemStacks[1]) == null)
					{
						tileEntity.setHandler(null, 0);
					}
				}
			}
			
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
			
			@Override
			public void tick(TileEntityGasTransposer tileEntity)
			{
				if(tileEntity.currentHandler == null)
				{
					ItemStack itemstack = tileEntity.itemStacks[1];
					if(itemstack != null)
					{
						for(IGasTransposerExtractHandler handler : handlers)
						{
							if(handler.getOutputGasType(itemstack) != null)
							{
								tileEntity.setHandler(handler, handler.getExtractionTime());
								break;
							}
						}
					}
				}
				
				if(tileEntity.currentHandler != null)
				{
					IGasTransposerExtractHandler extractHandler = (IGasTransposerExtractHandler)tileEntity.currentHandler;
				}
				
				if(tileEntity.containedType != null)
				{
					ForgeDirection direction = ForgeDirection.getOrientation(tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
					
					int x = tileEntity.xCoord + direction.offsetX;
					int y = tileEntity.yCoord + direction.offsetY;
					int z = tileEntity.zCoord + direction.offsetZ;
					
					Block block = tileEntity.worldObj.getBlock(x, y, z);
			    	if(block instanceof IGasReceptor)
					{
						if(((IGasReceptor)block).receiveGas(tileEntity.worldObj, x, y, z, direction.getOpposite(), tileEntity.containedType))
						{
							tileEntity.containedType = null;
						}
					}
					else
					{
						if(GasesFrameworkAPI.fillWithGas(tileEntity.worldObj, tileEntity.worldObj.rand, x, y, z, tileEntity.containedType))
						{
							tileEntity.containedType = null;
						}
					}
				}
			}
			
			@Override
			public boolean complete(TileEntityGasTransposer tileEntity)
			{
				IGasTransposerExtractHandler handler = (IGasTransposerExtractHandler)tileEntity.currentHandler;
				
				if(tileEntity.containedType == null)
				{
					GasType gasType = handler.getOutputGasType(tileEntity.itemStacks[1]);
					if(handler.completeExtraction(tileEntity.itemStacks[1], tileEntity.itemStacks[0], gasType))
					{
						tileEntity.containedType = gasType;
						tileEntity.itemStacks[1] = handler.getExtractionInputStack(tileEntity.itemStacks[1], tileEntity.containedType);
						tileEntity.itemStacks[0] = handler.getExtractionOutputStack(tileEntity.itemStacks[0], tileEntity.containedType);
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			
			@Override
			public boolean canReceiveGas(TileEntityGasTransposer tileEntity, GasType gasType)
			{
				return false;
			}
			
			@Override
			public boolean receiveGas(TileEntityGasTransposer tileEntity, GasType gasType)
			{
				return canReceiveGas(tileEntity, gasType);
			}
			
			@Override
			public boolean canPropelGas(TileEntityGasTransposer tileEntity)
			{
				return true;
			}
		};
		
		public abstract void registerHandler(IGasTransposerHandler handler);
		public abstract void validate(TileEntityGasTransposer tileEntity);
		public abstract boolean isItemValidForSlot(int slot, ItemStack itemstack);
		public abstract int[] getAccessibleSlotsFromSide(int side);
		public abstract boolean canExtractItem(int slot, ItemStack itemstack, int side);
		public abstract String getUnlocalizedName();
		public abstract void tick(TileEntityGasTransposer tileEntity);
		public abstract boolean complete(TileEntityGasTransposer tileEntity);
		public abstract boolean canReceiveGas(TileEntityGasTransposer tileEntity, GasType gasType);
		public abstract boolean receiveGas(TileEntityGasTransposer tileEntity, GasType gasType);
		public abstract boolean canPropelGas(TileEntityGasTransposer tileEntity);
	}
	
	public static void registerHandler(IGasTransposerHandler handler)
	{
		for(Mode mode : Mode.values())
		{
			mode.registerHandler(handler);
		}
	}
	
	private ItemStack[] itemStacks = new ItemStack[2];
	private String customName;
	public Mode mode = Mode.INSERT;
	public int time = 0;
	public int totalTime = 0;
	public GasType containedType = null;
	private IGasTransposerHandler currentHandler;
	
	private void setHandler(IGasTransposerHandler handler, int totalTime)
	{
		if(currentHandler != handler)
		{
			this.currentHandler = handler;
			this.time = 0;
			this.totalTime = totalTime;
		}
	}
	
	public void toggleMode()
	{
		int modeOrdinal = (mode.ordinal() + 1) % Mode.values().length;
		setMode(modeOrdinal);
	}
	
	public int getScaledProgress(int max)
	{
		if(totalTime > 0)
		{
			return max * time / totalTime;
		}
		else
		{
			return 0;
		}
	}
	
	public void setMode(int ordinal)
	{
		Mode[] modes = mode.values();
		if(ordinal >= 0 && ordinal < modes.length)
		{
			mode = modes[ordinal];
		}
		
		setHandler(null, 0);
	}
	
	@Override
    public void updateEntity()
    {
		if(!worldObj.isRemote)
		{
			if(currentHandler != null)
			{
				time++;
				if(time >= totalTime)
				{
					if(mode.complete(this))
					{
						setHandler(null, 0);
					}
					else
					{
						time = totalTime;
					}
				}
			}
			
			mode.tick(this);
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
        
        time = tagCompound.getInteger("time");
        containedType = GasType.getGasTypeByID(tagCompound.getInteger("containedType"));
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
		tagCompound.setInteger("time", time);
		tagCompound.setInteger("containedType", containedType != null ? containedType.gasID : -1);
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
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
		ItemStack result = null;
		if(stackInSlot != null)
		{
			if(stackInSlot.stackSize < amount)
			{
				itemStacks[slot] = null;
				result = stackInSlot;
			}
			else
			{
				result = stackInSlot.splitStack(amount);
				
				if(stackInSlot.stackSize <= 0)
				{
					itemStacks[slot] = null;
				}
			}
		}
		
		mode.validate(this);
		
		return result;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stackInSlot = itemStacks[slot];
		if(stackInSlot != null)
		{
			itemStacks[slot] = null;
			mode.validate(this);
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
		
		mode.validate(this);
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