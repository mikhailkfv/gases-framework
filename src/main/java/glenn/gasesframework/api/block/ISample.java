package glenn.gasesframework.api.block;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for blocks with interactivity with gas samplers.
 * @author Glenn
 *
 */
public interface ISample
{
	/**
	 * Called when right clicked with sampler. Returns the new gas type to be used for the sampler. Return "in" if nothing is sampled.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param in - The current gas type of the sampler
	 * @param side - The local side of the block the sampler is applied to
	 * @return
	 */
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, ForgeDirection side);
}