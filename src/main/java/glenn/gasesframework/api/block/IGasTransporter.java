package glenn.gasesframework.api.block;

import java.util.Random;

import net.minecraft.world.World;
import glenn.gasesframework.api.gastype.GasType;

/**
 * An interface for blocks that can carry and transport gas types, most commonly implemented by pipes.
 * @author Glenn
 *
 */
public interface IGasTransporter extends IGasInterface
{
	/**
	 * Get the type currently carried by this gas transporter.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	GasType getCarriedType(World world, int x, int y, int z);
	
	/**
	 * Set the type carried by this gas transporter and return the block in the position.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @return
	 */
	IGasTransporter setCarriedType(World world, int x, int y, int z, GasType type);
	
	void handlePressure(World world, Random random, int x, int y, int z, int pressure);
}
