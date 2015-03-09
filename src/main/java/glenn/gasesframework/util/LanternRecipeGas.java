package glenn.gasesframework.util;

import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.type.GasType;
import glenn.gasesframework.block.BlockLantern;
import glenn.gasesframework.item.ItemGasBottle;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LanternRecipeGas extends LanternRecipe
{
	public boolean equals(ItemStack itemStack)
	{
		Item item = itemStack.getItem();
		
		return item != null && item instanceof ItemGasBottle;
	}
	
	public BlockLantern getLantern(ItemStack itemstack)
	{
		return (BlockLantern)GasType.getGasTypeByID(itemstack.getItemDamage()).combustibility.lanternBlock;
	}
}