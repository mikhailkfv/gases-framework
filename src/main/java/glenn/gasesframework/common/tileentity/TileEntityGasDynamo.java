package glenn.gasesframework.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import glenn.gasesframework.GasesFramework;

public class TileEntityGasDynamo extends TileEntity implements IEnergyProvider
{
	private EnergyStorage energyStorage;
	public int fuelLevel;
	public boolean isBurning;
	public boolean hasEnergy;
	
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
		setFuelLevel(tagCompound.getInteger("fuelLevel"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		energyStorage.writeToNBT(tagCompound);
		tagCompound.setInteger("fuelLevel", fuelLevel);
	}
	
	private void updateEnergyState()
	{
		boolean hasEnergy = energyStorage.getEnergyStored() > 0;
		
		if(hasEnergy ^ this.hasEnergy)
		{
			if(worldObj != null)
			{
				worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1, hasEnergy ? 1 : 0);
			}
		}
	}
	
	public void setFuelLevel(int fuelLevel)
	{
		boolean isBurning = fuelLevel > 0;
		
		if(isBurning ^ this.isBurning)
		{
			if(worldObj != null)
			{
				worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 0, isBurning ? 1 : 0);
			}
		}
		
		this.fuelLevel = fuelLevel;
	}
	
	private void burnFuel()
	{
		int maxBurn = fuelLevel < 4 ? fuelLevel : 4;
		
		setFuelLevel(fuelLevel - maxBurn);
		energyStorage.modifyEnergyStored(maxBurn * GasesFramework.configurations.gasDynamo_energyPerFuel);
	}
	
	public void updateEntity()
	{
		if(worldObj.isRemote) return;
		
		burnFuel();
		
		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity tileEntity = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if(tileEntity != null && tileEntity instanceof IEnergyReceiver)
			{
				IEnergyReceiver energyReceiver = (IEnergyReceiver)tileEntity;
				
				energyStorage.modifyEnergyStored(-energyReceiver.receiveEnergy(direction, Math.min(energyStorage.getMaxExtract(), energyStorage.getEnergyStored()), false));
			}
		}
		
		updateEnergyState();
	}
	
	public boolean blockEvent(int eventID, int eventParam)
	{
		switch(eventID)
		{
		case 0:
			isBurning = eventParam == 1;
			break;
		case 1:
			hasEnergy = eventParam == 1;
			break;
		}
		
		if(worldObj != null && worldObj.isRemote)
		{
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