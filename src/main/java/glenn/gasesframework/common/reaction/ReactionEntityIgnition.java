package glenn.gasesframework.common.reaction;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.reaction.EntityReaction;
import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ReactionEntityIgnition extends EntityReaction
{
	@Override
	public void react(IEntityReactionEnvironment environment)
	{
		if (shouldIgnite(environment.getB()))
		{
			environment.igniteA();
		}
	}

	protected boolean shouldIgnite(Entity entity)
	{
		if (entity.isBurning())
		{
			return true;
		}
		else if (entity instanceof EntityPlayer)
		{
			EntityPlayer playerEntity = (EntityPlayer) entity;

			ItemStack equippedItem = playerEntity.getCurrentEquippedItem();
			if (equippedItem != null && GasesFramework.registry.isIgnitionItem(equippedItem.getItem()))
			{
				return true;
			}

			for (int i = 0; i < 4; i++)
			{
				ItemStack armorItem = playerEntity.inventory.armorItemInSlot(i);
				if (armorItem != null && GasesFramework.registry.isIgnitionItem(armorItem.getItem()))
				{
					return true;
				}
			}
		}
		else if (entity instanceof EntityItem)
		{
			EntityItem itemEntity = (EntityItem) entity;
			ItemStack droppedItem = itemEntity.getEntityItem();
			if (GasesFramework.registry.isIgnitionItem(droppedItem.getItem()))
			{
				return true;
			}
		}

		return false;
	}
}
