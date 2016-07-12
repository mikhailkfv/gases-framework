package glenn.moddingutils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRepresentation
{
	public String name;
	public int metadata = 0;
	public int amount = 1;

	public ItemStack getItemStack()
	{
		return new ItemStack((Item) Item.itemRegistry.getObject(name), amount == 0 ? 1 : amount, metadata);
	}
}