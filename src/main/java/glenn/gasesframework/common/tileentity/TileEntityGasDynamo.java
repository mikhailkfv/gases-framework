package glenn.gasesframework.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map.Entry;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import glenn.gasesframework.GasesFramework;

public abstract class TileEntityGasDynamo extends TileEntity implements IEnergyProvider, ISidedInventory
{
	private final EnergyStorage energyStorage;
	private final int maxFuelLevel;
	private final int fuelPerTick;

	private int fuelLevel;
	private String invName;
	
	public boolean isBurning;
	
	public TileEntityGasDynamo(int maxEnergy, int maxEnergyTransfer, int maxFuelLevel, int fuelPerTick)
	{
		this.energyStorage = new EnergyStorage(maxEnergy, maxEnergyTransfer);
		this.maxFuelLevel = maxFuelLevel;
		this.fuelPerTick = fuelPerTick;

		setFuelLevel(0);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		energyStorage.readFromNBT(tagCompound);
		fuelLevel = tagCompound.getInteger("fuelLevel");

        if (tagCompound.hasKey("CustomName"))
        {
            this.invName = tagCompound.getString("CustomName");
        }
		
		isBurning = fuelLevel > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		energyStorage.writeToNBT(tagCompound);
		tagCompound.setInteger("fuelLevel", fuelLevel);

        if (this.hasCustomInventoryName())
        {
            tagCompound.setString("CustomName", this.invName);
        }
	}
	
	public void setFuelLevel(int fuelLevel)
	{
		if(fuelLevel != this.fuelLevel)
		{
			this.fuelLevel = fuelLevel;
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 0, fuelLevel);
		}
	}
	
	private void burnFuel()
	{
		int capacity = Math.min(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored(), energyStorage.getMaxReceive());
		int burnableUnits = Math.min(fuelLevel, fuelPerTick);
		int energyCreated = Math.min(capacity, burnableUnits * GasesFramework.configurations.blocks.gasDynamo.energyPerFuel);
		
		setFuelLevel(fuelLevel - energyCreated / GasesFramework.configurations.blocks.gasDynamo.energyPerFuel);
		energyStorage.modifyEnergyStored(energyCreated);
	}
	
	public void updateEntity()
	{
		if(worldObj.isRemote) return;
		
		int previousEnergy = energyStorage.getEnergyStored();
		int previousFuelLevel = fuelLevel;
		
		burnFuel();
		
		EnumMap<ForgeDirection, IEnergyReceiver> energyReceivers = new EnumMap<ForgeDirection, IEnergyReceiver>(ForgeDirection.class);
		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity tileEntity = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if(tileEntity != null && tileEntity instanceof IEnergyReceiver)
			{
				energyReceivers.put(direction, (IEnergyReceiver)tileEntity);
			}
		}
		
		if(!energyReceivers.isEmpty())
		{
			int extract = Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()) / energyReceivers.size();
		
			for(Entry<ForgeDirection, IEnergyReceiver> entry : energyReceivers.entrySet())
			{
				energyStorage.modifyEnergyStored(-entry.getValue().receiveEnergy(entry.getKey(), extract, false));
			}
		}
		
		if(energyStorage.getEnergyStored() != previousEnergy)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1, energyStorage.getEnergyStored());
		}
	}
	
	public boolean blockEvent(int eventID, int eventParam)
	{
		switch(eventID)
		{
		case 0:
			fuelLevel = eventParam;
			break;
		case 1:
			energyStorage.setEnergyStored(eventParam);
			break;
		}
		
		boolean isBurning = fuelLevel > 1;
		
		if(worldObj != null && worldObj.isRemote && isBurning != this.isBurning)
		{
			this.isBurning = isBurning;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		return true;
	}
    
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound localNBTTagCompound = new NBTTagCompound();
		writeToNBT(localNBTTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 5, localNBTTagCompound);
	}
	
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
    	readFromNBT(packet.func_148857_g());
    }
	
	public int getFuelStored()
	{
		return fuelLevel;
	}
	
	public int getMaxFuelStored()
	{
		return maxFuelLevel;
	}
	
	@Override
	public boolean canConnectEnergy(ForgeDirection from)
	{
		return true;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return energyStorage.getMaxEnergyStored();
	}

	@Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

	@Override
	public int getSizeInventory()
	{
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		
	}

	@Override
	public String getInventoryName()
	{
        return this.hasCustomInventoryName() ? this.invName : "container.gasDynamo";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
        return this.invName != null && this.invName.length() > 0;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void openInventory()
	{
		
	}

	@Override
	public void closeInventory()
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack)
	{
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side)
	{
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side)
	{
		return false;
	}
}