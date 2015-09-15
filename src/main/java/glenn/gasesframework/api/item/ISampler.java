package glenn.gasesframework.api.item;

import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.gastype.GasType;

/**
 * An interface for items that contain a gas type, like samplers.
 * @author Erlend
 */
public interface ISampler
{
	GasType getGasType(ItemStack itemstack);
	
	ItemStack setGasType(ItemStack itemstack, GasType gasType);
}
