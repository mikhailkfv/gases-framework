package glenn.gasesframework.api.item;

import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.gastype.GasType;

public interface ISampler
{
	public GasType getGasType(ItemStack itemstack);
	
	public ItemStack setGasType(ItemStack itemstack, GasType gasType);
}
