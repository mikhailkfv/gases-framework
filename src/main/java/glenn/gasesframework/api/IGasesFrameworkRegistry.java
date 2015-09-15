package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.api.reaction.Reaction;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;

/**
 * An interface to connect the GasesFrameworkAPI to the registry of the GasesFramework mod.
 * @author Erlend
 */
public interface IGasesFrameworkRegistry
{
	/**
	 * Returns true if the block is a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * @param block
	 * @return isGasReactive
	 */
	boolean isIgnitionBlock(Block block);

	/**
	 * Register a block as a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * @param block
	 */
	void registerIgnitionBlock(Block block);

	/**
	 * Unregister a block as a gas igniting block, e.g. a block which can cause a gas to combust or explode.
	 * @param block
	 */
	void unregisterIgnitionBlock(Block block);

	/**
	 * Get an array of all gas igniting blocks, e.g. blocks which can cause a gas to combust or explode.
	 * @return
	 */
	Block[] getRegisteredIgnitionBlocks();



	/**
	 * Returns true if the item is a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * @param item
	 * @return isGasReactive
	 */
	boolean isIgnitionItem(Item item);

	/**
	 * Register an item as a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * @param item
	 */
	void registerIgnitionItem(Item item);

	/**
	 * Unregister an item as a gas igniting item, e.g. an item which can cause a gas to combust or explode when held, equipped or dropped.
	 * @param item
	 */
	void unregisterIgnitionItem(Item item);

	/**
	 * Get an array of all gas igniting items, e.g. items which can cause a gas to combust or explode when held, equipped or dropped.
	 * @return
	 */
	Item[] getRegisteredIgnitionItems();



	/**
	 * Gets the reaction between 2 blocks. Returns an empty reaction if it doesn't exist (not null)
	 * @param world
	 * @param block1
	 * @param block1X
	 * @param block1Y
	 * @param block1Z
	 * @param block2
	 * @param block2X
	 * @param block2Y
	 * @param block2Z
	 * @return
	 */
	Reaction getReactionForBlocks(World world, Block block1, int block1X, int block1Y, int block1Z, Block block2, int block2X, int block2Y, int block2Z);

	/**
	 * Registers a custom gas reaction.
	 * @param reaction
	 */
	void registerReaction(Reaction reaction);

	/**
	 * Is this reaction registered?
	 * @param reaction
	 * @return
	 */
	boolean isReactionRegistered(Reaction reaction);

	/**
	 * Get an array of all registered reactions.
	 * @return
	 */
	Reaction[] getRegisteredReactions();



	/**
	 * Get a gas type by its ID.
	 * @param id
	 * @return
	 */
	GasType getGasTypeByID(int id);

	/**
	 * Get a gas type by its name.
	 * @param name
	 * @return
	 */
	GasType getGasTypeByName(String name);

	/**
	 * Registers a gas type. This involves creating and registering the blocks necessary for a gas type.
	 * @param type
	 * @return The gas block registered for this type, if any.
	 */
	Block registerGasType(GasType type);

	/**
	 * Registers a gas type and places the gas block on a creative tab. This involves creating and registering the blocks necessary for a gas type.
	 * @param type
	 * @param creativeTab
	 * @return The gas block registered for this type, if any.
	 */
	Block registerGasType(GasType type, CreativeTabs creativeTab);

	/**
	 * Is this gas type registered?
	 * @param type
	 * @return
	 */
	boolean isGasTypeRegistered(GasType type);

	/**
	 * Get an array of all registered gas types.
	 * @return
	 */
	GasType[] getRegisteredGasTypes();



	/**
	 * Get a lantern type by its name.
	 * @param name
	 * @return
	 */
	LanternType getLanternTypeByName(String name);

	/**
	 * Get a lantern type by its item input.
	 * @param itemKey
	 * @return
	 */
	LanternType getLanternTypeByItemIn(ItemKey itemKey);

	/**
	 * Registers a lantern type. This involves creating and registering the blocks necessary for a lantern type.
	 * @param type
	 * @return The lantern block registered for this type, if any.
	 */
	Block registerLanternType(LanternType type);

	/**
	 * Registers a lantern type and places the lantern block on a creative tab. This involves creating and registering the blocks necessary for a lantern type.
	 * @param type
	 * @param creativeTab
	 * @return The lantern block registered for this type, if any.
	 */
	Block registerLanternType(LanternType type, CreativeTabs creativeTab);

	/**
	 * Is this lantern type registered?
	 * @param type
	 * @return
	 */
	boolean isLanternTypeRegistered(LanternType type);

	/**
	 * Register a recipe for a lantern type. This will override conflicting recipes.
	 * The lantern type can created by right clicking a lantern with the itemKey or by putting an empty lantern in a crafting grid together with the itemKey.
	 * @param type
	 * @param itemKey
	 */
	void registerLanternRecipe(LanternType type, ItemKey itemKey);

	/**
	 * Get an array of all registered lanterns.
	 * @return
	 */
	LanternType[] getRegisteredLanternTypes();



	/**
	 * Registers a gas world generator for generation in certain dimensions.
	 * @param type
	 */
	void registerGasWorldGenType(GasWorldGenType type, String... dimensions);

	/**
	 * Is this gas world gen type registered for this dimension?
	 * @param type
	 * @param dimension
	 * @return
	 */
	boolean isGasWorldGenTypeRegistered(GasWorldGenType type, String dimension);



	/**
	 * Registers a gas transposer handler.
	 * @param handler
	 */
	void registerGasTransposerHandler(IGasTransposerHandler handler);

	/**
	 * Is this gas transposer handler registered?
	 * @param handler
	 * @return
	 */
	boolean isGasTransposerHandlerRegistered(IGasTransposerHandler handler);



	/**
	 * Get a pipe type by its ID.
	 * @param id
	 * @return
	 */
	PipeType getPipeTypeByID(int id);

	/**
	 * Get a pipe type by its name.
	 * @param name
	 * @return
	 */
	PipeType getPipeTypeByName(String name);

	/**
	 * Registers a pipe type.
	 * @param type
	 */
	void registerPipeType(PipeType type);

	/**
	 * Is this pipe type registered?
	 * @param type
	 * @return
	 */
	boolean isPipeTypeRegistered(PipeType type);

	/**
	 * Get an array of all registered pipe types.
	 * @return
	 */
	PipeType[] getRegisteredPipeTypes();
}
