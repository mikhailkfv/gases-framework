package glenn.gasesframework.api;

import glenn.gasesframework.api.ExtendedGasEffectsBase.EffectType;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gastype.GasTypeAir;
import glenn.gasesframework.api.gastype.GasTypeFire;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.api.reaction.Reaction;
import glenn.gasesframework.api.reaction.ReactionEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * <b>The Gases Framework API</b>
 * <br>
 * <br>
 * <i>This API will work both with and without the Gases Framework installed, <b>with some restrictions</b>. Certain methods will throw exceptions if called when the mod is missing.</i>
 * <br>
 * <ul>
 * <li>You can determine the mod installation state by a query to {@link #isModInstalled()}.</li>
 * <li><b>IMPORTANT NOTE: To ensure the API will work properly when the mod is loaded, your mod must have the following added to its {@link cpw.mods.fml.common.Mod Mod} annotation:</b><br>
 * <i>{@link cpw.mods.fml.common.Mod#dependencies dependencies}="after:gasesFrameworkCore"</i></li>
 * <li>If you want the mod to work only if the Gases Framework mod installed, add the following instead:</br>
 * <i>{@link cpw.mods.fml.common.Mod#dependencies dependencies}="require-after:gasesFrameworkCore"</i></li>
 * <br>
 * </ul>
 * This piece of software is covered under the LGPL license. Redistribution and modification is permitted.
 * But honestly, why would you want to modify it?
 * @author Glenn
 * @author Trent
 */
public class GasesFrameworkAPI
{
	public static final String OWNER = "gasesFramework";
	public static final String VERSION = "1.1.2";
	public static final String TARGETVERSION = "1.7.10";
	public static final String PROVIDES = "gasesFrameworkAPI";
	
	private static HashSet<Block> gasReactiveBlocks = new HashSet<Block>();
	private static HashSet<Item> gasReactiveItems = new HashSet<Item>();
	private static ArrayList<Reaction> reactions = new ArrayList<Reaction>();
	
	/**
	 * The mod instance of Gases Framework. This serves as a connection point between the API and the mod itself.
	 * If the mod is not installed, this field is null.
	 */
	public static IGasesFramework modInstance;
	
	/**
	 * The default overlay image used when the player is submerged in gas.
	 */
	public static final ResourceLocation gasOverlayImage = new ResourceLocation("gasesframework:textures/misc/gas_overlay.png");
	/**
	 * The overlay image used when the player is inside ignited gas.
	 */
	public static final ResourceLocation fireOverlayImage = new ResourceLocation("gasesframework:textures/misc/fire_overlay.png");
	/**
	 * An empty overlay image used when the player is submerged in gas.
	 */
	public static final ResourceLocation emptyOverlayImage = new ResourceLocation("gasesframework:textures/misc/empty_overlay.png");

	/**
	 * The gas type for air. Do not register this!
	 */
	public static final GasType gasTypeAir = new GasTypeAir();
	/**
	 * The gas type for smoke. Do not register this!
	 */
	public static final GasType gasTypeSmoke = new GasType(true, 1, "smoke", 0x3F3F3F9F, 2, -16, Combustibility.NONE).setEffectRate(EffectType.BLINDNESS, 4).setEffectRate(EffectType.SUFFOCATION, 4).setEffectRate(EffectType.SLOWNESS, 16);
	/**
	 * The gas type for ignited gas. Do not register this!
	 */
	public static final GasType gasTypeFire = new GasTypeFire();
	
	/**
	 * The lantern type for empty lanterns. Do not register this!
	 */
	public static final LanternType lanternTypeEmpty = new LanternType("empty", 0.0f, "gasesframework:lantern_empty", new ItemKey(), null, 0).setInOut();
	/**
	 * The lantern type for lanterns containing bottles. Do not register this!
	 */
	public static final LanternType lanternTypeGasEmpty = new LanternType("gas_empty", 0.0f, "gasesframework:lantern_gas_empty", new ItemKey(Items.glass_bottle), lanternTypeEmpty, 0).setInOut();
	/**
	 * A list of lantern types for lanterns containing gas of varying {@link glenn.gasesframework.api.Combustibility#burnRate burn rates}. Do not register these!
	 */
	public static final LanternType[] lanternTypesGas = new LanternType[] {
		lanternTypeGasEmpty,
		new LanternType("gas_1", 1.0f, "gasesframework:lantern_gas_1", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 1),
		new LanternType("gas_2", 1.0f, "gasesframework:lantern_gas_2", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 2),
		new LanternType("gas_3", 1.0f, "gasesframework:lantern_gas_3", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 3),
		new LanternType("gas_4", 1.0f, "gasesframework:lantern_gas_4", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 4),
		new LanternType("gas_5", 1.0f, "gasesframework:lantern_gas_5", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 5)
	};
	
	/**
	 * The item used for glass bottles containing gas. These bottles are registered automatically for each gas type created, unless it is specified as non-industrial.
	 * If Gases Framework is not installed, this is null.
	 */
	public static Item gasBottle;
	/**
	 * The item used for including gas sampling. Sub-types of the samplers are registered automatically for each gas type created, unless it is specified as non-industrial.
	 * If Gases Framework is not installed, this is null.
	 */
	public static Item gasSamplerIncluder;
	/**
	 * The item used for excluding gas sampling. Sub-types of the samplers are registered automatically for each gas type created, unless it is specified as non-industrial.
	 * If Gases Framework is not installed, this is null.
	 */
	public static Item gasSamplerExcluder;
	/**
	 * Used in crafting.
	 * If Gases Framework is not installed, this is null.
	 */
	public static Item adhesive;
	/**
	 * It's duct tape. If you can't fix it with duct tape, it's not worth fixing.
	 * If Gases Framework is not installed, this is null.
	 */
	public static Item ductTape;
	
	/**
	 * The damage source used when a player asphyxiates in gas.
	 */
	public static final DamageSource asphyxiationDamageSource = new DamageSource("gf_asphyxiation");
	/**
	 * The creative tab used by Gases Framework. It sports a fancy lantern icon.
	 * If Gases Framework is not installed, this is null.
	 */
	public static CreativeTabs creativeTab;
	
	/**
	 * Returns true if Gases Framework is installed.
	 * This method may give false negatives if Gases Framework is loaded after this method is called. See {@link GasesFrameworkAPI}.
	 */
	public static boolean isModInstalled()
	{
		return modInstance != null;
	}
	
	/**
	 * Register a block as a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * This method is safe to call when Gases Framework is not present.
	 * @param block
	 */
	public static void registerIgnitionBlock(Block block)
	{
		gasReactiveBlocks.add(block);
	}
	
	/**
	 * Unregister a block as a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * This method is safe to call when Gases Framework is not present.
	 * @param block
	 */
	public static void unregisterIgnitionBlock(Block block)
	{
		gasReactiveBlocks.remove(block);
	}
	
	/**
	 * Returns true if the block is a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * This method is safe to call when Gases Framework is not present.
	 * @param block
	 * @return isGasReactive
	 */
	public static boolean isIgnitionBlock(Block block)
	{
		return gasReactiveBlocks.contains(block);
	}
	
	/**
	 * Register an item as a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * This method is safe to call when Gases Framework is not present.
	 * @param item
	 */
	public static void registerIgnitionItem(Item item)
	{
		gasReactiveItems.add(item);
	}
	
	/**
	 * Unregister an item as a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * This method is safe to call when Gases Framework is not present.
	 * @param item
	 */
	public static void unregisterIgnitionItem(Item item)
	{
		gasReactiveItems.remove(item);
	}
	
	/**
	 * Returns true if the item is a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * This method is safe to call when Gases Framework is not present.
	 * @param item
	 * @return isGasReactive
	 */
	public static boolean isIgnitionItem(Item item)
	{
		return gasReactiveItems.contains(item);
	}
	
	/**
	 * Registers a custom gas reaction.
	 * This method is safe to call when Gases Framework is not present.
	 * @param reaction
	 */
	public static void registerReaction(Reaction reaction)
	{
		if(!reaction.isErroneous())
		{
			reactions.add(reaction);
		}
	}
	
	/**
	 * Gets the reaction between 2 blocks. Returns an empty reaction if it doesn't exist (not null)
	 * This method is safe to call when Gases Framework is not present.
	 * @param block1
	 * @param block2
	 * @return
	 */
	public static Reaction getReactionForBlocks(World world, Block block1, int block1X, int block1Y, int block1Z, Block block2, int block2X, int block2Y, int block2Z)
	{
		for(Reaction reaction : reactions)
		{
			if(reaction.is(world, block1, block1X, block1Y, block1Z, block2, block2X, block2Y, block2Z)) return reaction;
		}
		
		return new ReactionEmpty();
	}
	
	/**
	 * Adds a special furnace recipe which can be used in a gas furnace. Special furnace recipes are notably different in the way the stack size of what is smelted matters.
	 * This method is safe to call when Gases Framework is not present.
	 * @param ingredient - The item to be smelted. Can have a stack size larger than 1.
	 * @param result - The result of the smelting action.
	 * @param cookTime - The time it takes to complete the smelting action. Default is 200.
	 */
	public static void addSpecialFurnaceRecipe(ItemStack ingredient, ItemStack result, int cookTime, int exp)
	{
		if(isModInstalled())
		{
			modInstance.addSpecialFurnaceRecipe(ingredient, result, cookTime, exp);
		}
	}
	
	/**
	 * Returns true if this block coordinate can be filled with a unit of gas.
	 * If this returns true, {@link glenn.gasesframework.api.IGasesFramework#fillWithGas(World, Random, int, int, int, GasType) fillWithGas(World,Random,int,int,int,GasType)} will also return true.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world - The world object
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return If true, the unit of gas can be consumed.
	 */
	public static boolean canFillWithGas(World world, int x, int y, int z, GasType type)
	{
		return modInstance.canFillWithGas(world, x, y, z, type);
	}
	
	/**
	 * Try to fill this block coordinate with a unit of gas. If necessary, this method will spread the gas outwards.
	 * The result of this method can be predetermined with {@link glenn.gasesframework.api.IGasesFramework#canFillWithGas(World, int, int, int, GasType) canFillWithGas(World,int,int,int,GasType)}.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return If true, the unit of gas was consumed.
	 */
	public static boolean fillWithGas(World world, Random random, int x, int y, int z, GasType type)
	{
		return modInstance.fillWithGas(world, random, x, y, z, type);
	}
	
	/**
	 * Place a gas block of the specified type with a specific volume ranging from 0 to 16.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param volume
	 * @return
	 */
	public static void placeGas(World world, int x, int y, int z, GasType type, int volume)
	{
		modInstance.placeGas(world, x, y, z, type, volume);
	}
	
	/**
	 * Pump gas into an IGasTransporter or an IGasReceptor with a certain direction and pressure.
	 * If the block is an IGasTransporter, the gas will be pumped as far as the pressure allows it.
	 * This method is unsafe to call when Gases Framework is not present.
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
	public static boolean pumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		return modInstance.pumpGas(world, random, x, y, z, type, direction, pressure);
	}

	/**
	 * Push gas to a coordinate with a certain direction and pressure.
	 * If the block is an IGasTransporter or IGasReceptor, {@link glenn.gasesframework.api.IGasesFramework#pumpGas(World,int,int,int,GasType,ForgeDirection,int) pumpGas(World,int,int,int,GasType,ForgeDirection,int)} is returned.
	 * Else, {@link glenn.gasesframework.api.IGasesFramework#fillWithGas(World,int,int,int,GasType) fillWithGas(World,int,int,int,GasType)} is returned.
	 * This method is unsafe to call when Gases Framework is not present.
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
	public static boolean pushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		return modInstance.pushGas(world, random, x, y, z, type, direction, pressure);
	}
	
	/**
	 * If gas exists at this location, it will be ignited.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 */
	public static void ignite(World world, int x, int y, int z, Random random)
	{
		modInstance.ignite(world, x, y, z, random);
	}

	/**
	 * Spawn a delayed explosion in the world.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @param power
	 * @param isFlaming
	 * @param isSmoking
	 */
	public static void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking)
	{
		modInstance.spawnDelayedExplosion(world, x, y, z, delay, power, isFlaming, isSmoking);
	}
	
	/**
	 * Sent a filter update packet for {@link glenn.gasesframework.api.block.IGasTypeFilter IGasFilter} blocks to clients.
	 * This will call {@link glenn.gasesframework.api.block.IGasTypeFilter#setFilter(World,int,int,int,ForgeDirection,GasTypeFilter) setFilter(World,int,int,int,ForgeDirection,GasTypeFilter)}.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param filter
	 */
	public static void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{
		modInstance.sendFilterUpdatePacket(world, x, y, z, side, filter);
	}
	
	/**
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present, null is returned.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static GasType getGasType(World world, int x, int y, int z)
	{
		return modInstance.getGasType(world, x, y, z);
	}
	
	/**
	 * Gets the gas type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static GasType getGasPipeType(World world, int x, int y, int z)
	{
		return modInstance.getGasPipeType(world, x, y, z);
	}
	
	/**
	 * Gets the volume of a gas block ranging from 1 to 16.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int getGasVolume(World world, int x, int y, int z)
	{
		return modInstance.getGasVolume(world, x, y, z);
	}
	
	/**
	 * Get the global multiplier for gas explosion power.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @return
	 */
	public static float getGasExplosionPowerFactor()
	{
		return modInstance.getGasExplosionPowerFactor();
	}
	
	/**
	 * Get the amount of smoke to be generated by fires.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @return
	 */
	public static int getFireSmokeAmount()
	{
		return modInstance.getFireSmokeAmount();
	}
	
	/**
	 * Get the block rendering ID for blocks that implement {@link glenn.gasesframework.api.block.IRenderedGasTypeFilter IRenderedGasTypeFilter}.
	 * This method is unsafe to call when Gases Framework is not present.
	 * @return
	 */
	public static int getRenderedGasTypeFilterBlockRenderType()
	{
		return modInstance.getRenderedGasTypeFilterBlockRenderType();
	}
	
	/**
	 * Registers a gas type. This involves creating and registering the blocks necessary for a gas type.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param type
	 * @return The gas block registered for this type, if any.
	 */
	public static Block registerGasType(GasType type)
	{
		if(isModInstalled())
		{
			return modInstance.registerGasType(type);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Registers a gas type and places the gas block on a creative tab. This involves creating and registering the blocks necessary for a gas type.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param type
	 * @param creativeTab
	 * @return The gas block registered for this type, if any.
	 */
	public static Block registerGasType(GasType type, CreativeTabs creativeTab)
	{
		if(isModInstalled())
		{
			return modInstance.registerGasType(type, creativeTab);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Registers a lantern type. This involves creating and registering the blocks necessary for a lantern type.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param type
	 * @return The lantern block registered for this type, if any.
	 */
	public static Block registerLanternType(LanternType type)
	{
		if(isModInstalled())
		{
			return modInstance.registerLanternType(type);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Registers a lantern type. This involves creating and registering the blocks necessary for a lantern type.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param type
	 * @param creativeTab
	 * @return The lantern block registered for this type, if any.
	 */
	public static Block registerLanternType(LanternType type, CreativeTabs creativeTab)
	{
		if(isModInstalled())
		{
			return modInstance.registerLanternType(type, creativeTab);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Registers a gas world generator for generation in certain dimensions.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param type
	 */
	public static void registerGasWorldGenType(GasWorldGenType type, String ... dimensionNames)
	{
		if(isModInstalled())
		{
			modInstance.registerGasWorldGenType(type, dimensionNames);
		}
	}
	
	/**
	 * Registers a gas transposer handler.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param handler
	 */
	public static void registerGasTransposerHandler(IGasTransposerHandler handler)
	{
		if(isModInstalled())
		{
			modInstance.registerGasTransposerHandler(handler);
		}
	}
	
	/**
	 * Registers a pipe type.
	 * This method will do nothing if Gases Framework is not installed, and the type will not be marked as registered.
	 * @param handler
	 */
	public static void registerPipeType(PipeType type)
	{
		if(isModInstalled())
		{
			modInstance.registerPipeType(type);
		}
	}
}