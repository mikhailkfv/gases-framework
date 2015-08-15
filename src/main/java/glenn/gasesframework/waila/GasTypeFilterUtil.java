package glenn.gasesframework.waila;

import java.util.ArrayList;

import com.google.common.base.Joiner;

import net.minecraft.client.resources.I18n;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterClosed;
import glenn.gasesframework.api.filter.GasTypeFilterMulti;
import glenn.gasesframework.api.filter.GasTypeFilterMultiExcluding;
import glenn.gasesframework.api.filter.GasTypeFilterMultiIncluding;
import glenn.gasesframework.api.filter.GasTypeFilterOpen;
import glenn.gasesframework.api.filter.GasTypeFilterSingle;
import glenn.gasesframework.api.filter.GasTypeFilterSingleExcluding;
import glenn.gasesframework.api.filter.GasTypeFilterSingleIncluding;
import glenn.gasesframework.api.gastype.GasType;

public class GasTypeFilterUtil
{
	public static String getFilterString(GasTypeFilter filter)
	{
		if (filter instanceof GasTypeFilterOpen)
		{
			return I18n.format("filter.open.desc");
		}
		else if (filter instanceof GasTypeFilterClosed)
		{
			return I18n.format("filter.closed.desc");
		}
		else if (filter instanceof GasTypeFilterSingle)
		{
			GasTypeFilterSingle singleFilter = (GasTypeFilterSingle)filter;
			String filterTypeName = singleFilter.filterType != null ? I18n.format(singleFilter.filterType.getUnlocalizedName() + ".name") : "";
			if (singleFilter instanceof GasTypeFilterSingleIncluding)
			{
				return I18n.format("filter.including.desc", filterTypeName);
			}
			else if (singleFilter instanceof GasTypeFilterSingleExcluding)
			{
				return I18n.format("filter.excluding.desc", filterTypeName);
			}
			else
			{
				return null;
			}
		}
		else if (filter instanceof GasTypeFilterMulti)
		{
			GasTypeFilterMulti multiFilter = (GasTypeFilterMulti)filter;
			ArrayList<String> filterTypeNames = new ArrayList<String>();
			for (GasType filterType : multiFilter.filterTypes)
			{
				if (filterType != null)
				{
					filterTypeNames.add(I18n.format(filterType.getUnlocalizedName() + ".name"));
				}
			}
			String filterTypeName = Joiner.on(", ").join(filterTypeNames);

			if (multiFilter instanceof GasTypeFilterMultiIncluding)
			{
				return I18n.format("filter.including.desc", filterTypeName);
			}
			else if (multiFilter instanceof GasTypeFilterMultiExcluding)
			{
				return I18n.format("filter.excluding.desc", filterTypeName);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
