package glenn.gasesframework.common.item;

import glenn.gasesframework.common.DuctTapeGag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDuctTape extends Item
{
	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 */
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity)
	{
		if (entity instanceof EntityVillager)
		{
			if (!DuctTapeGag.isGagged(entity))
			{
				if (!player.capabilities.isCreativeMode)
				{
					itemStack.stackSize--;
				}

				entity.worldObj.playSoundAtEntity(entity, "gasesframework:effect.duct_tape", 1.0F, 1.0F);
				DuctTapeGag.gag(entity);
			}
			return true;
		}
		return false;
	}
}
