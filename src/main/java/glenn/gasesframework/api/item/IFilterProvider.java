package glenn.gasesframework.api.item;

import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.filter.GasTypeFilter;

/**
 * An interface for items that can provide filters for {@link glenn.gasesframework.api.block.IGasFilter IGasFilters}.
 * @author Erlend
 *
 */
public interface IFilterProvider
{
	GasTypeFilter getFilter(ItemStack itemstack);
}
