package glenn.gasesframework.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.sun.javafx.collections.MappingChange.Map;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import glenn.gasesframework.GasesFramework;

public class TileEntityGasDynamo extends TileEntity implements IEnergyProvider
{
	private EnergyStorage energyStorage;
	public int fuelLevel;
	public boolean isBurning;
	
	public TileEntityGasDynamo()
	{
		energyStorage = new EnergyStorage(GasesFramework.configurations.gasDynamo_maxEnergy, GasesFramework.configurations.gasDynamo_maxEnergyTransfer);
		setFuelLevel(0);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		energyStorage.readFromNBT(tagCompound);
		fuelLevel = tagCompound.getInteger("fuelLevel");
		
		isBurning = fuelLevel > 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		energyStorage.writeToNBT(tagCompound);
		tagCompound.setInteger("fuelLevel", fuelLevel);
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
		int burnableUnits = Math.min(fuelLevel, 4);
		int energyCreated = Math.min(capacity, burnableUnits * GasesFramework.configurations.gasDynamo_energyPerFuel);
		
		setFuelLevel(fuelLevel - energyCreated / GasesFramework.configurations.gasDynamo_energyPerFuel);
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
}