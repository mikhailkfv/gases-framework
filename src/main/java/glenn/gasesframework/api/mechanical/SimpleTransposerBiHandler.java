package glenn.gasesframework.api.mechanical;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;

public class SimpleTransposerBiHandler implements IGasTransposerExtractHandler, IGasTransposerInsertHandler
{
	private final ItemStack insertable;
	private final ItemStack extractable;
	private final GasType gasType;
	private final int time;

	/**
	 * Create a simple two-way gas transposer recipe.
	 * 
	 * @param insertable
	 *            - The item gas can be inserted into.
	 * @param extractable
	 *            - The item gas can be extracted from.
	 * @param gasType
	 *            - The gas type that can be inserted into {@link #insertable}
	 *            and extracted from {@link #extractable}.
	 * @param time
	 *            - The time it takes to insert or extract.
	 */
	public SimpleTransposerBiHandler(ItemStack insertable, ItemStack extractable, GasType gasType, int time)
	{
		this.insertable = insertable;
		this.extractable = extractable;
		this.gasType = gasType;
		this.time = time;
	}

	@Override
	public boolean isValidInsertionInput(ItemStack itemstack)
	{
		return insertable.isItemEqual(itemstack);
	}

	@Override
	public boolean isValidInsertionInput(ItemStack inputStack, GasType gasType)
	{
		return insertable.stackSize <= inputStack.stackSize && this.gasType == gasType;
	}

	@Override
	public boolean completeInsertion(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		return outputStack == null || (outputStack.isItemEqual(extractable) && outputStack.stackSize + extractable.stackSize <= outputStack.getMaxStackSize());
	}

	@Override
	public ItemStack getInsertionInputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		return (inputStack.stackSize -= insertable.stackSize) > 0 ? inputStack : null;
	}

	@Override
	public ItemStack getInsertionOutputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		if (outputStack == null)
		{
			return extractable.copy();
		}
		else
		{
			outputStack.stackSize += extractable.stackSize;
			return outputStack;
		}
	}

	@Override
	public int getInsertionTime()
	{
		return time;
	}

	@Override
	public boolean isValidExtractionInput(ItemStack itemstack)
	{
		return extractable.isItemEqual(itemstack);
	}

	@Override
	public GasType getExtractionOutputGasType(ItemStack inputStack)
	{
		if (extractable.stackSize <= inputStack.stackSize)
		{
			return gasType;
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean completeExtraction(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		return outputStack == null || (outputStack.isItemEqual(insertable) && outputStack.stackSize + insertable.stackSize <= outputStack.getMaxStackSize());
	}

	@Override
	public ItemStack getExtractionInputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		return (inputStack.stackSize -= extractable.stackSize) > 0 ? inputStack : null;
	}

	@Override
	public ItemStack getExtractionOutputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		if (outputStack == null)
		{
			return insertable.copy();
		}
		else
		{
			outputStack.stackSize += insertable.stackSize;
			return outputStack;
		}
	}

	@Override
	public int getExtractionTime()
	{
		return time;
	}
}