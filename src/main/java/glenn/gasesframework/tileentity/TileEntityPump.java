package glenn.gasesframework.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasSource;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.block.BlockGas;
import glenn.gasesframework.block.BlockGasPump;
import glenn.moddingutils.DVec;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPump extends TileEntity
{
	protected static final int[] xDirection = new int[]{
		0, 0, 1, -1, 0, 0
	};
	protected static final int[] yDirection = new int[]{
		1, -1, 0, 0, 0, 0
	};
	protected static final int[] zDirection = new int[]{
		0, 0, 0, 0, 1, -1
	};
	
	public int pumpTime;
	private int overload;
	private int failedPumpings;
	public boolean excludes;
	public GasType containedType;
	public GasType filterType;
	
	public TileEntityPump()
	{
		pumpTime = 25;
		overload = 0;
		excludes = false;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		pumpTime = par1NBTTagCompound.getInteger("pumpTime");
		failedPumpings = par1NBTTagCompound.getInteger("failedPumpings");
		excludes = par1NBTTagCompound.getBoolean("excludes");
		containedType = GasType.getGasTypeByID(par1NBTTagCompound.getInteger("containedType"));
		filterType = GasType.getGasTypeByID(par1NBTTagCompound.getInteger("filterType"));
		overload = par1NBTTagCompound.getInteger("overload");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("pumpTime", pumpTime);
		par1NBTTagCompound.setInteger("overload", overload);
		par1NBTTagCompound.setInteger("failedPumpings", failedPumpings);
		par1NBTTagCompound.setBoolean("excludes", excludes);
		par1NBTTagCompound.setInteger("containedType", containedType != null ? containedType.gasID : -1);
		par1NBTTagCompound.setInteger("filterType", filterType != null ? filterType.gasID : -1);
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
	
    protected boolean extractFromSides()
    {
		int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    	int[] indices = randomIndexArray(this.worldObj.rand);
		for(int i = 0; i < 6 & containedType == null; i++)
		{
			int index = indices[i];
			if(index == metadata)
			{
				continue;
			}
			
			int x1 = xCoord + xDirection[index];
			int y1 = yCoord + yDirection[index];
			int z1 = zCoord + zDirection[index];
			
			Block directionBlock = worldObj.getBlock(x1, y1, z1);
			
			if(directionBlock != Blocks.air && IGasSource.class.isAssignableFrom(directionBlock.getClass()))
			{
				IGasSource gasSource = (IGasSource)directionBlock;
				if(acceptsType(gasSource.getGasTypeFromSide(worldObj, x1, y1, z1, index)))
				{
					containedType = gasSource.takeGasTypeFromSide(worldObj, x1, y1, z1, index);
				}
				return false;
			}
		}
		
		return true;
    }
    
    protected void handleFailedPumpings()
    {
    	if(worldObj.isRemote && failedPumpings > 20 && worldObj.rand.nextInt(1000 / ((failedPumpings - 20) * (failedPumpings - 20)) + 1) == 0)
		{
			DVec velocity = DVec.randomNormalizedVec(worldObj.rand).scale(0.25D);
			worldObj.spawnParticle("largesmoke", xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, velocity.x, velocity.y, velocity.z);
		}
    	
    	if(worldObj.isRemote && overload > 20 && worldObj.rand.nextInt(2000 / ((overload - 20) * (overload - 20)) + 1) == 0)
		{
			DVec velocity = DVec.randomNormalizedVec(worldObj.rand).scale(0.25D);
			worldObj.spawnParticle("largesmoke", xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, velocity.x, velocity.y, velocity.z);
		}
    }

	@Override
	public void updateEntity()
    {
		if(!worldObj.isRemote && pumpTime-- <= 0)
		{
			if(!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			{
				BlockGasPump block = (BlockGasPump)worldObj.getBlock(xCoord, yCoord, zCoord);
				int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
				boolean canPumpAir = extractFromSides();
				
				if(containedType == null & canPumpAir)
				{
					containedType = GasesFrameworkAPI.gasTypeAir;
				}
				
				if(containedType != null)
				{
					int x1 = xCoord + xDirection[metadata];
					int y1 = yCoord + yDirection[metadata];
					int z1 = zCoord + zDirection[metadata];
					
					Block directionBlock = worldObj.getBlock(x1, y1, z1);
					boolean success = false;
					
					if(IGasReceptor.class.isAssignableFrom(directionBlock.getClass()))
					{
						success = ((IGasReceptor)directionBlock).receiveGas(worldObj, x1, y1, z1, metadata < 2 ? (1 - metadata) : metadata, containedType);
					}
					else if(GasesFrameworkAPI.fillWithGas(worldObj, worldObj.rand, x1, y1, z1, containedType))
					{
						success = true;
					}
					
					if(success)
					{
						containedType = null;
						failedPumpings = 0;
						overload += 10;
						worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 0, 1);
					}
					else
					{
						failedPumpings++;
						pumpTime += 2;
						worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 0, 0);
					}
				}
			}
			else
			{
				failedPumpings = 0;
			}
			
			
			pumpTime = 25;
		}
		
		if(overload-- < 0)
		{
			overload = 0;
		}
		
		handleFailedPumpings();
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
		if(!worldObj.isRemote) return true;
		
		switch(eventID)
		{
		case 0:
			if(eventParam == 1)
			{
				failedPumpings = 0;
				overload += 10;
			}
			else if(eventParam == 0)
			{
				failedPumpings++;
			}
			break;
		}
		return true;
	}
}