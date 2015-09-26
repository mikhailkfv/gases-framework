package glenn.gasesframework.api.item;

import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.filter.GasTypeFilter;

/**
 * An interface for items that can provide filters for {@link glenn.gasesframework.api.block.IGasTypeFilter IGasTypeFilters}.
 */
public interface IFilterProvider
{
	/**
	 * Get the gas type filter held by the item.
	 * @param itemstack The itemstack
	 * @return The gas type filter held by this item, or null
	 */
	GasTypeFilter getFilter(ItemStack itemstack);
}
