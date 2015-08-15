package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasSource;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterOpen;
import glenn.gasesframework.api.filter.GasTypeFilterSimple;
import glenn.gasesframework.api.filter.GasTypeFilterSingleExcluding;
import glenn.gasesframework.api.filter.GasTypeFilterSingleIncluding;
import glenn.gasesframework.api.gastype.GasType;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityDirectionalGasPropellor extends TileEntity
{
	public static final int PUMP_EVENT = 0;
	public static final int FAILED_PUMP = 0;
	public static final int SUCCESSFUL_PUMP = 1;
	
	private final int pumpRate;
	
	public int pumpTime;
	public GasType containedType;
	public GasTypeFilterSimple filter;
	
	public TileEntityDirectionalGasPropellor(int pumpRate)
	{
		this.pumpRate = pumpRate;
		pumpTime = pumpRate;
		filter = new GasTypeFilterOpen();
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		pumpTime = tagCompound.getInteger("pumpTime");
		containedType = GasType.getGasTypeByID(tagCompound.getInteger("containedType"));
		filter = GasTypeFilterSimple.readFromNBT(tagCompound.getCompoundTag("filter"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("pumpTime", pumpTime);
		tagCompound.setInteger("containedType", GasType.getGasID(containedType));
		NBTTagCompound filterTagCompound = new NBTTagCompound();
		filter.writeToNBT(filterTagCompound);
		tagCompound.setTag("filter", filterTagCompound);
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
	
	public GasTypeFilter setFilter(GasTypeFilterSimple filter)
	{
		if (!this.filter.equals(filter))
		{
			this.filter = filter;
		}
		else
		{
			this.filter = new GasTypeFilterOpen();
		}
		
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return this.filter;
	}
    
	/**
	 * Is this type accepted by the pump?
	 * @param gasType
	 * @return
	 */
    public boolean acceptsType(GasType gasType)
    {
    	return filter.accept(gasType);
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
		ForgeDirection blockDirection = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 6);
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
    	IGasPropellor propellor = (IGasPropellor)getBlockType();
    	int pressure = propellor.getPressureFromSide(worldObj, xCoord, yCoord, zCoord, direction);
    	return GasesFrameworkAPI.pushGas(worldObj, worldObj.rand, x, y, z, containedType, direction, pressure);
    }
    
    /**
     * Attempt to pump the contained gas type to the specified direction. Returns whether or not it worked.
     * @return
     */
    protected boolean pump()
    {
		if(containedType != null)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 6);
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
	
	public boolean isActive()
	{
		return !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity()
    {
		if(!worldObj.isRemote && pumpTime-- <= 0)
		{
			if(isActive())
			{
				tick();
			}
			
			pumpTime = pumpRate;
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
			}
		}
		
		return true;
	}
}