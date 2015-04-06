package glenn.gasesframework.api.mechanical;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;

/**
 * Handles the insertion operation of a gas transposer.
 * Must be registered through {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasTransposerHandler(IGasTransposerHandler) registerGasTransposerHandler(IGasTransposerHandler)}.
 * @author Glenn
 *
 */
public interface IGasTransposerInsertHandler extends IGasTransposerHandler
{
	/**
	 * Is this a valid item to insert gas into?
	 * @param inputStack - The itemstack in the input slot. Should not be modified.
	 * @return
	 */
	boolean isValidInputItemStack(ItemStack inputStack);
	
	/**
	 * Is this a valid gas to insert into this item?
	 * @param inputStack - The itemstack in the input slot. Should not be modified.
	 * @param gasType - The gas type that will be inserted into the item.
	 * @return
	 */
	boolean isValidInputGasType(ItemStack inputStack, GasType gasType);
	
	/**
	 * Can insertion be completed with the current state of the gas transposer?
	 * Commonly used to determine if the input item can be extracted from the slot and the output item can be placed in the slot.
	 * If false is returned, the insertion will be delayed until true is returned.
	 * @param inputStack - The itemstack in the input slot.
	 * @param outputStack - The itemstack in the output slot.
	 * @param gasType - The gas type that will be inserted into the item.
	 * @return
	 */
	boolean completeInsertion(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * If {@link #completeInsertion(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to extract the input item from the slot.
	 * @param inputStack - The itemstack that already exists in the input slot.
	 * @param gasType - The gas type that will be inserted into the item.
	 * @return The modified itemstack to be placed in the input slot.
	 */
	ItemStack getInsertionInputStack(ItemStack inputStack, GasType gasType);
	
	/**
	 * If {@link #completeInsertion(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to extract the input item from the slot.
	 * @param inputStack - The itemstack that already exists in the input slot.
	 * @param gasType - The gas type that will be inserted into the item.
	 * @return The modified itemstack to be placed in the input slot.
	 */
	ItemStack getInsertionOutputStack(ItemStack outputStack, GasType gasType);
	
	/**
	 * Get the amount of ticks required to complete this insertion. Commonly set to 20.
	 * @return
	 */
	int getInsertionTime();
}