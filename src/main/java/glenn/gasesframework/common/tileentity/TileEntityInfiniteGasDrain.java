package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasSource;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityInfiniteGasDrain extends TileEntity
{
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
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
	
	public boolean isActive()
	{
		return !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void updateEntity()
    {
		if(!worldObj.isRemote && isActive())
		{
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				int x = xCoord + side.offsetX;
				int y = yCoord + side.offsetY;
				int z = zCoord + side.offsetZ;
				
				if(GasesFrameworkAPI.getGasType(worldObj, x, y, z) != null)
				{
					worldObj.setBlockToAir(x, y, z);
				}
				else
				{
					Block block = worldObj.getBlock(x, y, z);
					
					if(block instanceof IGasSource)
					{
						((IGasSource)block).takeGasTypeFromSide(worldObj, x, y, z, side.getOpposite());
					}
				}
			}
		}
    }
}