package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.api.reaction.Reaction;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * An interface to connect the GFAPI to the registry of the GasesFramework mod.
 * @author Erlend
 */
public interface IGFRegistry
{
	/**
	 * Register a furnace recipe that only applies to gas furnaces.
	 * A gas furnace recipe will always be prioritized before an ordinary furnace recipe.
	 * @param ingredient The item that can be smelted. Can be more than 1
	 * @param result The result of the smelting action
	 * @param time The amount of time it takes to smelt. Default is 200
	 * @param exp The amount of exp given
	 */
	void registerGasFurnaceRecipe(ItemStack ingredient, ItemStack result, int time, int exp);



	/**
	 * Returns true if the block is a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * @param block The block in question
	 * @return True if the block is an ignition block
	 */
	boolean isIgnitionBlock(Block block);

	/**
	 * Register a block as a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * @param block The block
	 */
	void registerIgnitionBlock(Block block);

	/**
	 * Unregister a block as a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * @param block The block
	 */
	void unregisterIgnitionBlock(Block block);

	/**
	 * Get an array of all gas igniting blocks, e.g. blocks which can cause a gas to combust or explode.
	 * @return A modifiable array with all registered ignition blocks
	 */
	Block[] getRegisteredIgnitionBlocks();



	/**
	 * Returns true if the item is a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * @param item The item in question
	 * @return True if the item is an ignition item
	 */
	boolean isIgnitionItem(Item item);

	/**
	 * Register an item as a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * @param item The item
	 */
	void registerIgnitionItem(Item item);

	/**
	 * Unregister an item as a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * @param item The item
	 */
	void unregisterIgnitionItem(Item item);

	/**
	 * Get an array of all gas igniting items, e.g. items which can cause a gas to combust or explode when held, equipped or dropped.
	 * @return A modifiable array with all registered ignition items
	 */
	Item[] getRegisteredIgnitionItems();



	/**
	 * Register a reaction for a set of gas types.
	 * Reactions must be registered during {@link cpw.mods.fml.common.event.FMLInitializationEvent Initialization}.
	 * @param reaction The reaction to register to the gas types
	 * @param gasTypes The gas types to register the reaction to
	 */
	void registerReaction(Reaction reaction, GasType... gasTypes);

	/**
	 * Is this reaction registered for this gas type?
	 * @param reaction The reaction
	 * @return True if the reaction is registered to the gas type
	 */
	boolean isReactionRegistered(Reaction reaction, GasType gasType);

	/**
	 * Get an array of all registered reactions for a gas type.
	 * @return A modifiable array with all reactions registered for the gas type
	 */
	Reaction[] getRegisteredReactions(GasType gasType);



	/**
	 * Get a gas type by its ID.
	 * @param id The gas ID
	 * @return A gas type with a matching gas ID, or null
	 */
	GasType getGasTypeByID(int id);

	/**
	 * Get a gas type by its name.
	 * @param name The gas name
	 * @return A gas type with a matching name, or null
	 */
	GasType getGasTypeByName(String name);

	/**
	 * Registers a gas type. This involves creating and registering the blocks necessary for a gas type.
	 * Gas types must be registered during {@link cpw.mods.fml.common.event.FMLPreInitializationEvent PreInitialization}.
	 * @param type The gas type
	 */
	void registerGasType(GasType type);

	/**
	 * Is this gas type registered?
	 * @param type The gas type
	 * @return True if the gas type is registered
	 */
	boolean isGasTypeRegistered(GasType type);

	/**
	 * Get an array of all registered gas types.
	 * @return A modifiable array with all registered gas types
	 */
	GasType[] getRegisteredGasTypes();



	/**
	 * Get a lantern type by its name.
	 * @param name The lantern name
	 * @return A lantern type with a matching name, or null
	 */
	LanternType getLanternTypeByName(String name);

	/**
	 * Get a lantern type by its item input.
	 * @param itemKey The input item
	 * @return A lantern type for the item input, or null
	 */
	LanternType getLanternTypeByInput(ItemKey itemKey);

	/**
	 * Registers a lantern type. This involves creating and registering the blocks necessary for a lantern type.
	 * Lantern types must be registered during {@link cpw.mods.fml.common.event.FMLPreInitializationEvent PreInitialization}.
	 * @param type The lantern type
	 */
	void registerLanternType(LanternType type);

	/**
	 * Is this lantern type registered?
	 * @param type The lantern type
	 * @return True if the lantern type is registered
	 */
	boolean isLanternTypeRegistered(LanternType type);

	/**
	 * Register a recipe for a lantern type. This will override conflicting registered inputs.
	 * Lantern inputs must be registered during {@link cpw.mods.fml.common.event.FMLInitializationEvent Initialization}.
	 * @param type The lantern type
	 * @param itemKey The item that can be put in the lantern
	 */
	void registerLanternInput(LanternType type, ItemKey itemKey);

	/**
	 * Get an array of all registered lantern types.
	 * @return A modifiable array with all registered lantern types
	 */
	LanternType[] getRegisteredLanternTypes();



	/**
	 * Registers a gas world generator for generation in certain dimensions.
	 * @param type The world gen type to register
	 */
	void registerGasWorldGenType(GasWorldGenType type, String... dimensions);

	/**
	 * Is this gas world gen type registered for this dimension?
	 * @param type The world gen type in question
	 * @param dimension The dimension in question
	 * @return True if this gas world gen type is registered for this dimension
	 */
	boolean isGasWorldGenTypeRegistered(GasWorldGenType type, String dimension);



	/**
	 * Registers a gas transposer handler.
	 * @param handler The gas transposer handler
	 */
	void registerGasTransposerHandler(IGasTransposerHandler handler);

	/**
	 * Is this gas transposer handler registered?
	 * @param handler The gas transposer handler in question
	 * @return True if this gas transposer handler is registered
	 */
	boolean isGasTransposerHandlerRegistered(IGasTransposerHandler handler);



	/**
	 * Get a pipe type by its ID.
	 * @param id The pipe ID
	 * @return A pipe with a matching ID, or null
	 */
	PipeType getPipeTypeByID(int id);

	/**
	 * Get a pipe type by its name.
	 * @param name The pipe name
	 * @return A pipe with a matching ID, or null
	 */
	PipeType getPipeTypeByName(String name);

	/**
	 * Registers a pipe type.
	 * Pipe types must be registered during PreInit.
	 * @param type The pipe type
	 */
	void registerPipeType(PipeType type);

	/**
	 * Is this pipe type registered?
	 * @param type The pipe type in question
	 * @return True if the pipe type is registered
	 */
	boolean isPipeTypeRegistered(PipeType type);

	/**
	 * Get an array of all registered pipe types.
	 * @return A modifiable array of all registered pipe types
	 */
	PipeType[] getRegisteredPipeTypes();
}
