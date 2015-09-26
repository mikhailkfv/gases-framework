package glenn.gasesframework.api.mechanical;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.item.ItemStack;

/**
 * Handles the insertion operation of a gas transposer.
 * Must be registered through {@link glenn.gasesframework.api.IGasesFrameworkRegistry#registerGasTransposerHandler(IGasTransposerHandler) registerGasTransposerHandler(IGasTransposerHandler)}.
 */
public interface IGasTransposerInsertHandler extends IGasTransposerHandler
{
	/**
	 * Is this a valid item to insert gas into? Must not check the size of the itemstack.
	 * @param itemstack The itemstack in the input slot. Should not be modified
	 * @return True if the handler can insert something into this itemstack
	 */
	boolean isValidInsertionInput(ItemStack itemstack);
	
	/**
	 * Is this a valid gas to insert into this item? Can check the size of the itemstack.
	 * This will only be called if {@link #isValidInsertionInput(ItemStack)} returns true.
	 * @param inputStack The itemstack in the input slot. Should not be modified
	 * @param gasType The gas type that will be inserted into the item
	 * @return True if the handler can insert this gas type into this itemstack
	 */
	boolean isValidInsertionInput(ItemStack inputStack, GasType gasType);
	
	/**
	 * Can insertion be completed with the current state of the gas transposer?
	 * Commonly used to determine if the output item can be placed in the slot.
	 * If false is returned, the insertion will be delayed until true is returned.
	 * @param inputStack The itemstack in the input slot
	 * @param outputStack The itemstack in the output slot
	 * @param gasType The gas type that will be inserted into the item
	 * @return True if the insertion can be completed
	 */
	boolean completeInsertion(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * If {@link #completeInsertion(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to extract the input item from the slot.
	 * @param inputStack The itemstack that already exists in the input slot
	 * @param outputStack The itemstack that already exists in the output slot
	 * @param gasType The gas type that will be inserted into the item
	 * @return The modified itemstack to be placed in the input slot.
	 */
	ItemStack getInsertionInputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * If {@link #completeInsertion(ItemStack, ItemStack, GasType)} returns true, this method is called.
	 * Use this method to insert the output item in the slot.
	 * @param inputStack - The itemstack that already exists in the input slot.
	 * @param outputStack - The itemstack that already exists in the output slot.
	 * @param gasType - The gas type that will be inserted into the item.
	 * @return The modified itemstack to be placed in the input slot.
	 */
	ItemStack getInsertionOutputStack(ItemStack inputStack, ItemStack outputStack, GasType gasType);
	
	/**
	 * Get the amount of ticks required to complete this insertion. Commonly set to 20.
	 * @return The amount of ticks required to complete this insertion
	 */
	int getInsertionTime();
}