package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.GFAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.mechanical.IGasTransposerExtractHandler;
import glenn.gasesframework.api.mechanical.IGasTransposerInsertHandler;
import glenn.gasesframework.common.item.ItemGasBottle;

public class GasBottleTransposerHandler implements IGasTransposerInsertHandler, IGasTransposerExtractHandler
{
	@Override
	public boolean isValidInsertionInput(ItemStack itemstack)
	{
		return itemstack.getItem() == Items.glass_bottle;
	}

	@Override
	public boolean isValidInsertionInput(ItemStack inputStack, GasType gasType)
	{
		return gasType != null && gasType != GFAPI.gasTypeAir;
	}

	@Override
	public boolean completeInsertion(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		ItemStack result = new ItemStack(GasesFramework.items.gasBottle, 1, gasType.gasID);
		
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
	public ItemStack getInsertionInputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		return --inputStack.stackSize > 0 ? inputStack : null;
	}

	@Override
	public ItemStack getInsertionOutputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		ItemStack result = new ItemStack(GasesFramework.items.gasBottle, 1, gasType.gasID);
		
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
	public int getInsertionTime()
	{
		return 20;
	}
	
	@Override
	public boolean isValidExtractionInput(ItemStack itemstack)
	{
		return itemstack.getItem() == GasesFramework.items.gasBottle;
	}

	@Override
	public GasType getExtractionOutputGasType(ItemStack inputStack)
	{
		return ((ItemGasBottle)GasesFramework.items.gasBottle).getGasType(inputStack);
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
	public ItemStack getExtractionInputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
	{
		return --inputStack.stackSize > 0 ? inputStack : null;
	}

	@Override
	public ItemStack getExtractionOutputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType)
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
	public int getExtractionTime()
	{
		return 20;
	}
}