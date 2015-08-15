package glenn.gasesframework.api.filter;

import net.minecraft.nbt.NBTTagCompound;
import glenn.gasesframework.api.gastype.GasType;

public abstract class GasTypeFilter
{
	public abstract byte getType();
	
	public abstract boolean accept(GasType gasType);
	
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setByte("type", getType());
	}
	
	public static GasTypeFilter readFromNBT(NBTTagCompound tagCompound)
	{
		if (tagCompound != null && tagCompound.hasKey("type"))
		{
			switch (tagCompound.getByte("type"))
			{
			case GasTypeFilterOpen.TYPE:
			case GasTypeFilterClosed.TYPE:
			case GasTypeFilterSingleIncluding.TYPE:
			case GasTypeFilterSingleExcluding.TYPE:
				return GasTypeFilterSimple.readFromNBT(tagCompound);
			case GasTypeFilterMultiIncluding.TYPE:
			case GasTypeFilterMultiExcluding.TYPE:
				return GasTypeFilterMulti.readFromNBT(tagCompound);
			}
		}
		
		return new GasTypeFilterOpen();
	}
}
