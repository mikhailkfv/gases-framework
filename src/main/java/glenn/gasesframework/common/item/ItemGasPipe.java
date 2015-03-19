package glenn.gasesframework.common.item;

import glenn.gasesframework.common.block.BlockGasPipe;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemGasPipe extends ItemBlockWithMetadata
{
	public ItemGasPipe(Block block)
	{
		super(block, block);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "tile.gf_gasPipe." + ((BlockGasPipe)Block.getBlockFromItem(itemstack.getItem())).subTypes[itemstack.getItemDamage()].name;
	}
}