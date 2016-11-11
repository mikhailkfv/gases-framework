package glenn.moddingutils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemRepresentation
{
	public String name;
	public Integer metadata;
	public int amount = 1;

	public ItemStack getRealItemStack()
	{
		if (metadata != null)
		{
			return new ItemStack((Item) Item.itemRegistry.getObject(name), amount == 0 ? 1 : amount, metadata);
		}
		else
		{
			return new ItemStack((Item) Item.itemRegistry.getObject(name), amount == 0 ? 1 : amount, 0);
		}
	}

	public ItemStack getMatcherItemStack()
	{
		if (metadata != null)
		{
			return new ItemStack((Item) Item.itemRegistry.getObject(name), amount == 0 ? 1 : amount, metadata);
		}
		else
		{
			return new ItemStack((Item) Item.itemRegistry.getObject(name), amount == 0 ? 1 : amount, OreDictionary.WILDCARD_VALUE);
		}
	}
}