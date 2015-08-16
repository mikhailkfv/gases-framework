package glenn.gasesframework.common.item;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterSingleIncluding;
import net.minecraft.item.ItemStack;

public class ItemGasSamplerIncluding extends ItemGasSampler
{
	@Override
	public GasTypeFilter getFilter(ItemStack itemstack)
	{
		return new GasTypeFilterSingleIncluding(getGasType(itemstack));
	}
}
