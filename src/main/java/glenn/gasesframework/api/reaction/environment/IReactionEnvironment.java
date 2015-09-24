package glenn.gasesframework.api.reaction.environment;

import java.util.Random;

import glenn.gasesframework.api.PartialGasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * IReactionEnvironment is an abstractification of an environment where a reaction can happen.
 * It is up to the Gases Framework implementation to provide such environments.
 * While reactions currently only happen in the world, this interface ensures compatibility with potential new reaction environments.
 */
public interface IReactionEnvironment
{
	/**
	 * Get A as a PartialGasStack.
	 * @return
	 */
	PartialGasStack getA();

	/**
	 * Set A as a PartialGasStack.
	 * @param a
	 */
	void setA(PartialGasStack a);

	/**
	 * Ignite A.
	 */
	void igniteA();

	/**
	 * Drop an item.
	 * @param itemstack
	 */
	void dropItem(ItemStack itemstack);

	/**
	 * Create an explosion.
	 * @param power
	 * @param isFlaming
	 * @param isSmoking
	 */
	void explode(float power, boolean isFlaming, boolean isSmoking);

	/**
	 * Get a Random object.
	 * @return
	 */
	Random getRandom();

	/**
	 * Get the world object.
	 * @return
	 */
	World getWorld();

	/**
	 * Play a sound.
	 * @param name
	 * @param volume
	 * @param pitch
	 */
	void playSound(String name, float volume, float pitch);
}
