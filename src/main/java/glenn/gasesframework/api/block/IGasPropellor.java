package glenn.gasesframework.api.block;

import net.minecraft.world.World;

/**
 * An interface for interactivity with gas pipe systems. A gas propellor is a block that can propel gas from one or more of its sides.
 * This interface must be implemented to allow piping guides to originate from this block.
 * @author Glenn
 *
 */
public interface IGasPropellor
{
	/**
	 * Returns true if the gas propellor can propel gas from this specific side.
	 * @param world - The world object
	 * @param x
	 * @param y
	 * @param z
	 * @param side - The side gas can be propelled from.
	 * @return
	 */
	boolean canPropelGasFromSide(World world, int x, int y, int z, int side);
}