package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.api.GasesFrameworkAPI;
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
	private int pumpTime;
	private GasType[] types = new GasType[6];
	
	public TileEntityInfiniteGasPump()
	{
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
		
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), ordinal, newType != null ? newType.gasID : -1);
		types[ordinal] = newType;
	}
	
	@Override
	public void updateEntity()
	{
		if(!worldObj.isRemote && pumpTime-- <= 0)
		{
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				GasType type = getType(side);
				
				if(type == null)
				{
					type = GasesFrameworkAPI.gasTypeAir;
				}

				int x = xCoord + side.offsetX;
				int y = yCoord + side.offsetY;
				int z = zCoord + side.offsetZ;
				Block block = worldObj.getBlock(x, y, z);
				
				if(block instanceof IGasReceptor)
				{
					IGasReceptor receptor = (IGasReceptor)block;
					receptor.receiveGas(worldObj, x, y, z, side.getOpposite(), type);
				}
				else
				{
					GasesFrameworkAPI.fillWithGas(worldObj, worldObj.rand, x, y, z, type);
				}
			}
			
			pumpTime = 25;
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
			GasType type = types[i];
			gasIDArray[i] = type.gasID;
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
