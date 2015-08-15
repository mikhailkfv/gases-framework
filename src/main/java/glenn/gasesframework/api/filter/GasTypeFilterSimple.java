package glenn.gasesframework.api.filter;

import net.minecraft.nbt.NBTTagCompound;

public abstract class GasTypeFilterSimple extends GasTypeFilter
{
	public abstract boolean equals(GasTypeFilterSimple other);

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
		
		return new GasTypeFilterOpen();
	}
}
