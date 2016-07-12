package glenn.gasesframework.api.block;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks with interactivity with gas samplers.
 * 
 * @author Erlend
 */
public interface ISample
{
	/**
	 * Called when right clicked on this side with a sampler. Get the new gas
	 * type to be used for the sampler. Return "in" if nothing is sampled.
	 * 
	 * @param world
	 *            The world object
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param z
	 *            Z coordinate
	 * @param in
	 *            The current gas type of the sampler
	 * @param side
	 *            The local side
	 * @return The new gas type to be contained in the sampler
	 */
	GasType sampleInteraction(World world, int x, int y, int z, GasType in, ForgeDirection side);
}