package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasSource;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.block.BlockGas;
import glenn.gasesframework.common.block.BlockGasPump;
import glenn.moddingutils.DVec;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGasPump extends TileEntity
{
	public static final int PUMP_EVENT = 0;
	public static final int SET_FILTER_EVENT = 1;
	public static final int FAILED_PUMP = 0;
	public static final int SUCCESSFUL_PUMP = 1;
	
	public int pumpTime;
	public boolean excludes;
	public GasType containedType;
	public GasType filterType;
	
	public TileEntityGasPump()
	{
		pumpTime = 25;
		excludes = false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		pumpTime = tagCompound.getInteger("pumpTime");
		excludes = tagCompound.getBoolean("excludes");
		containedType = GasType.getGasTypeByID(tagCompound.getInteger("containedType"));
		filterType = GasType.getGasTypeByID(tagCompound.getInteger("filterType"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("pumpTime", pumpTime);
		tagCompound.setBoolean("excludes", excludes);
		tagCompound.setInteger("containedType", containedType != null ? containedType.gasID : -1);
		tagCompound.setInteger("filterType", filterType != null ? filterType.gasID : -1);
	}
	
	/**
     * Overriden in a sign to provide the text.
     */
	@Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }
	
	/**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
    	readFromNBT(packet.func_148857_g());
    }
	
	public void setFilter(GasType filterType, boolean excludes)
	{
		if(this.excludes == excludes && this.filterType == filterType)
		{
			this.excludes = false;
			this.filterType = null;
		}
		else
		{
			this.excludes = excludes;
			this.filterType = filterType;
		}
		
		int eventParam = filterType.gasID & 0x7fffffff;
		if(excludes) eventParam |= 0x80000000;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), SET_FILTER_EVENT, eventParam);
	}
    
	/**
	 * Is this type accepted by the pump?
	 * @param gasType
	 * @return
	 */
    public boolean acceptsType(GasType gasType)
    {
    	if(filterType == null)
    	{
    		return true;
    	}
    	else
    	{
    		return (filterType == gasType ^ excludes) | gasType == GasesFrameworkAPI.gasTypeAir;
    	}
    }
    
    /**
     * If true, the contents of the pump can be replaced by something else.
     * @return
     */
    protected boolean canFill()
    {
    	return containedType == null || containedType == GasesFrameworkAPI.gasTypeAir;
    }
	
    /**
     * Attempt to extract gas from surrounding blocks or whatever the subtype may do.
     * @return
     */
    protected GasType extract()
    {
		ForgeDirection blockDirection = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    	int[] indices = randomIndexArray(this.worldObj.rand);
		for(int i = 0; i < 6 & containedType == null; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(indices[i]);
			if(direction == blockDirection)
			{
				continue;
			}
			
			int x1 = xCoord + direction.offsetX;
			int y1 = yCoord + direction.offsetY;
			int z1 = zCoord + direction.offsetZ;
			
			Block directionBlock = worldObj.getBlock(x1, y1, z1);
			
			if(directionBlock instanceof IGasSource)
			{
				IGasSource gasSource = (IGasSource)directionBlock;
				if(acceptsType(gasSource.getGasTypeFromSide(worldObj, x1, y1, z1, direction.getOpposite())))
				{
					return gasSource.takeGasTypeFromSide(worldObj, x1, y1, z1, direction.getOpposite());
				}
			}
		}
		
		return GasesFrameworkAPI.gasTypeAir;
    }
    
    /**
     * Pump to a specified block. If the contained gas type is consumed, return true.
     * @param x
     * @param y
     * @param z
     * @param direction
     * @return
     */
    protected boolean pumpToBlock(int x, int y, int z, ForgeDirection direction)
    {
    	Block block = worldObj.getBlock(x, y, z);
    	if(block instanceof IGasReceptor)
		{
			return ((IGasReceptor)block).receiveGas(worldObj, x, y, z, direction.getOpposite(), containedType);
		}
		else
		{
			return GasesFrameworkAPI.fillWithGas(worldObj, worldObj.rand, x, y, z, containedType);
		}
    }
    
    /**
     * Attempt to pump the contained gas type to the specified direction. Returns whether or not it worked.
     * @return
     */
    protected boolean pump()
    {
		if(containedType != null)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
			if(pumpToBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, direction))
			{
				containedType = null;
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
    
    /**
     * Called on average every 25th tick.
     */
    protected void tick()
    {
    	if(canFill())
		{
			containedType = extract();
		}
		
		if(pump())
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), PUMP_EVENT, SUCCESSFUL_PUMP);
		}
		else
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), PUMP_EVENT, FAILED_PUMP);
		}
    }

	@Override
	public void updateEntity()
    {
		if(!worldObj.isRemote && pumpTime-- <= 0)
		{
			if(!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			{
				tick();
			}
			
			pumpTime = 25;
		}
    }
	
	protected int[] randomIndexArray(Random random)
	{
		int[] array = new int[6];
		
		for(int i = 0; i < 6; i++)
    	{
    		while(true)
    		{
    			int index = random.nextInt(6);
    			if(array[index] == 0)
    			{
    				array[index] = i;
    				break;
    			}
    		}
    	}
		
		return array;
	}
	
	public boolean blockEvent(int eventID, int eventParam)
	{
		if(worldObj.isRemote)
		{
			switch(eventID)
			{
			case PUMP_EVENT:
				if(eventParam == SUCCESSFUL_PUMP)
				{
					
				}
				else if(eventParam == FAILED_PUMP)
				{
					
				}
				break;
			case SET_FILTER_EVENT:
				excludes = (eventParam & 0x80000000) != 0;
				filterType = GasType.getGasTypeByID(eventParam & 0x7fffffff);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				break;
			}
		}
		
		return true;
	}
}