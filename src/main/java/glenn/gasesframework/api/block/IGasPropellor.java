package glenn.gasesframework.api.block;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for interactivity with gas pipe systems. A gas propellor is a block that can propel gas from one or more of its sides.
 * This interface must be implemented to allow piping guides to originate from this block.
 */
public interface IGasPropellor extends IGasInterface
{
	/**
	 * Get the pressure the gas propellor can give from this side.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param side The local side gas can be propelled from.
	 * @return The pressure that the block can give from this side, or 0 if it cannot propel from this side
	 */
	int getPressureFromSide(World world, int x, int y, int z, ForgeDirection side);
}