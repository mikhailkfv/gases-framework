package glenn.gasesframework.api.reaction.environment;

import java.util.Random;

import glenn.gasesframework.api.PartialGasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * IReactionEnvironment is an abstraction of an environment where a reaction can
 * happen. It is up to the Gases Framework implementation to provide such
 * environments. While reactions currently only happen in the world, this
 * interface ensures compatibility with potential new reaction environments.
 */
public interface IReactionEnvironment
{
	/**
	 * Get A as a partial gas stack.
	 * 
	 * @return A as a partial gas stack
	 */
	PartialGasStack getA();

	/**
	 * Set A as a partial gas stack.
	 * 
	 * @param a
	 *            A partial gas stack
	 */
	void setA(PartialGasStack a);

	/**
	 * Ignite A.
	 */
	void igniteA();

	/**
	 * Drop an item.
	 * 
	 * @param itemstack
	 *            The item to drop
	 */
	void dropItem(ItemStack itemstack);

	/**
	 * Create an explosion.
	 * 
	 * @param power
	 *            The power of the explosion
	 * @param isFlaming
	 *            If true, the explosion will leave flames
	 * @param isSmoking
	 *            If true, the explosion will leave smoke particles
	 */
	void explode(float power, boolean isFlaming, boolean isSmoking);

	/**
	 * Get the Random object.
	 * 
	 * @return The random object
	 */
	Random getRandom();

	/**
	 * Get the world object.
	 * 
	 * @return The world object
	 */
	World getWorld();

	/**
	 * Play a sound.
	 * 
	 * @param name
	 *            The name of the sound
	 * @param volume
	 *            The volume of the sound
	 * @param pitch
	 *            The pitch of the sound
	 */
	void playSound(String name, float volume, float pitch);
}
