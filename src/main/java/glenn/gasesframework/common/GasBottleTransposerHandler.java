package glenn.gasesframework.common;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.mechanical.IGasTransposerExtractHandler;
import glenn.gasesframework.api.mechanical.IGasTransposerInsertHandler;
import glenn.gasesframework.common.item.ItemGasBottle;

public class GasBottleTransposerHandler implements IGasTransposerInsertHandler, IGasTransposerExtractHandler
{
	@Override
	public GasType getOutputGasType(ItemStack inputStack)
	{
		if(inputStack.getItem() == GasesFrameworkAPI.gasBottle)
		{
			return ((ItemGasBottle)GasesFrameworkAPI.gasBottle).getGasType(inputStack);
		}
		return null;
	}

	@Override
	public boolean isValidInputItemStack(ItemStack inputStack)
	{
		return inputStack.getItem() == Items.glass_bottle;
	}

	@Override
	public boolean isValidInputGasType(GasType gasType)
	{
		return gasType != null && gasType != GasesFrameworkAPI.gasTypeAir;
	}

	@Override
	public int getExtractionTime()
	{
		return 20;
	}

	@Override
	public int getInsertionTime()
	{
		return 20;
	}

	@Override
	public boolean completeExtraction(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		ItemStack result = new ItemStack(Items.glass_bottle);
		
		if(outputStack == null)
		{
			return true;
		}
		else 
		{
			return outputStack.isItemEqual(result) && outputStack.stackSize + result.stackSize <= outputStack.getMaxStackSize();
		}
	}

	@Override
	public ItemStack getExtractionInputStack(ItemStack inputStack, GasType gasType)
	{
		return --inputStack.stackSize > 0 ? inputStack : null;
	}

	@Override
	public ItemStack getExtractionOutputStack(ItemStack outputStack, GasType gasType)
	{
		ItemStack result = new ItemStack(Items.glass_bottle);
		
		if(outputStack == null)
		{
			return result;
		}
		else
		{
			outputStack.stackSize += result.stackSize;
			return outputStack;
		}
	}

	@Override
	public boolean completeInsertion(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		ItemStack result = new ItemStack(GasesFrameworkAPI.gasBottle, 1, gasType.gasID);
		
		if(outputStack == null)
		{
			return true;
		}
		else 
		{
			return outputStack.isItemEqual(result) && outputStack.stackSize + result.stackSize <= outputStack.getMaxStackSize();
		}
	}

	@Override
	public ItemStack getInsertionInputStack(ItemStack inputStack, GasType gasType)
	{
		return --inputStack.stackSize > 0 ? inputStack : null;
	}

	@Override
	public ItemStack getInsertionOutputStack(ItemStack outputStack, GasType gasType)
	{
		ItemStack result = new ItemStack(GasesFrameworkAPI.gasBottle, 1, gasType.gasID);
		
		if(outputStack == null)
		{
			return result;
		}
		else
		{
			outputStack.stackSize += result.stackSize;
			return outputStack;
		}
	}
}