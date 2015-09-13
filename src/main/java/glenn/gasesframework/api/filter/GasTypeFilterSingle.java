package glenn.gasesframework.api.filter;

import glenn.gasesframework.GasesFramework;
import net.minecraft.nbt.NBTTagCompound;
import glenn.gasesframework.api.gastype.GasType;

/**
 * Abstract filter involving one gas type, be it including or excluding.
 * @author Erlend
 *
 */
public abstract class GasTypeFilterSingle extends GasTypeFilterSimple
{
	protected final GasType filterType;
	
	public GasTypeFilterSingle(GasType filterType)
	{
		this.filterType = filterType;
	}
	
	/**
	 * Get the type used by this filter, be it excluding or including.
	 * @return
	 */
	public GasType getFilterType()
	{
		return filterType;
	}
	
	/**
	 * Get a {@link glenn.gasesframework.api.filter.GasTypeFilterMulti GasTypeFilterMulti} variant of this filter
	 * @return
	 */
	public abstract GasTypeFilterMulti toMulti();
	
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("gasType", GasType.getGasID(filterType));
	}
	
	public static GasTypeFilterSingle fromNBTTagCompound(NBTTagCompound tagCompound)
	{
		if (tagCompound != null && tagCompound.hasKey("type"))
		{
			GasType filterType = null;
			if (tagCompound.hasKey("gasType"))
			{
				filterType = GasesFramework.registry.getGasTypeByID(tagCompound.getInteger("gasType"));
			}

			switch (tagCompound.getByte("type"))
			{
			case GasTypeFilterSingleIncluding.TYPE:
				return new GasTypeFilterSingleIncluding(filterType);
			case GasTypeFilterSingleExcluding.TYPE:
				return new GasTypeFilterSingleExcluding(filterType);
			}
		}
		return null;
	}
}
