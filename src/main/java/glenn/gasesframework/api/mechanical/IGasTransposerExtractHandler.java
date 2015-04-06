package glenn.gasesframework.api.mechanical;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;

/**
 * Handles the extraction operation of a gas transposer.
 * Must be registered through {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasTransposerHandler(IGasTransposerHandler) registerGasTransposerHandler(IGasTransposerHandler)}.
 * @author Glenn
 *
 */
public interface IGasTransposerExtractHandler extends IGasTransposerHandler
{
	/**
	 * Get the gas type to be outputted by an item. If this handler should not apply, always return null.
	 * @param inputStack - The itemstack in the input slot. Should not be modified.
	 * @return
	 */
	GasType getOutputGasType(ItemStack inputStack);
	
	/**
	 * Can extraction be completed with the current state of the gas transposer?
	 * Commonly used to determine if the input item can be extracted from the slot and the output item can be placed in the slot.
	 * If false is returned, the extraction will be delayed until true is returned.
	 * @param inputStack - The itemstack in the input slot.
	 * @param outputStack - The itemstack in the output slot.
	 * @param gasType - Taken from {@link #getOutputGasType(ItemStack)}
	 * @return
	 */
	boolean completeExtraction(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * If {@link #completeExtraction(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to extract the input item from the slot.
	 * @param inputStack - The itemstack that already exists in the input slot.
	 * @param gasType - Taken from {@link #getOutputGasType(ItemStack)}.
	 * @return The modified itemstack to be placed in the input slot.
	 */
	ItemStack getExtractionInputStack(ItemStack inputStack, GasType gasType);
	
	/**
	 * If {@link #completeExtraction(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to insert the output item in the slot.
	 * @param outputStack - The itemstack that already exists in the output slot.
	 * @param gasType - Taken from {@link #getOutputGasType(ItemStack)}.
	 * @return The modified itemstack to be placed in the output slot.
	 */
	ItemStack getExtractionOutputStack(ItemStack outputStack, GasType gasType);
	
	/**
	 * Get the amount of ticks required to complete this extraction. Commonly set to 20.
	 * @return
	 */
	int getExtractionTime();
}