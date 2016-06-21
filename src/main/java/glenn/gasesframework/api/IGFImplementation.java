package glenn.gasesframework.api;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;

import java.util.Random;

import glenn.gasesframework.api.pipetype.PipeType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface to connect the GFAPI to the functionality of the GasesFramework mod.
 * @author Erlend
 */
public interface IGFImplementation
{
	/**
	 * Returns true if this block coordinate can be filled with a unit of gas.
	 * If this returns true, {@link IGFImplementation#tryFillWithGas(World, Random, int, int, int, GasType) tryFillWithGas(World,Random,int,int,int,GasType)} will also return true.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param type The gas type that can or cannot be filled here
	 * @return True if the unit of gas can be filled here
	 */
	boolean canFillWithGas(World world, int x, int y, int z, GasType type);
	
	/**
	 * Try to fill this block coordinate with a unit of gas. If necessary, this method will spread the gas outwards.
	 * The result of this method can be predetermined with {@link IGFImplementation#canFillWithGas(World, int, int, int, GasType) canFillWithGas(World,int,int,int,GasType)}.
	 * @param world The world object
	 * @param random The random object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param type The gas type that can or cannot be filled here
	 * @return True if this unit of gas was filled here
	 */
	boolean tryFillWithGas(World world, Random random, int x, int y, int z, GasType type);

	/**
	 * Place a gas block regardless of what is already there.
	 * If the type is air, and volume > 0, air will be placed.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param gasStack The amount of gas to place
	 */
	void placeGas(World world, int x, int y, int z, PartialGasStack gasStack);
	
	/**
	 * Place a gas block of the specified type with a specific volume ranging from 0 to 16.
	 * If the type is air, and volume > 0, air will be placed.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param type The type of gas to place
	 * @param volume The amount of the gas type to place
	 */
	void placeGas(World world, int x, int y, int z, GasType type, int volume);
	
	/**
	 * Try to pump gas into an IGasTransporter or an IGasReceptor with a certain direction and pressure.
	 * If the block is an IGasTransporter, the gas will be pumped as far as the pressure allows it.
	 * @param world The world object
	 * @param random The random object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param type The type of gas to pump
	 * @param direction The direction of pumping
	 * @param pressure The pressure of the pumping action
	 * @return True if the pumping action succeeded
	 */
	boolean tryPumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure);
	
	/**
	 * Push gas to a coordinate with a certain direction and pressure.
	 * If the block is an IGasTransporter or IGasReceptor,
	 * {@link IGFImplementation#tryPumpGas(World,Random,int,int,int,GasType,ForgeDirection,int) tryPumpGas(World,Random,int,int,int,GasType,ForgeDirection,int)} is returned.
	 * Else, {@link IGFImplementation#tryFillWithGas(World,Random,int,int,int,GasType) tryFillWithGas(World,Random,int,int,int,GasType)} is returned.
	 * @param world The world object
	 * @param random The random object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param type The type of gas to push
	 * @param direction The direction of pushing
	 * @param pressure The pressure of the pushing action
	 * @return True if the pumping action succeeded
	 */
	boolean tryPushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure);

	/**
	 * Place a pipe block of the specified type containing a gas.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param pipeType The pipe type that will be placed
	 * @param gasType The gas type of the unit of gas that will be placed in the pipe. Use air for empty pipes
	 */
	void placePipe(World world, int x, int y, int z, PipeType pipeType, GasType gasType);
	
	/**
	 * If gas exists at this location, it will be ignited.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param random The random object
	 */
	void ignite(World world, int x, int y, int z, Random random);
	
	/**
	 * Spawn a delayed explosion in the world.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param delay The amount of ticks before the explosion happens
	 * @param power The power of the explosion
	 * @param isFlaming If true, the explosion will leave flames
	 * @param isSmoking If true, the explosion will leave smoke particles
	 */
	void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking);
	
	/**
	 * Sent a filter update packet for {@link glenn.gasesframework.api.block.IGasTypeFilter IGasTypeFilter} blocks to clients.
	 * This will call {@link glenn.gasesframework.api.block.IGasTypeFilter#setFilter(IBlockAccess,int,int,int,ForgeDirection,GasTypeFilter) setFilter(IBlockAccess,int,int,int,ForgeDirection,GasTypeFilter)}.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param side The side the filter is applied on
	 * @param filter The filter to apply
	 */
	void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter);



	/**
	 * Get a PartialGasStack at the location. If the block is air, a full stack of air is returned. Else, null is returned.
	 * @param blockAccess The block access
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return The PartialGasStack at the location, or null if no gas is present
	 */
	PartialGasStack getGas(IBlockAccess blockAccess, int x, int y, int z);
	
	/**
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present and air exists, GFAPI.gasTypeAir is returned. Otherwise, null is returned.
	 * @param blockAccess The block access
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return The gas type at the location, or null if none is present
	 */
	GasType getGasType(IBlockAccess blockAccess, int x, int y, int z);
	
	/**
	 * Gets the gas type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param blockAccess The block access
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return The gas type of the unit of gas in the pipe at the location. If no pipe is present, null
	 */
	GasType getGasTypeInPipe(IBlockAccess blockAccess, int x, int y, int z);

	/**
	 * Gets the pipe type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param blockAccess The block access
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return The pipe type of the pipe at the location. If no pipe is present, null
	 */
	PipeType getPipeType(IBlockAccess blockAccess, int x, int y, int z);

	/**
	 * Gets the volume of a gas block ranging from 1 to 16.
	 * If the block is not a gas block, the result is undefined.
	 * @param blockAccess The block access
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return The volume of the gas at the location, if any
	 */
	int getGasVolume(IBlockAccess blockAccess, int x, int y, int z);

	/**
	 * Get the block rendering ID for blocks that implement {@link glenn.gasesframework.api.block.IRenderedGasTypeFilter IRenderedGasTypeFilter}.
	 * @return The block rendering ID
	 */
	int getRenderedGasTypeFilterBlockRenderType();
}