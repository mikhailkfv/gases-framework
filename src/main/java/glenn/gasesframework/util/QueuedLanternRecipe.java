package glenn.gasesframework.util;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.block.BlockLantern;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class QueuedLanternRecipe
{
	public BlockLantern result;
	public ItemStack ingredient;
	
	public QueuedLanternRecipe(BlockLantern result, ItemStack ingredient)
	{
		this.result = result;
		this.ingredient = ingredient;
	}
	
	public void register()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(result), new ItemStack(GasesFramework.lanternEmpty), ingredient);
	}
}