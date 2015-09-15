package glenn.gasesframework.common.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGasContainer extends Item
{
	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List itemList)
	{
		GasType[] allTypes = GasesFramework.registry.getRegisteredGasTypes();
		for (GasType type : allTypes)
		{
			if(type.creativeTab == creativeTabs && type.isIndustrial)
			{
				itemList.add(new ItemStack(item, 1, type.gasID));
			}
		}
	}

	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		return CreativeTabs.creativeTabArray;
	}
}
