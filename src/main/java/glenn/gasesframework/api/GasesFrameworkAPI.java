package glenn.gasesframework.api;

import glenn.gasesframework.api.reaction.Reaction;
import glenn.gasesframework.api.reaction.ReactionEmpty;
import glenn.gasesframework.api.type.GasType;
import glenn.gasesframework.api.type.GasTypeAir;
import glenn.gasesframework.api.type.GasTypeFire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * <b>The Gases Framework API</b>
 * <br>
 * <br>
 * This piece of software is covered under the LGPL license. Redistribution and modification is permitted.
 * But honestly, why would you want to modify it?
 * @author Glenn
 * @author Trent
 */
public class GasesFrameworkAPI
{
	public static final String OWNER = "gasesFramework";
	public static final String VERSION = "1.0.6";
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
	 * The gas type for air.
	 */
	public static final GasType gasTypeAir = new GasTypeAir();
	/**
	 * The gas type for smoke.
	 */
	public static final GasType gasTypeSmoke = new GasType(true, 1, "smoke", 0x3F3F3F9F, 2, -16, Combustibility.NONE).setEffectRates(4, 4, 16);
	/**
	 * The gas type for ignited gas.
	 */
	public static final GasType gasTypeFire = new GasTypeFire();
	
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
	 * This method may give false negatives if Gases Framework is loaded after this method is called. To ensure this, make sure Gases Framework is loaded before your mod.
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
	 * If gas exists at this location, it will be ignited.
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
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present, null is returned.
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
	 * @return
	 */
	public static float getGasExplosionPowerFactor()
	{
		return modInstance.getGasExplosionPowerFactor();
	}
	
	/**
	 * Get the amount of smoke to be generated by fires.
	 * @return
	 */
	public static int getFireSmokeAmount()
	{
		return modInstance.getFireSmokeAmount();
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
	 * Register a non-expiring lantern that contains a certain item. This lantern can be created by right-clicking a lantern with the input in hand, or by crafting an empty lantern with the input.
	 * @param input - The item contained by this lantern.
	 * @param name - The block name for this lantern.
	 * @param lightLevel - The level of light emitted by this lantern from 0.0f to 1.0f.
	 * @param textureName - The texture name of the item displayed inside the lantern.
	 * @return
	 */
	public static Block registerLanternType(ItemStack input, String name, float lightLevel, String textureName)
	{
		if(isModInstalled())
		{
			return modInstance.registerLanternType(input, name, lightLevel, textureName);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Register an expiring lantern that contains a certain item. This lantern can be created by right-clicking a lantern with the input in hand, or by crafting an empty lantern with the input.
	 * @param tickRate - The rate at which the lantern expires. Lower values will cause the lantern to expire more quickly.
	 * @param input - The item contained by this lantern.
	 * @param output - The item given when the item is removed.
	 * @param expirationBlock - The block this lantern will transform into when it expires. Ideally, this is another lantern block.
	 * @param name - The block name for this lantern.
	 * @param lightLevel - The level of light emitted by this lantern from 0.0f to 1.0f.
	 * @param textureName - The texture name of the item displayed inside the lantern.
	 * @return
	 */
	public static Block registerExpiringLanternType(int tickRate, ItemStack input, ItemStack output, Block expirationBlock, String name, float lightLevel, String textureName)
	{
		if(isModInstalled())
		{
			return modInstance.registerExpiringLanternType(tickRate, input, output, expirationBlock, name, lightLevel, textureName);
		}
		else
		{
			return null;
		}
	}
}