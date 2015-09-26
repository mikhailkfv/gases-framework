package glenn.gasesframework.api;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;

import java.util.Random;

import glenn.gasesframework.api.pipetype.PipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface to connect the GasesFrameworkAPI to the functionality of the GasesFramework mod.
 * @author Erlend
 */
public interface IGasesFrameworkImplementation
{
	/**
	 * Returns true if this block coordinate can be filled with a unit of gas.
	 * If this returns true, {@link IGasesFrameworkImplementation#fillWithGas(World, Random, int, int, int, GasType) fillWithGas(World,Random,int,int,int,GasType)} will also return true.
	 * @param world - The world object
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return
	 */
	boolean canFillWithGas(World world, int x, int y, int z, GasType type);
	
	/**
	 * Try to fill this block coordinate with a unit of gas. If necessary, this method will spread the gas outwards.
	 * The result of this method can be predetermined with {@link IGasesFrameworkImplementation#canFillWithGas(World, int, int, int, GasType) canFillWithGas(World,int,int,int,GasType)}.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return
	 */
	boolean fillWithGas(World world, Random random, int x, int y, int z, GasType type);

	/**
	 * Place a gas block.
	 * If the type is air, and volume > 0, air will be placed.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param gasStack
	 */
	void placeGas(World world, int x, int y, int z, PartialGasStack gasStack);
	
	/**
	 * Place a gas block of the specified type with a specific volume ranging from 0 to 16.
	 * If the type is air, and volume > 0, air will be placed.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param volume
	 * @return
	 */
	void placeGas(World world, int x, int y, int z, GasType type, int volume);
	
	/**
	 * Pump gas into an IGasTransporter or an IGasReceptor with a certain direction and pressure.
	 * If the block is an IGasTransporter, the gas will be pumped as far as the pressure allows it.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param direction
	 * @param pressure
	 * @return Whether the pumping action succeeded or not.
	 */
	boolean pumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure);
	
	/**
	 * Push gas to a coordinate with a certain direction and pressure.
	 * If the block is an IGasTransporter or IGasReceptor,
	 * {@link glenn.gasesframework.api.IGasesFrameworkImplementation#pumpGas(World,Random,int,int,int,GasType,ForgeDirection,int) pumpGas(World,Random,int,int,int,GasType,ForgeDirection,int)} is returned.
	 * Else, {@link glenn.gasesframework.api.IGasesFrameworkImplementation#fillWithGas(World,Random,int,int,int,GasType) fillWithGas(World,Random,int,int,int,GasType)} is returned.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param direction
	 * @param pressure
	 * @return Whether the pushing action succeeded or not.
	 */
	boolean pushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure);

	/**
	 * Place a pipe block of the specified type containing a gas.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param pipeType
	 * @param gasType
	 */
	void placePipe(World world, int x, int y, int z, PipeType pipeType, GasType gasType);
	
	/**
	 * If gas exists at this location, it will be ignited.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 */
	void ignite(World world, int x, int y, int z, Random random);
	
	/**
	 * Spawn a delayed explosion in the world.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @param power
	 * @param isFlaming
	 * @param isSmoking
	 */
	void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking);
	
	/**
	 * Sent a filter update packet for {@link glenn.gasesframework.api.block.IGasTypeFilter IGasTypeFilter} blocks to clients.
	 * This will call {@link glenn.gasesframework.api.block.IGasTypeFilter#setFilter(IBlockAccess,int,int,int,ForgeDirection,GasTypeFilter) setFilter(IBlockAccess,int,int,int,ForgeDirection,GasTypeFilter)}.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param filter
	 */
	void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter);



	/**
	 * Get a PartialGasStack at the location. If the block is air, a full stack of air is returned. Else, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	PartialGasStack getGas(IBlockAccess blockAccess, int x, int y, int z);
	
	/**
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	GasType getGasType(IBlockAccess blockAccess, int x, int y, int z);
	
	/**
	 * Gets the gas type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	GasType getGasTypeInPipe(IBlockAccess blockAccess, int x, int y, int z);

	/**
	 * Gets the pipe type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	PipeType getPipeType(IBlockAccess blockAccess, int x, int y, int z);

	/**
	 * Gets the volume of a gas block ranging from 1 to 16.
	 * If the block is not a gas block, the result is undefined.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	int getGasVolume(IBlockAccess blockAccess, int x, int y, int z);

	/**
	 * Get the block rendering ID for blocks that implement {@link glenn.gasesframework.api.block.IRenderedGasTypeFilter IRenderedGasTypeFilter}.
	 * @return
	 */
	int getRenderedGasTypeFilterBlockRenderType();
}