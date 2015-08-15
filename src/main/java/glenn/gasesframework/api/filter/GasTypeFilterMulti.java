package glenn.gasesframework.api.filter;

import glenn.gasesframework.api.gastype.GasType;

import java.util.HashSet;

import net.minecraft.nbt.NBTTagCompound;

public abstract class GasTypeFilterMulti extends GasTypeFilter
{
	public HashSet<GasType> filterTypes = new HashSet<GasType>();
	
	public GasTypeFilterMulti(GasType[] filterTypes)
	{
		for (int i = 0; i < filterTypes.length; i++)
		{
			this.filterTypes.add(filterTypes[i]);
		}
	}
	
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);

		int[] gasTypes = new int[filterTypes.size()];
		int i = 0;
		for (GasType filterType : filterTypes)
		{
			gasTypes[i++] = GasType.getGasID(filterType);
		}
		tagCompound.setIntArray("gasTypes", gasTypes);
	}

	public static GasTypeFilterMulti readFromNBT(NBTTagCompound tagCompound)
	{
		if (tagCompound != null && tagCompound.hasKey("type"))
		{
			GasType[] filterTypes = new GasType[0];
			if (tagCompound.hasKey("gasTypes"))
			{
				int[] gasTypes = tagCompound.getIntArray("gasTypes");
				filterTypes = new GasType[gasTypes.length];
				for (int i = 0; i < gasTypes.length; i++)
				{
					filterTypes[i] = GasType.getGasTypeByID(gasTypes[i]);
				}
			}

			if (filterTypes.length > 0)
			{
				switch (tagCompound.getByte("type"))
				{
				case GasTypeFilterMultiIncluding.TYPE:
					return new GasTypeFilterMultiIncluding(filterTypes);
				case GasTypeFilterMultiExcluding.TYPE:
					return new GasTypeFilterMultiExcluding(filterTypes);
			}
			}
		}
		return null;
	}
}
