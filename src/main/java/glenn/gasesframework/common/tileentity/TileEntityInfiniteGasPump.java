package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityInfiniteGasPump extends TileEntity
{
	private final int pumpRate;
	
	private int pumpTime;
	private GasType[] types = new GasType[6];
	
	public TileEntityInfiniteGasPump()
	{
		pumpRate = GasesFramework.configurations.piping.infiniteMaterial.pumpRate;
		pumpTime = pumpRate;
		for(int i = 0; i < types.length; i++)
		{
			types[i] = GasesFrameworkAPI.gasTypeAir;
		}
	}
	
	public GasType getType(ForgeDirection side)
	{
		GasType type = types[side.ordinal()];
		return type;
	}
	
	public void setType(GasType newType, ForgeDirection side)
	{
		int ordinal = side.ordinal();
		if(newType == types[ordinal]) newType = GasesFrameworkAPI.gasTypeAir;
		
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), ordinal, GasType.getGasID(newType));
		types[ordinal] = newType;
	}
	
	public boolean isActive()
	{
		return !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
    /**
     * Pump to a specified block. If the contained gas type is consumed, return true.
     * @param x
     * @param y
     * @param z
     * @param direction
     * @return
     */
    protected boolean pumpToBlock(int x, int y, int z, GasType type, ForgeDirection direction)
    {
    	IGasPropellor propellor = (IGasPropellor)getBlockType();
    	int pressure = propellor.getPressureFromSide(worldObj, xCoord, yCoord, zCoord, direction);
    	return GasesFrameworkAPI.pushGas(worldObj, worldObj.rand, x, y, z, type, direction, pressure);
    }
    
	@Override
	public void updateEntity()
	{
		if(!worldObj.isRemote && pumpTime-- <= 0)
		{
			if(isActive())
			{
				for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
				{
					GasType type = getType(side);
	
					int x = xCoord + side.offsetX;
					int y = yCoord + side.offsetY;
					int z = zCoord + side.offsetZ;
					
					pumpToBlock(x, y, z, type, side);
				}
			}
			
			pumpTime = pumpRate;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		pumpTime = tagCompound.getInteger("pumpTime");
		int[] gasIDArray = tagCompound.getIntArray("types");
		
		for(int i = 0; i < types.length; i++)
		{
			types[i] = GasType.getGasTypeByID(gasIDArray[i]);
			if(types[i] == null) types[i] = GasesFrameworkAPI.gasTypeAir; 
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("pumpTime", pumpTime);
		int[] gasIDArray = new int[6];
		for(int i = 0; i < types.length; i++)
		{
			GasType gasType = types[i];
			gasIDArray[i] = GasType.getGasID(gasType);
		}
		tagCompound.setIntArray("types", gasIDArray);
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }
	
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
    	readFromNBT(packet.func_148857_g());
    }
	
	public boolean blockEvent(int eventID, int eventParam)
	{
		if(worldObj.isRemote)
		{
			types[eventID] = GasType.getGasTypeByID(eventParam);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		return true;
	}
}
