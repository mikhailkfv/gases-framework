package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.block.BlockGas;
import glenn.gasesframework.common.block.BlockGasCollector;
import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyPair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGasCollector extends TileEntityDirectionalGasPropellor
{
	private final int collectionRange;
	
	private GasType pendingGasType;
	private int pendingGasAmount;
	
	public TileEntityGasCollector(int pumpRate, int collectionRange)
	{
		super(pumpRate);
		this.collectionRange = collectionRange;
		
		pendingGasType = null;
		pendingGasAmount = 0;
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
    protected GasType extract()
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
				
				if(pos.first <= collectionRange)
				{
					getBranching(pos, queue, checked);
				}
	    	}
		}
    	
    	if(pendingGasAmount >= 16)
		{
			GasType temp = pendingGasType;
			if((pendingGasAmount -= 16) == 0)
			{
				pendingGasType = null;
			}
			return temp;
		}
    	else
    	{
    		return super.extract();
    	}
    }
    
    @Override
    protected boolean pumpToBlock(int x, int y, int z, ForgeDirection direction)
    {
    	boolean result = super.pumpToBlock(x, y, z, direction);
    	
    	if(pendingGasType != null)
    	{
	    	Block block = worldObj.getBlock(x, y, z);
	    	if(block instanceof BlockGasCollector)
	    	{
	    		TileEntityGasCollector tileEntity = (TileEntityGasCollector)worldObj.getTileEntity(x, y, z);
	    		
	    		if(tileEntity.acceptsType(pendingGasType) && (tileEntity.pendingGasType == null || tileEntity.pendingGasType == pendingGasType))
	    		{
	    			tileEntity.pendingGasAmount += pendingGasAmount;
	    			tileEntity.pendingGasType = pendingGasType;
	    			pendingGasAmount = 0;
	    			pendingGasType = null;
	    		}
	    	}
    	}
    	
    	return result;
    }

    @Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("pendingGasType", GasType.getGasID(pendingGasType));
		tagCompound.setInteger("pendingGasAmount", pendingGasAmount);
	}

    @Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		pendingGasType = GasesFramework.registry.getGasTypeByID(tagCompound.getInteger("pendingGasType"));
		pendingGasAmount = tagCompound.getInteger("pendingGasAmount");
	}
}