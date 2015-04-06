package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.mechanical.IGasTransposerExtractHandler;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.mechanical.IGasTransposerInsertHandler;

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
		INSERT(0, 1)
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
					if(tileEntity.itemStacks[inputSlot] == null || !handler.isValidInputItemStack(tileEntity.itemStacks[inputSlot]) || !handler.isValidInputGasType(tileEntity.itemStacks[inputSlot], tileEntity.containedType))
					{
						tileEntity.setHandler(null, 0);
					}
				}
			}
			
			@Override
			public boolean isValidInput(ItemStack inputStack)
			{
				if(inputStack != null)
				{
					return !getHandlersForItem(inputStack).isEmpty();
				}
				else
				{
					return false;
				}
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
				if(handler.completeInsertion(tileEntity.itemStacks[inputSlot], tileEntity.itemStacks[outputSlot], tileEntity.containedType))
				{
					tileEntity.itemStacks[inputSlot] = handler.getInsertionInputStack(tileEntity.itemStacks[inputSlot], tileEntity.containedType);
					tileEntity.itemStacks[outputSlot] = handler.getInsertionOutputStack(tileEntity.itemStacks[outputSlot], tileEntity.containedType);
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
				IGasTransposerInsertHandler handler = getHandlerForItemAndGasType(tileEntity.itemStacks[inputSlot], gasType);
				
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
				IGasTransposerInsertHandler handler = getHandlerForItemAndGasType(tileEntity.itemStacks[inputSlot], gasType);
				
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
			
			private Collection<IGasTransposerInsertHandler> getHandlersForItem(ItemStack inputStack)
			{
				LinkedList<IGasTransposerInsertHandler> itemHandlers = new LinkedList<IGasTransposerInsertHandler>();
				if(inputStack != null)
				{
					for(IGasTransposerInsertHandler handler : handlers)
					{
						if(handler.isValidInputItemStack(inputStack)) itemHandlers.push(handler);
					}
				}
				return itemHandlers;
			}
			
			private IGasTransposerInsertHandler getHandlerForItemAndGasType(ItemStack inputStack, GasType gasType)
			{
				for(IGasTransposerInsertHandler handler : getHandlersForItem(inputStack))
				{
					if(handler.isValidInputGasType(gasType)) return handler;
				}
				
				return null;
			}

			@Override
			public int getGuiArrowColor(TileEntityGasTransposer tileEntity)
			{
				if(tileEntity.containedType != null)
				{
					return tileEntity.containedType.color >> 8;
				}
				
				return 0xFFFFFF;
			}
		},
		EXTRACT(1, 0)
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
					if(tileEntity.itemStacks[inputSlot] == null || handler.getOutputGasType(tileEntity.itemStacks[inputSlot]) == null)
					{
						tileEntity.setHandler(null, 0);
					}
				}
			}
			
			@Override
			public boolean isValidInput(ItemStack inputStack)
			{
				if(inputStack != null)
				{
					return getHandlerFromItem(inputStack) != null;
				}
				else
				{
					return false;
				}
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
					ItemStack itemstack = tileEntity.itemStacks[inputSlot];
					if(itemstack != null)
					{
						IGasTransposerExtractHandler handler = getHandlerFromItem(itemstack);
						if(handler != null)
						{
							tileEntity.setHandler(handler, handler.getExtractionTime());
							tileEntity.pendingType = handler.getOutputGasType(itemstack);
						}
					}
				}
			}
			
			@Override
			public boolean complete(TileEntityGasTransposer tileEntity)
			{
				IGasTransposerExtractHandler handler = (IGasTransposerExtractHandler)tileEntity.currentHandler;
				
				if(handler.completeExtraction(tileEntity.itemStacks[1], tileEntity.itemStacks[outputSlot], tileEntity.pendingType))
				{
					ForgeDirection direction = ForgeDirection.getOrientation(tileEntity.worldObj.getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
					int x = tileEntity.xCoord + direction.offsetX;
					int y = tileEntity.yCoord + direction.offsetY;
					int z = tileEntity.zCoord + direction.offsetZ;
					
					Block block = tileEntity.worldObj.getBlock(x, y, z);
			    	if(block instanceof IGasReceptor)
					{
						if(((IGasReceptor)block).receiveGas(tileEntity.worldObj, x, y, z, direction.getOpposite(), tileEntity.pendingType))
						{
							tileEntity.itemStacks[inputSlot] = handler.getExtractionInputStack(tileEntity.itemStacks[inputSlot], tileEntity.pendingType);
							tileEntity.itemStacks[outputSlot] = handler.getExtractionOutputStack(tileEntity.itemStacks[outputSlot], tileEntity.pendingType);
							return true;
						}
					}
					else
					{
						if(GasesFrameworkAPI.fillWithGas(tileEntity.worldObj, tileEntity.worldObj.rand, x, y, z, tileEntity.pendingType))
						{
							tileEntity.itemStacks[inputSlot] = handler.getExtractionInputStack(tileEntity.itemStacks[inputSlot], tileEntity.pendingType);
							tileEntity.itemStacks[outputSlot] = handler.getExtractionOutputStack(tileEntity.itemStacks[outputSlot], tileEntity.pendingType);
							return true;
						}
					}
				}
				
				return false;
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

			@Override
			public int getGuiArrowColor(TileEntityGasTransposer tileEntity)
			{
				if(tileEntity.pendingType != null)
				{
					return tileEntity.pendingType.color >> 8;
				}
				
				return 0xFFFFFF;
			}
			
			private IGasTransposerExtractHandler getHandlerFromItem(ItemStack inputStack)
			{
				for(IGasTransposerExtractHandler handler : handlers)
				{
					if(handler.getOutputGasType(inputStack) != null)
					{
						return handler;
					}
				}
				
				return null;
			}
		};
		
		public final int inputSlot;
		public final int outputSlot;
		
		private Mode(int inputSlot, int outputSlot)
		{
			this.inputSlot = inputSlot;
			this.outputSlot = outputSlot;
		}
		
		public abstract void registerHandler(IGasTransposerHandler handler);
		public abstract void validate(TileEntityGasTransposer tileEntity);
		public abstract boolean isValidInput(ItemStack inputStack);
		public abstract String getUnlocalizedName();
		public abstract void tick(TileEntityGasTransposer tileEntity);
		public abstract boolean complete(TileEntityGasTransposer tileEntity);
		public abstract boolean canReceiveGas(TileEntityGasTransposer tileEntity, GasType gasType);
		public abstract boolean receiveGas(TileEntityGasTransposer tileEntity, GasType gasType);
		public abstract boolean canPropelGas(TileEntityGasTransposer tileEntity);
		public abstract int getGuiArrowColor(TileEntityGasTransposer tileEntity);
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
	public GasType pendingType = null;
	private IGasTransposerHandler currentHandler;
	
	private void setHandler(IGasTransposerHandler handler, int totalTime)
	{
		if(currentHandler != handler)
		{
			this.currentHandler = handler;
			this.time = 0;
			this.totalTime = totalTime;
			this.pendingType = null;
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
		return slot == mode.inputSlot && mode.isValidInput(itemstack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		if(side == 0)
		{
			return new int[]{ mode.outputSlot };
		}
		else
		{
			return new int[]{ mode.inputSlot };
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return slot == mode.outputSlot;
	}
}