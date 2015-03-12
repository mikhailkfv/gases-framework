package glenn.gasesframework.tileentity;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityInfiniteGas extends TileEntity
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
	
	private GasType type;
	private boolean directional;
	
	public void setType(GasType newType)
	{
		type = newType;
	}
	
	public void setDirectional(boolean directional)
	{
		this.directional = directional;
	}
	
	@Override
	public void updateEntity()
	{
		if(type != null)
		{
			if(directional)
			{
				int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
				System.out.println(metadata);
				int x1 = xCoord + xDirection[metadata];
				int y1 = yCoord + yDirection[metadata];
				int z1 = zCoord + zDirection[metadata];
				
				if(worldObj.isAirBlock(x1, y1, z1))
				{
					worldObj.setBlock(x1, y1, z1, type.block);
				}
			}
			else
			{
				for(int x1 = xCoord-1; x1 <= xCoord+1; x1++)
				{
			    	for(int y1 = yCoord-1; y1 <= yCoord+1; y1++)
			    	{
			        	for(int z1 = zCoord-1; z1 <= zCoord+1; z1++)
			        	{
			        		if(worldObj.isAirBlock(x1, y1, z1))
			        		{
			        			worldObj.setBlock(x1, y1, z1, type.block);
			        		}
			        	}
			    	}
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		type = GasType.getGasTypeByID(par1NBTTagCompound.getInteger("gasType"));
		directional = par1NBTTagCompound.getBoolean("directional");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("gasType", type.gasID);
		par1NBTTagCompound.setBoolean("directional", directional);
	}
}
