package glenn.gasesframework.api.filter;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Abstract filter involving one or less gas types.
 */
public abstract class GasTypeFilterSimple extends GasTypeFilter
{
	/**
	 * Is this filter equal to the other filter?
	 * 
	 * @param other
	 *            The other filter
	 * @return True if the filters are equal
	 */
	public abstract boolean equals(GasTypeFilterSimple other);

	/**
	 * Read any kind of simple gas type filter from NBT.
	 * 
	 * @param tagCompound
	 *            The tag compound
	 * @return The gas type filter, or null if the tag compound is invalid
	 */
	public static GasTypeFilterSimple readFromNBT(NBTTagCompound tagCompound)
	{
		if (tagCompound != null && tagCompound.hasKey("type"))
		{
			switch (tagCompound.getByte("type"))
			{
				case GasTypeFilterOpen.TYPE:
					return new GasTypeFilterOpen();
				case GasTypeFilterClosed.TYPE:
					return new GasTypeFilterClosed();
				case GasTypeFilterSingleIncluding.TYPE:
				case GasTypeFilterSingleExcluding.TYPE:
					return GasTypeFilterSingle.fromNBTTagCompound(tagCompound);
			}
		}

		return null;
	}
}
