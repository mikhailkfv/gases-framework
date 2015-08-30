package glenn.gasesframework.api;

import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * An interface to connect the GasesFrameworkAPI to the actual GasesFramework mod.
 * @author Glenn
 */
public interface IGasesFramework
{
	/**
	 * Adds a special furnace recipe which can be used in a gas furnace. Special furnace recipes are notably different in the way the stack size of what is smelted matters.
	 * A special gas furnace recipe will always be prioritized before an ordinary furnace recipe.
	 * @param ingredient - The item to be smelted. Can have a stack size larger than 1.
	 * @param result - The result of the smelting action.
	 * @param cookTime - The time it takes to complete the smelting action. Default is 200.
	 */
	public void addSpecialFurnaceRecipe(ItemStack ingredient, ItemStack result, int cookTime, int exp);
	
	/**
	 * Returns true if this block coordinate can be filled with a unit of gas.
	 * If this returns true, {@link glenn.gasesframework.api.IGasesFramework#fillWithGas(World, Random, int, int, int, GasType) fillWithGas(World,Random,int,int,int,GasType)} will also return true.
	 * @param world - The world object
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return
	 */
	public boolean canFillWithGas(World world, int x, int y, int z, GasType type);
	
	/**
	 * Try to fill this block coordinate with a unit of gas. If necessary, this method will spread the gas outwards.
	 * The result of this method can be predetermined with {@link glenn.gasesframework.api.IGasesFramework#canFillWithGas(World, int, int, int, GasType) canFillWithGas(World,int,int,int,GasType)}.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return
	 */
	public boolean fillWithGas(World world, Random random, int x, int y, int z, GasType type);
	
	/**
	 * Place a gas block of the specified type with a specific volume ranging from 0 to 16.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param volume
	 * @return
	 */
	public void placeGas(World world, int x, int y, int z, GasType type, int volume);
	
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
	public boolean pumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure);
	
	/**
	 * Push gas to a coordinate with a certain direction and pressure.
	 * If the block is an IGasTransporter or IGasReceptor, {@link glenn.gasesframework.api.IGasesFramework#pumpGas(World,int,int,int,GasType,ForgeDirection,int) pumpGas(World,int,int,int,GasType,ForgeDirection,int)} is returned.
	 * Else, {@link glenn.gasesframework.api.IGasesFramework#fillWithGas(World,int,int,int,GasType) fillWithGas(World,int,int,int,GasType)} is returned.
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
	public boolean pushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure);
	
	/**
	 * If gas exists at this location, it will be ignited.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 */
	public void ignite(World world, int x, int y, int z, Random random);
	
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
	public void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking);
	
	/**
	 * Sent a filter update packet for {@link glenn.gasesframework.api.block.IGasTypeFilter IGasFilter} blocks to clients.
	 * This will call {@link glenn.gasesframework.api.block.IGasTypeFilter#setFilter(World,int,int,int,ForgeDirection,GasTypeFilter) setFilter(World,int,int,int,ForgeDirection,GasTypeFilter)}.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param filter
	 */
	public void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter);
	
	/**
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present, null is returned.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public GasType getGasType(World world, int x, int y, int z);
	
	/**
	 * Gets the gas type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public GasType getGasPipeType(World world, int x, int y, int z);
	
	/**
	 * Gets the volume of a gas block ranging from 1 to 16.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int getGasVolume(World world, int x, int y, int z);
	
	/**
	 * Get the global multiplier for gas explosion power.
	 * @return
	 */
	public float getGasExplosionPowerFactor();
	
	/**
	 * Get the amount of smoke to be generated by fires.
	 * @return
	 */
	public int getFireSmokeAmount();
	
	/**
	 * Get the block rendering ID for blocks that implement {@link glenn.gasesframework.api.block.IRenderedGasTypeFilter IRenderedGasTypeFilter}.
	 * @return
	 */
	public int getRenderedGasTypeFilterBlockRenderType();
	
	/**
	 * Registers a gas type. This involves creating and registering the blocks necessary for a gas type.
	 * @param type
	 * @return The gas block registered for this type, if any.
	 */
	public Block registerGasType(GasType type);
	
	/**
	 * Registers a gas type and places the gas block on a creative tab. This involves creating and registering the blocks necessary for a gas type.
	 * @param type
	 * @param creativeTab
	 * @return The gas block registered for this type, if any.
	 */
	public Block registerGasType(GasType type, CreativeTabs creativeTab);
	
	/**
	 * Registers a lantern type. This involves creating and registering the blocks necessary for a lantern type.
	 * @param type
	 * @return The lantern block registered for this type, if any.
	 */
	public Block registerLanternType(LanternType type);
	
	/**
	 * Registers a lantern type and places the lantern block on a creative tab. This involves creating and registering the blocks necessary for a lantern type.
	 * @param type
	 * @param creativeTab
	 * @return The lantern block registered for this type, if any.
	 */
	public Block registerLanternType(LanternType type, CreativeTabs creativeTab);
	
	/**
	 * Registers a gas world generator for generation in certain dimensions.
	 * @param type
	 */
	public void registerGasWorldGenType(GasWorldGenType type, String[] dimensionNames);
	
	/**
	 * Registers a gas transposer handler.
	 * @param handler
	 */
	public void registerGasTransposerHandler(IGasTransposerHandler handler);
	
	/**
	 * Registers a pipe type.
	 * @param type
	 */
	public void registerPipeType(PipeType type);
}