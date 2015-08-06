package glenn.gasesframework.common.item;

import glenn.gasesframework.api.pipetype.PipeType;
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
		PipeType type = ((BlockGasPipe)Block.getBlockFromItem(itemstack.getItem())).getPipeType(itemstack.getItemDamage());
		return "tile.gf_gasPipe." + type.name;
	}
}