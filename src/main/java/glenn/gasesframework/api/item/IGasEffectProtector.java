package glenn.gasesframework.api.item;

import glenn.gasesframework.api.ExtendedGasEffectsBase.EffectType;
import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * An interface for items that can protect against gas effects when held or
 * worn.
 */
public interface IGasEffectProtector
{
	/**
	 * Return true if this item will protect the holder from this effect.
	 * 
	 * @param entity
	 *            The affected entity
	 * @param itemstack
	 *            The itemstack containing the IGasEffectProtector
	 * @param slot
	 *            The slot the itemstack comes from. 0 is held item, 1-4 is
	 *            armor
	 * @param gasType
	 *            The gas type causing this effect
	 * @param effect
	 *            The effect that can be prevented
	 * @return True if the item can protect agains this gas effect
	 */
	boolean protect(EntityLivingBase entity, ItemStack itemstack, int slot, GasType gasType, EffectType effect);

	/**
	 * Get the itemstack after it has protected the holder against one or
	 * several gas effects. Return itemstack if the item is unaffected.
	 * 
	 * @param entity
	 *            - The affected entity
	 * @param itemstack
	 *            - The itemstack containing the IGasEffectProtector
	 * @param slot
	 *            - The slot the itemstack comes from. 0 is held item, 1-4 is
	 *            armor
	 * @param gasType
	 *            - The gas type causing this effect
	 * @return The itemstack after it has protected the affected entity
	 */
	ItemStack getItemstackOnProtect(EntityLivingBase entity, ItemStack itemstack, int slot, GasType gasType);
}