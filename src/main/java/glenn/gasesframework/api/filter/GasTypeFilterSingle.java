package glenn.gasesframework.api.filter;

import net.minecraft.nbt.NBTTagCompound;
import glenn.gasesframework.api.gastype.GasType;

public abstract class GasTypeFilterSingle extends GasTypeFilterSimple
{
	public GasType filterType;
	
	public GasTypeFilterSingle(GasType filterType)
	{
		this.filterType = filterType;
	}
	
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
				filterType = GasType.getGasTypeByID(tagCompound.getInteger("gasType"));
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
