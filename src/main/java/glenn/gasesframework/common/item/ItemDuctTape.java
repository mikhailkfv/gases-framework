package glenn.gasesframework.common.item;

import glenn.gasesframework.common.DuctTapeGag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDuctTape extends Item
{
	private void tryGag(ItemStack itemStack, EntityPlayer entityPlayer, EntityLivingBase entity, World world)
	{
		if (!DuctTapeGag.isGagged(entity))
		{
			if (!entityPlayer.capabilities.isCreativeMode)
			{
				itemStack.stackSize--;
			}

			world.playSoundAtEntity(entity, "gasesframework:effect.duct_tape", 1.0F, 1.0F);
			DuctTapeGag.gag(entity);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		if (!world.isRemote)
		{
			if (entityPlayer.isSneaking())
			{
				this.tryGag(itemStack, entityPlayer, entityPlayer, world);
			}
		}
		return itemStack;
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on
	 * sheep.
	 */
	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer entityPlayer, EntityLivingBase entity)
	{
		if (DuctTapeGag.canGag(entity))
		{
			if (!entity.worldObj.isRemote)
			{
				this.tryGag(itemStack, entityPlayer, entity, entity.worldObj);
			}
			return true;
		}
		return false;
	}
}
