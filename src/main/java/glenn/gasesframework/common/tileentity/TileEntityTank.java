package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.block.BlockGasTank;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTank extends TileEntity
{
	public static final int SET_AMOUNT = 0;
	public static final int SET_TYPE = 1;
	
	public GasType containedType;
	public int amount;
	
	public double[][] ps;
	public double[][] vs;
	
	private static Random rand = new Random();
	
	public TileEntityTank()
	{
		amount = 0;
		
		if(GasesFramework.configurations.other_fancyTank)
		{
			ps = new double[9][9];
			vs = new double[9][9];
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		amount = par1NBTTagCompound.getInteger("amount");
		containedType = GasType.getGasTypeByID(par1NBTTagCompound.getInteger("containedType"));
		
		if(GasesFramework.configurations.other_fancyTank)
		{
			double gasHeight = getGasCap() == 0 ? 0.0D : (double)amount / getGasCap();
			for(double[] fs : ps)
			{
				Arrays.fill(fs, gasHeight);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("amount", amount);
		par1NBTTagCompound.setInteger("containedType", containedType != null ? containedType.gasID : -1);
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
	
	private double get(int x, int y)
	{
		if(x < 0) x = 0;
		else if(x > 8) x = 8;
		if(y < 0) y = 0;
		else if(y > 8) y = 8;
		return ps[x][y];
	}
	
	private void wobble(boolean up)
	{
		if(!GasesFramework.configurations.other_fancyTank)
		{
			return;
		}
		
		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				vs[i][j] += (up ? -0.005D : 0.005D) * (1.0D - rand.nextDouble() * 3.0D);
			}
		}
	}

	@Override
	public void updateEntity()
    {
		if(!worldObj.isRemote)
		{
			if(amount > 0)
			{
				TileEntity belowTileEntity = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
				if(belowTileEntity != null && belowTileEntity instanceof TileEntityTank)
				{
					TileEntityTank tankEntity = (TileEntityTank)belowTileEntity;
					if(!tankEntity.isFull() && tankEntity.increment(containedType)) decrement();
				}
			}
			
			return;
		}
		
		if(worldObj.isRemote && GasesFramework.configurations.other_fancyTank)
		{
			double gasHeight = getGasCap() == 0 ? 0.0D : (double)amount / getGasCap();
			for(int i = 0; i < 9; i++)
			{
				for(int j = 0; j < 9; j++)
				{
					vs[i][j] = vs[i][j];
					vs[i][j] += (gasHeight - ps[i][j]) * 0.4D;
					vs[i][j] += (get(i - 1, j) + get(i, j + 1) + get(i + 1, j) + get(i, j - 1) - ps[i][j] * 4) * 0.05D;
					vs[i][j] += (get(i - 1, j) + get(i, j + 1) + get(i + 1, j) + get(i, j- 1) - ps[i][j] * 4) * 0.035355D;
					vs[i][j] *= 0.9D;
				}
			}
			
			for(int i = 0; i < 9; i++)
			{
				for(int j = 0; j < 9; j++)
				{
					ps[i][j] += vs[i][j];
					
					if(ps[i][j] < 0.0D)
					{
						ps[i][j] = 0.0D;
						vs[i][j] = -vs[i][j];
					}
					else if(ps[i][j] > 1.0D)
					{
						ps[i][j] = 1.0D;
						vs[i][j] = -vs[i][j];
					}
				}
			}
		}
    }
	
	public int getGasCap()
	{
		if(containedType == null)
		{
			return 0;
		}
		else
		{
			return (64 - containedType.density) * 2 + 16;
		}
	}
	
	public boolean isFull()
	{
		if(containedType == null) return false;
		else return amount >= getGasCap();
	}
	
	public boolean canIncrement(GasType gasType)
	{
		GasType prevGasType = containedType;
		
		if(gasType == GasesFrameworkAPI.gasTypeAir)
		{
			return true;
		}
		else if(containedType == null | containedType == gasType)
		{
			containedType = gasType;
			if(amount + 1 <= getGasCap())
			{
				return true;
			}
			else
			{
				TileEntity aboveTileEntity = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
				return aboveTileEntity != null && aboveTileEntity instanceof TileEntityTank && ((TileEntityTank)aboveTileEntity).canIncrement(gasType);
			}
		}
		else
		{
			return false;
		}
	}
	
	public boolean increment(GasType gasType)
	{
		GasType prevGasType = containedType;
		
		if(gasType == GasesFrameworkAPI.gasTypeAir)
		{
			return true;
		}
		else if(containedType == null | containedType == gasType)
		{
			containedType = gasType;
			if(++amount <= getGasCap())
			{
				if(!worldObj.isRemote)
				{
					worldObj.addBlockEvent(xCoord, yCoord, zCoord, GasesFramework.gasTank, SET_AMOUNT, amount);
					worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), SET_TYPE, containedType == null ? -1 : containedType.gasID);
				}
				return true;
			}
			else
			{
				amount = getGasCap();
				
				TileEntity aboveTileEntity = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
				return aboveTileEntity != null && aboveTileEntity instanceof TileEntityTank && ((TileEntityTank)aboveTileEntity).increment(gasType);
			}
		}
		else
		{
			return false;
		}
	}
	
	public boolean decrement()
	{
		if(worldObj.isRemote) return amount > 0;
		
		GasType prevGasType = containedType;
		
		if(amount-- > 0)
		{
			if(amount == 0)
			{
				containedType = null;
			}
			
			if(!worldObj.isRemote)
			{
				worldObj.addBlockEvent(xCoord, yCoord, zCoord, GasesFramework.gasTank, SET_AMOUNT, amount);
				worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), SET_TYPE, containedType == null ? -1 : containedType.gasID);
			}
			
			return true;
		}
		else
		{
			amount = 0;
			return false;
		}
	}
	
	public boolean blockEvent(int eventID, int eventParam)
	{
		if(!worldObj.isRemote) return true;
		
		switch(eventID)
		{
		case SET_AMOUNT:
			if(amount < eventParam) wobble(true);
			else if(amount > eventParam) wobble(false);
			amount = eventParam;
			break;
		case SET_TYPE:
			containedType = GasType.getGasTypeByID(eventParam);
			break;
		}
		return true;
	}
}