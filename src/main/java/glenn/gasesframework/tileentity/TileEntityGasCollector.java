package glenn.gasesframework.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.block.BlockGas;
import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyPair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityGasCollector extends TileEntityPump
{
	private GasType pendingGasType;
	private int pendingGasAmount;
	private int collectionTime;

    @Override
	protected boolean extractFromSides()
    {
		return true;
    }

    @Override
	protected void handleFailedPumpings()
    {
		
    }
    
    private void getBranching(KeyPair<Integer, IVec> pos, Queue<KeyPair<Integer, IVec>> queue, HashSet<IVec> checked)
    {
    	for(int side = 0; side < 6; side++)
		{
			int xDirection = pos.second.x + (side == 4 ? 1 : (side == 5 ? -1 : 0));
	    	int yDirection = pos.second.y + (side == 0 ? 1 : (side == 1 ? -1 : 0));
	    	int zDirection = pos.second.z + (side == 2 ? 1 : (side == 3 ? -1 : 0));

	    	KeyPair<Integer, IVec> branchPos = new KeyPair<Integer, IVec>(pos.first + 1, new IVec(xDirection, yDirection, zDirection));
			Block posBlock = worldObj.getBlock(xDirection, yDirection, zDirection);
	    	if(!posBlock.isOpaqueCube() && !checked.contains(branchPos))
    		{
	    		queue.add(branchPos);
    		}
	    	
	    	checked.add(branchPos.second);
		}
    }

    @Override
	public void updateEntity()
    {
		if(!worldObj.isRemote && collectionTime-- <= 0)
		{
			if(!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) & (containedType == null | containedType == GasesFrameworkAPI.gasTypeAir))
			{
				if(pendingGasAmount < 16)
				{
					Queue<KeyPair<Integer, IVec>> queue = new LinkedList<KeyPair<Integer, IVec>>();
					HashSet<IVec> checked = new HashSet<IVec>();
					KeyPair<Integer, IVec> center = new KeyPair<Integer, IVec>(0, new IVec(xCoord, yCoord, zCoord));
					checked.add(center.second);
					
					getBranching(center, queue, checked);
					
					while(!queue.isEmpty() && pendingGasAmount < 16)
			    	{
						KeyPair<Integer, IVec> pos = queue.remove();
						
						Block posBlock = worldObj.getBlock(pos.second.x, pos.second.y, pos.second.z);
						if(posBlock instanceof BlockGas)
						{
							BlockGas gasBlock = (BlockGas)posBlock;
							if(gasBlock.type.isIndustrial && acceptsType(gasBlock.type))
							{
								if(gasBlock.type != pendingGasType)
								{
									pendingGasAmount = 0;
								}
								
								pendingGasAmount += 16 - worldObj.getBlockMetadata(pos.second.x, pos.second.y, pos.second.z);
								pendingGasType = gasBlock.type;
								
								worldObj.setBlockToAir(pos.second.x, pos.second.y, pos.second.z);
							}
						}
						
						if(pos.first <= 4)
						{
							getBranching(pos, queue, checked);
						}
			    	}
				}
				
				if(pendingGasAmount >= 16)
				{
					pendingGasAmount -= 16;
					containedType = pendingGasType;
				}
			}
			
			collectionTime = 5;
		}
		
		
		super.updateEntity();
    }

    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("pendingGasType", pendingGasType != null ? pendingGasType.gasID : -1);
		par1NBTTagCompound.setInteger("pendingGasAmount", pendingGasAmount);
		par1NBTTagCompound.setInteger("collectionTime", collectionTime);
	}

    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		pendingGasAmount = par1NBTTagCompound.getInteger("pendingGasType");
		collectionTime = par1NBTTagCompound.getInteger("collectionTime");
		pendingGasType = GasType.getGasTypeByID(par1NBTTagCompound.getInteger("pendingGasType"));
	}
}