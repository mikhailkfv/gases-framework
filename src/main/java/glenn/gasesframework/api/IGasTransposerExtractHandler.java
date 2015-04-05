package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;

public interface IGasTransposerExtractHandler extends IGasTransposerHandler
{
	GasType getOutputGasType(ItemStack inputStack);
	boolean completeExtraction(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	ItemStack getExtractionInputStack(ItemStack inputStack, GasType gasType);
	ItemStack getExtractionOutputStack(ItemStack outputStack, GasType gasType);
	int getExtractionTime();
}