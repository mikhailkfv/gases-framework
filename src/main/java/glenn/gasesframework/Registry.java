package glenn.gasesframework;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.registry.GameRegistry;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.IGasesFrameworkRegistry;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.api.reaction.BlockReaction;
import glenn.gasesframework.api.reaction.EntityReaction;
import glenn.gasesframework.api.reaction.GasReaction;
import glenn.gasesframework.api.reaction.Reaction;
import glenn.gasesframework.common.block.BlockGas;
import glenn.gasesframework.common.block.BlockGasPipe;
import glenn.gasesframework.common.block.BlockLantern;
import glenn.gasesframework.common.item.ItemGasPipe;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Registry implements IGasesFrameworkRegistry
{
	private final Set<Block> registeredIgnitionBlocks = Collections.newSetFromMap(new IdentityHashMap<Block, Boolean>());

	@Override
	public boolean isIgnitionBlock(Block block)
	{
		return registeredIgnitionBlocks.contains(block);
	}

	@Override
	public void registerIgnitionBlock(Block block)
	{
		registeredIgnitionBlocks.add(block);
	}

	@Override
	public void unregisterIgnitionBlock(Block block)
	{
		registeredIgnitionBlocks.remove(block);
	}

	@Override
	public Block[] getRegisteredIgnitionBlocks()
	{
		Block[] ignitionBlocks = new Block[registeredIgnitionBlocks.size()];
		registeredIgnitionBlocks.toArray(ignitionBlocks);
		return ignitionBlocks;
	}



	private final Set<Item> registeredIgnitionItems = Collections.newSetFromMap(new IdentityHashMap<Item, Boolean>());

	@Override
	public boolean isIgnitionItem(Item item)
	{
		return registeredIgnitionItems.contains(item);
	}

	@Override
	public void registerIgnitionItem(Item item)
	{
		registeredIgnitionItems.add(item);
	}

	@Override
	public void unregisterIgnitionItem(Item item)
	{
		registeredIgnitionItems.remove(item);
	}

	@Override
	public Item[] getRegisteredIgnitionItems()
	{
		Item[] ignitionItems = new Item[registeredIgnitionItems.size()];
		registeredIgnitionItems.toArray(ignitionItems);
		return ignitionItems;
	}



	private final Map<GasType, Set<Reaction>> registeredReactions = new IdentityHashMap<GasType, Set<Reaction>>();
	private final Map<GasType, Set<GasReaction>> registeredGasReactions = new IdentityHashMap<GasType, Set<GasReaction>>();
	private final Map<GasType, Set<BlockReaction>> registeredBlockReactions = new IdentityHashMap<GasType, Set<BlockReaction>>();
	private final Map<GasType, Set<EntityReaction>> registeredEntityReactions = new IdentityHashMap<GasType, Set<EntityReaction>>();

	@Override
	public void registerReaction(Reaction reaction, GasType... gasTypes)
	{
		if (gasTypes.length == 0)
		{
			GasesFramework.logger.warn("Attempted to register reaction without specifying any gas types. Reactions must be bound to their respective gas types!");
		}

		for (GasType gasType : gasTypes)
		{
			if (isReactionRegistered(reaction, gasType))
			{
				throw new RuntimeException("A reaction was attempted registered to a gas type it was already registered to.");
			}

			Set<Reaction> reactions = registeredReactions.get(gasType);
			if (reactions == null)
			{
				reactions = Collections.newSetFromMap(new IdentityHashMap<Reaction, Boolean>());
				registeredReactions.put(gasType, reactions);
			}
			reactions.add(reaction);

			if (reaction instanceof GasReaction)
			{
				registerGasReaction((GasReaction)reaction, gasType);
			}

			if (reaction instanceof BlockReaction)
			{
				registerBlockReaction((BlockReaction)reaction, gasType);
			}

			if (reaction instanceof EntityReaction)
			{
				registerEntityReaction((EntityReaction)reaction, gasType);
			}
		}
	}

	private void registerGasReaction(GasReaction reaction, GasType gasType)
	{
		Set<GasReaction> gasReactions = registeredGasReactions.get(gasType);
		if (gasReactions == null)
		{
			gasReactions = Collections.newSetFromMap(new IdentityHashMap<GasReaction, Boolean>());
			registeredGasReactions.put(gasType, gasReactions);
		}
		gasReactions.add(reaction);
	}

	private void registerBlockReaction(BlockReaction reaction, GasType gasType)
	{
		Set<BlockReaction> blockReactions = registeredBlockReactions.get(gasType);
		if (blockReactions == null)
		{
			blockReactions = Collections.newSetFromMap(new IdentityHashMap<BlockReaction, Boolean>());
			registeredBlockReactions.put(gasType, blockReactions);
		}
		blockReactions.add(reaction);
	}

	private void registerEntityReaction(EntityReaction reaction, GasType gasType)
	{
		Set<EntityReaction> entityReactions = registeredEntityReactions.get(gasType);
		if (entityReactions == null)
		{
			entityReactions = Collections.newSetFromMap(new IdentityHashMap<EntityReaction, Boolean>());
			registeredEntityReactions.put(gasType, entityReactions);
		}
		entityReactions.add(reaction);
	}
	@Override
	public boolean isReactionRegistered(Reaction reaction, GasType gasType)
	{
		Set<Reaction> reactions = registeredReactions.get(gasType);
		if (reactions != null)
		{
			return reactions.contains(reaction);
		}
		return false;
	}

	@Override
	public Reaction[] getRegisteredReactions(GasType gasType)
	{
		Set<Reaction> reactionsSet = registeredReactions.get(gasType);
		if (reactionsSet != null)
		{
			Reaction[] reactions = new Reaction[reactionsSet.size()];
			reactionsSet.toArray(reactions);
			return reactions;
		}

		return new Reaction[0];
	}

	public GasReaction[] getRegisteredGasReactions(GasType gasType)
	{
		Set<GasReaction> reactionsSet = registeredGasReactions.get(gasType);
		if (reactionsSet != null)
		{
			GasReaction[] reactions = new GasReaction[reactionsSet.size()];
			reactionsSet.toArray(reactions);
			return reactions;
		}

		return new GasReaction[0];
	}

	public BlockReaction[] getRegisteredBlockReactions(GasType gasType)
	{
		Set<BlockReaction> reactionsSet = registeredBlockReactions.get(gasType);
		if (reactionsSet != null)
		{
			BlockReaction[] reactions = new BlockReaction[reactionsSet.size()];
			reactionsSet.toArray(reactions);
			return reactions;
		}

		return new BlockReaction[0];
	}

	public EntityReaction[] getRegisteredEntityReactions(GasType gasType)
	{
		Set<EntityReaction> reactionsSet = registeredEntityReactions.get(gasType);
		if (reactionsSet != null)
		{
			EntityReaction[] reactions = new EntityReaction[reactionsSet.size()];
			reactionsSet.toArray(reactions);
			return reactions;
		}

		return new EntityReaction[0];
	}



	private final Set<GasType> registeredGasTypes = Collections.newSetFromMap(new IdentityHashMap<GasType, Boolean>());
	private final GasType[] gasTypesByID = new GasType[256];
	private final Map<String, GasType> gasTypesByName = new HashMap<String, GasType>();

	private final Map<GasType, BlockGas> gasTypeGasBlocks = new IdentityHashMap<GasType, BlockGas>();
	private final Map<GasType, BlockGasPipe> gasTypeGasPipeBlocks = new IdentityHashMap<GasType, BlockGasPipe>();

	@Override
	public GasType getGasTypeByID(int id)
	{
		if (id >= 0 && id < gasTypesByID.length)
		{
			return gasTypesByID[id];
		}
		else
		{
			return null;
		}
	}

	@Override
	public GasType getGasTypeByName(String name)
	{
		return gasTypesByName.get(name);
	}

	@Override
	public void registerGasType(GasType type)
	{
		if(isGasTypeRegistered(type))
		{
			throw new RuntimeException("Gas type named " + type.name + " was attempted registered while it was already registered.");
		}
		else if(getGasTypeByName(type.name) != null)
		{
			throw new RuntimeException("Gas type named " + type.name + " has a name conflict with another gas type.");
		}
		else if(getGasTypeByID(type.gasID) != null)
		{
			throw new RuntimeException("Gas type named " + type.name + " has an ID conflict with gas type named " + getGasTypeByID(type.gasID).name + ".");
		}

		if(type != GasesFrameworkAPI.gasTypeAir)
		{
			BlockGas gasBlock = (BlockGas)GameRegistry.registerBlock(new BlockGas(type), "gas_" + type.name);
			gasTypeGasBlocks.put(type, gasBlock);

			if(type.combustibility.fireSpreadRate >= 0 | type.combustibility.explosionPower > 0.0F)
			{
				Blocks.fire.setFireInfo(gasBlock, 1000, 1000);
			}
		}
		if(type.isIndustrial)
		{
			BlockGasPipe gasPipeBlock = (BlockGasPipe)GameRegistry.registerBlock(new BlockGasPipe(type), ItemGasPipe.class, "gasPipe_" + type.name);
			gasTypeGasPipeBlocks.put(type, gasPipeBlock);

			LanternType lanternType = GasesFramework.lanternTypesGas[type.combustibility.burnRate];
			if(lanternType != GasesFramework.lanternTypeGasEmpty)
			{
				registerLanternRecipe(lanternType, new ItemKey(GasesFramework.items.gasBottle, type.gasID));
			}
		}

		registeredGasTypes.add(type);
		gasTypesByID[type.gasID] = type;
		gasTypesByName.put(type.name, type);
	}

	@Override
	public boolean isGasTypeRegistered(GasType type)
	{
		return registeredGasTypes.contains(type);
	}

	@Override
	public GasType[] getRegisteredGasTypes()
	{
		GasType[] gasTypes = new GasType[registeredGasTypes.size()];
		registeredGasTypes.toArray(gasTypes);
		return gasTypes;
	}

	public BlockGas getGasBlock(GasType type)
	{
		return gasTypeGasBlocks.get(type);
	}

	public BlockGasPipe getGasPipeBlock(GasType type)
	{
		return gasTypeGasPipeBlocks.get(type);
	}



	private final Set<LanternType> registeredLanternTypes = Collections.newSetFromMap(new IdentityHashMap<LanternType, Boolean>());
	private final Map<String, LanternType> lanternTypesByName = new HashMap<String, LanternType>();
	private final Map<ItemKey, LanternType> lanternRecipes = new HashMap<ItemKey, LanternType>();

	private final Map<LanternType, BlockLantern> lanternTypeLanternBlocks = new IdentityHashMap<LanternType, BlockLantern>();

	@Override
	public LanternType getLanternTypeByName(String name)
	{
		return lanternTypesByName.get(name);
	}

	@Override
	public LanternType getLanternTypeByItemIn(ItemKey itemKey)
	{
		return lanternRecipes.get(itemKey);
	}

	@Override
	public void registerLanternType(LanternType type)
	{
		if(isLanternTypeRegistered(type))
		{
			throw new RuntimeException("Lantern type named " + type.name + " was attempted registered while it was already registered.");
		}
		else if(getLanternTypeByName(type.name) != null)
		{
			throw new RuntimeException("Lantern type named " + type.name + " has a name conflict with another lantern type.");
		}

		BlockLantern lanternBlock = (BlockLantern)GameRegistry.registerBlock(new BlockLantern(type), "lantern_" + type.name);

		lanternTypeLanternBlocks.put(type, lanternBlock);

		registeredLanternTypes.add(type);
		lanternTypesByName.put(type.name, type);
	}

	@Override
	public void registerLanternRecipe(LanternType type, ItemKey itemKey)
	{
		lanternRecipes.put(itemKey, type);
		GameRegistry.addShapelessRecipe(new ItemStack(getLanternBlock(type)), new ItemStack(getLanternBlock(GasesFramework.lanternTypeEmpty)), new ItemStack(itemKey.item, 1, itemKey.damage));
	}

	@Override
	public boolean isLanternTypeRegistered(LanternType type)
	{
		return registeredLanternTypes.contains(type);
	}

	@Override
	public LanternType[] getRegisteredLanternTypes()
	{
		LanternType[] lanternTypes = new LanternType[registeredLanternTypes.size()];
		registeredLanternTypes.toArray(lanternTypes);
		return lanternTypes;
	}

	public BlockLantern getLanternBlock(LanternType type)
	{
		return lanternTypeLanternBlocks.get(type);
	}



	@Override
	public void registerGasWorldGenType(GasWorldGenType type, String... dimensions)
	{
		for (String dimension : dimensions)
		{
			GasesFramework.worldGenerator.registerGasWorldGenType(type, dimension);
		}
	}

	@Override
	public boolean isGasWorldGenTypeRegistered(GasWorldGenType type, String dimension)
	{
		return GasesFramework.worldGenerator.isGasWorldGenTypeRegistered(type, dimension);
	}



	private final Set<IGasTransposerHandler> registeredGasTransposerHandlers = Collections.newSetFromMap(new IdentityHashMap<IGasTransposerHandler, Boolean>());

	@Override
	public void registerGasTransposerHandler(IGasTransposerHandler handler)
	{
		if (isGasTransposerHandlerRegistered(handler))
		{
			throw new RuntimeException("A gas transposer environment was attempted registered while it was already registered.");
		}

		TileEntityGasTransposer.registerHandler(handler);

		registeredGasTransposerHandlers.add(handler);
	}

	@Override
	public boolean isGasTransposerHandlerRegistered(IGasTransposerHandler handler)
	{
		return registeredGasTransposerHandlers.contains(handler);
	}



	private final Set<PipeType> registeredPipeTypes = Collections.newSetFromMap(new IdentityHashMap<PipeType, Boolean>());
	private final PipeType[] pipeTypesByID = new PipeType[16];
	private final Map<String, PipeType> pipeTypesByName = new HashMap<String, PipeType>();

	@Override
	public PipeType getPipeTypeByID(int id)
	{
		if (id >= 0 && id < pipeTypesByID.length)
		{
			return pipeTypesByID[id];
		}
		else
		{
			return null;
		}
	}

	@Override
	public PipeType getPipeTypeByName(String name)
	{
		return pipeTypesByName.get(name);
	}

	@Override
	public void registerPipeType(PipeType type)
	{
		if (isPipeTypeRegistered(type))
		{
			throw new RuntimeException("A pipe type named " + type.name + " was attempted registered while it was already registered.");
		}
		else if(getPipeTypeByName(type.name) != null)
		{
			throw new RuntimeException("Gas type named " + type.name + " has a name conflict with another gas type.");
		}
		else if (getPipeTypeByID(type.pipeID) != null)
		{
			throw new RuntimeException("Gas type named " + type.name + " has an ID conflict with gas type named " + getPipeTypeByID(
					type.pipeID).name + ".");
		}

		registeredPipeTypes.add(type);
		pipeTypesByID[type.pipeID] = type;
		pipeTypesByName.put(type.name, type);
	}

	@Override
	public boolean isPipeTypeRegistered(PipeType type)
	{
		return registeredPipeTypes.contains(type);
	}

	@Override
	public PipeType[] getRegisteredPipeTypes()
	{
		PipeType[] lanternTypes = new PipeType[registeredPipeTypes.size()];
		registeredPipeTypes.toArray(lanternTypes);
		return lanternTypes;
	}
}
