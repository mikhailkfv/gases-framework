package glenn.gasesframework.api.block;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for interactivity with gas pipe systems. A gas receptor is able to receive gas.
 */
public interface IGasReceptor extends IGasInterface
{
	/**
	 * Receive a gas through one of this side of the block. Returns true if the gas type is accepted and consumed.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param side The local side the received gas is inserted into
	 * @param gasType The type of gas being received
	 * @return True if the block could receive the gas
	 */
	boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType);
	
	/**
	 * Determine if a gas can be received through this side of the block.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param side The local side the received gas is inserted into
	 * @param gasType The type of gas being received
	 * @return True if the block can receive the gas
	 */
	boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType);
}