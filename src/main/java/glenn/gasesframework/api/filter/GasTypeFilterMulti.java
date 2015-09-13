package glenn.gasesframework.api.filter;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.gastype.GasType;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Abstract filter involving several gas types, be it including or excluding.
 * @author Erlend
 *
 */
public abstract class GasTypeFilterMulti extends GasTypeFilter
{
	protected final Set<GasType> filterTypes;
	
	public GasTypeFilterMulti(GasType[] filterTypes)
	{
		Set<GasType> mutableFilterTypes = Collections.newSetFromMap(new IdentityHashMap<GasType, Boolean>());
		for (int i = 0; i < filterTypes.length; i++)
		{
			mutableFilterTypes.add(filterTypes[i]);
		}
		
		this.filterTypes = Collections.unmodifiableSet(Collections.unmodifiableSet(mutableFilterTypes));
	}
	
	/**
	 * Get an immutable set of gas types used by this filter, be it excluding or including.
	 * @return
	 */
	public Set<GasType> getFilterTypes()
	{
		return filterTypes;
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
					filterTypes[i] = GasesFramework.registry.getGasTypeByID(gasTypes[i]);
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
