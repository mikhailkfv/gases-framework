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
	 * Is this a valid item to extract gas from? Must not check the size of the itemstack.
	 * @param itemstack
	 * @return
	 */
	boolean isValidExtractionInput(ItemStack itemstack);
	
	/**
	 * Get the gas type to be outputted by an item. Return null if no gas type can be extracted.
	 * This will only be called if {@link #isValidExtractionInput(ItemStack)} returns true.
	 * @param inputStack - The itemstack in the input slot. Should not be modified.
	 * @return
	 */
	GasType getExtractionOutputGasType(ItemStack inputStack);
	
	/**
	 * Can extraction be completed with the current state of the gas transposer?
	 * Commonly used to determine if the output item can be placed in the slot.
	 * If false is returned, the extraction will be delayed until true is returned.
	 * @param inputStack - The itemstack in the input slot.
	 * @param outputStack - The itemstack in the output slot.
	 * @param gasType - Taken from {@link #getExtractionOutputGasType(ItemStack)}
	 * @return
	 */
	boolean completeExtraction(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * If {@link #completeExtraction(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to extract the input item from the slot.
	 * @param inputStack - The itemstack that already exists in the input slot.
	 * @param outputStack - The itemstack that already exists in the output slot.
	 * @param gasType - Taken from {@link #getExtractionOutputGasType(ItemStack)}.
	 * @return The modified itemstack to be placed in the input slot.
	 */
	ItemStack getExtractionInputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * If {@link #completeExtraction(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to insert the output item in the slot.
	 * @param inputStack - The itemstack that already exists in the input slot.
	 * @param outputStack - The itemstack that already exists in the output slot.
	 * @param gasType - Taken from {@link #getExtractionOutputGasType(ItemStack)}.
	 * @return The modified itemstack to be placed in the output slot.
	 */
	ItemStack getExtractionOutputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * Get the amount of ticks required to complete this extraction. Commonly set to 20.
	 * @return
	 */
	int getExtractionTime();
}