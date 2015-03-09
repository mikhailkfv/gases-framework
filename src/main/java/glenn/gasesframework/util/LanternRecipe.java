package glenn.gasesframework.util;

import glenn.gasesframework.block.BlockLantern;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LanternRecipe
{
	public Item item;
	public int itemDamage;
	protected BlockLantern blockLantern;
	
	public LanternRecipe(ItemStack itemStack, BlockLantern blockLantern)
	{
		this.item = itemStack.getItem();
		this.itemDamage = itemStack.getItemDamage();
		this.blockLantern = blockLantern;
	}
	
	public LanternRecipe()
	{
		
	}
	
	public boolean equals(ItemStack itemStack)
	{
		return itemStack != null && item == itemStack.getItem() & itemStack.getItemDamage() == this.itemDamage;
	}
	
	public BlockLantern getLantern(ItemStack itemStack)
	{
		return blockLantern;
	}
}