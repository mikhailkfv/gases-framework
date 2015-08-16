package glenn.gasesframework.api.filter;

import net.minecraft.nbt.NBTTagCompound;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

/**
 * A module for gas type filtering. It has several implementations with their own rules.
 * All filters will accept air or null.
 * All filters are immutable.
 * All filters are NBT compatible.
 * @author Erlend
 *
 */
public abstract class GasTypeFilter
{
	public abstract byte getType();
	
	public  boolean accept(GasType gasType)
	{
		return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir;
	}
	
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
