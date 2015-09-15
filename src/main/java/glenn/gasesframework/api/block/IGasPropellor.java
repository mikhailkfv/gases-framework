package glenn.gasesframework.api.block;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for interactivity with gas pipe systems. A gas propellor is a block that can propel gas from one or more of its sides.
 * This interface must be implemented to allow piping guides to originate from this block.
 * @author Erlend
 */
public interface IGasPropellor extends IGasInterface
{
	/**
	 * Returns the pressure the gas propellor can give from a side.
	 * @param world - The world object
	 * @param x
	 * @param y
	 * @param z
	 * @param side - The local side gas can be propelled from.
	 * @return
	 */
	int getPressureFromSide(World world, int x, int y, int z, ForgeDirection side);
}