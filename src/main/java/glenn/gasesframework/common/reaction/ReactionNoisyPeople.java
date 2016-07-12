package glenn.gasesframework.common.reaction;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import glenn.gasesframework.api.reaction.EntityReaction;
import glenn.gasesframework.api.reaction.environment.IEntityReactionEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ReactionNoisyPeople extends EntityReaction
{
	private static final Set<String> noisyPeople = new HashSet<String>();
	private static final Random soundRandom = new Random();

	static
	{
		noisyPeople.add("cyanideepic");
		noisyPeople.add("dethridgecraft");
		noisyPeople.add("wyld");
		noisyPeople.add("crustymustard");
		noisyPeople.add("glenna");
		noisyPeople.add("trentv4");
		noisyPeople.add("username720");
	}

	@Override
	public void react(IEntityReactionEnvironment environment)
	{
		Entity entity = environment.getB();
		if (entity instanceof EntityPlayer)
		{
			String displayName = ((EntityPlayer) entity).getDisplayName().toLowerCase();
			if (noisyPeople.contains(displayName) && soundRandom.nextInt(20) == 0)
			{
				entity.worldObj.playSoundAtEntity(entity, "mob.villager.idle", 1.0F, 0.75F + soundRandom.nextFloat() * 0.5F);
			}
		}
	}
}
