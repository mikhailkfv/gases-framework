package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;

public interface IGasTransposerInsertHandler extends IGasTransposerHandler
{
	boolean isValidInputItemStack(ItemStack inputStack);
	boolean isValidInputGasType(GasType gasType);
	boolean completeInsertion(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	ItemStack getInsertionInputStack(ItemStack inputStack, GasType gasType);
	ItemStack getInsertionOutputStack(ItemStack outputStack, GasType gasType);
	int getInsertionTime();
}