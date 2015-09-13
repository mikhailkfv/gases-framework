package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTab extends CreativeTabs
{
	public CreativeTab(String label)
	{
		super(label);
	}

	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(GasesFramework.registry.getLanternBlock(GasesFramework.lanternTypeEmpty));
	}
}
