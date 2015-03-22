package glenn.gasesframework.api.item;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * An interface for items that can protect against gas effects when worn.
 * @author Glenn
 */
public interface IGasEffectProtector
{
	/**
	 * Return true if this item will protect the vision of the holder.
	 * @param entity
	 * @param stack
	 * @param type
	 * @return
	 */
	public boolean protectVision(EntityLivingBase entity, ItemStack stack, GasType type);
	/**
	 * Return true if this item will protect the breath of the holder.
	 * @param entity
	 * @param stack
	 * @param type
	 * @return
	 */
	public boolean protectBreath(EntityLivingBase entity, ItemStack stack, GasType type);
}