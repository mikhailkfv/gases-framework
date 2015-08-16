package glenn.gasesframework.common.item;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterSingleExcluding;
import net.minecraft.item.ItemStack;

public class ItemGasSamplerExcluding extends ItemGasSampler
{
	@Override
	public GasTypeFilter getFilter(ItemStack itemstack)
	{
		return new GasTypeFilterSingleExcluding(getGasType(itemstack));
	}
}
