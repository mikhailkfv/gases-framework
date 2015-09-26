package glenn.gasesframework.api.filter;

import net.minecraft.nbt.NBTTagCompound;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;

/**
 * A module for gas type filtering. It has several implementations with their own rules.
 * All filters will accept air or null.
 * All filters are immutable.
 * All filters are NBT compatible.
 */
public abstract class GasTypeFilter
{
	/**
	 * Get the ID of this filter type.
	 * @return The ID of this filter type
	 */
	public abstract byte getType();

	/**
	 * Apply a gas type to the filter.
	 * @param gasType The gas type
	 * @return True if the gas type filter accepts the gas type
	 */
	public boolean accept(GasType gasType)
	{
		return gasType == null || gasType == GasesFrameworkAPI.gasTypeAir;
	}

	/**
	 * Write the filter to NBT.
	 * @param tagCompound The tag compound
	 */
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		tagCompound.setByte("type", getType());
	}

	/**
	 * Read any kind of gas type filter from NBT.
	 * @param tagCompound The tag compound
	 * @return The gas type filter, or an open filter if the tag compound is invalid
	 */
	public static GasTypeFilter readFromNBT(NBTTagCompound tagCompound)
	{
		GasTypeFilter result = null;
		if (tagCompound != null && tagCompound.hasKey("type"))
		{
			switch (tagCompound.getByte("type"))
			{
			case GasTypeFilterOpen.TYPE:
			case GasTypeFilterClosed.TYPE:
			case GasTypeFilterSingleIncluding.TYPE:
			case GasTypeFilterSingleExcluding.TYPE:
				result = GasTypeFilterSimple.readFromNBT(tagCompound);
				break;
			case GasTypeFilterMultiIncluding.TYPE:
			case GasTypeFilterMultiExcluding.TYPE:
				result = GasTypeFilterMulti.readFromNBT(tagCompound);
				break;
			}
		}
		
		return result != null ? result : new GasTypeFilterOpen();
	}
}
