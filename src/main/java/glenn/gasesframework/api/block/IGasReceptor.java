package glenn.gasesframework.api.block;

import glenn.gasesframework.api.gastype.GasType;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface for interactivity with gas pipe systems. A gas receptor is able to receive gas from a pipe.
 * @author Glenn
 *
 */
public interface IGasReceptor extends IGasInterface
{
	/**
	 * This method is called when gas is attempted to be pumped inside this block. Returns true if this gas type is accepted and consumed.
	 * @param world - The World object
	 * @param x
	 * @param y
	 * @param z
	 * @param side - The local side the received gas is inserted into
	 * @param gasType - The type of gas being received
	 * @return
	 */
	boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType);
	
	/**
	 * This method is called when it must be determined if a gas pipe can insert a type of gas into this receptor. This happens during pumping.
	 * @param world - The World object
	 * @param x
	 * @param y
	 * @param z
	 * @param side - The local side the received gas is inserted into
	 * @param gasType - The type of gas being that can be received
	 * @return
	 */
	boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType);
}