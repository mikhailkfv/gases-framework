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
 * A dummy registry for the Gases Framework that is used when the mod is not
 * installed.
 * 
 * @author Erlend
 */
public class DummyRegistry implements IGFRegistry
{
	@Override
	public void registerGasFurnaceRecipe(ItemStack ingredient, ItemStack result, int time, int exp)
	{
	}

	@Override
	public boolean isIgnitionBlock(Block block)
	{
		return false;
	}

	@Override
	public void registerIgnitionBlock(Block block)
	{
	}

	@Override
	public void unregisterIgnitionBlock(Block block)
	{
	}

	@Override
	public Block[] getRegisteredIgnitionBlocks()
	{
		return new Block[0];
	}

	@Override
	public boolean isIgnitionItem(Item item)
	{
		return false;
	}

	@Override
	public void registerIgnitionItem(Item item)
	{
	}

	@Override
	public void unregisterIgnitionItem(Item item)
	{
	}

	@Override
	public Item[] getRegisteredIgnitionItems()
	{
		return new Item[0];
	}

	@Override
	public void registerReaction(Reaction reaction, GasType... gasTypes)
	{
	}

	@Override
	public boolean isReactionRegistered(Reaction reaction, GasType gasType)
	{
		return false;
	}

	@Override
	public Reaction[] getRegisteredReactions(GasType gasType)
	{
		return new Reaction[0];
	}

	@Override
	public GasType getGasTypeByID(int id)
	{
		return null;
	}

	@Override
	public GasType getGasTypeByName(String name)
	{
		return null;
	}

	@Override
	public void registerGasType(GasType type)
	{
	}

	@Override
	public boolean isGasTypeRegistered(GasType type)
	{
		return false;
	}

	@Override
	public GasType[] getRegisteredGasTypes()
	{
		return new GasType[0];
	}

	@Override
	public LanternType getLanternTypeByName(String name)
	{
		return null;
	}

	@Override
	public LanternType getLanternTypeByInput(ItemKey itemKey)
	{
		return null;
	}

	@Override
	public void registerLanternType(LanternType type)
	{
	}

	@Override
	public boolean isLanternTypeRegistered(LanternType type)
	{
		return false;
	}

	@Override
	public void registerLanternInput(LanternType type, ItemKey itemKey)
	{
	}

	@Override
	public LanternType[] getRegisteredLanternTypes()
	{
		return new LanternType[0];
	}

	@Override
	public void registerGasWorldGenType(GasWorldGenType type, String... dimensions)
	{
	}

	@Override
	public boolean isGasWorldGenTypeRegistered(GasWorldGenType type, String dimension)
	{
		return false;
	}

	@Override
	public void registerGasTransposerHandler(IGasTransposerHandler handler)
	{
	}

	@Override
	public boolean isGasTransposerHandlerRegistered(IGasTransposerHandler handler)
	{
		return false;
	}

	@Override
	public PipeType getPipeTypeByID(int id)
	{
		return null;
	}

	@Override
	public PipeType getPipeTypeByName(String name)
	{
		return null;
	}

	@Override
	public void registerPipeType(PipeType type)
	{
	}

	@Override
	public boolean isPipeTypeRegistered(PipeType type)
	{
		return false;
	}

	@Override
	public PipeType[] getRegisteredPipeTypes()
	{
		return new PipeType[0];
	}
}
