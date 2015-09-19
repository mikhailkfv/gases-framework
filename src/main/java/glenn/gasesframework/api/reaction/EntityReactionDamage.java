package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;

public class EntityReactionDamage extends EntityReaction
{
	public final DamageSource damageSource;
	public final float damage;

	public EntityReactionDamage(DamageSource damageSource, float damage)
	{
		this.damageSource = damageSource;
		this.damage = damage;
	}

	@Override
	public void react(IEntityReactionEnvironment environment)
	{
		Entity entity = environment.getB();
		if (!(entity instanceof EntityItem))
		{
			entity.attackEntityFrom(damageSource, damage);
		}
	}
}
