package glenn.gasesframework.api.item;

import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.gastype.GasType;

/**
 * An interface for items that contain a gas type, like samplers.
 * 
 * @author Erlend
 */
public interface ISampler
{
	/**
	 * Get the gas type contained in this sampler.
	 * 
	 * @param itemstack
	 *            The itemstack
	 * @return The gas type contained in this sampler, or null
	 */
	GasType getGasType(ItemStack itemstack);

	/**
	 * Set the gas type contained in this sampler.
	 * 
	 * @param itemstack
	 *            The itemstack
	 * @param gasType
	 *            The gas type
	 * @return An itemstack with the item containing the gas type
	 */
	ItemStack setGasType(ItemStack itemstack, GasType gasType);
}
