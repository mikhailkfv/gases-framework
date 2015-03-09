package glenn.gasesframework.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockLanternEmpty extends BlockLantern
{
	public BlockLanternEmpty()
	{
		super(0);
	}
	
	@Override
	public Block getExpirationBlock()
	{
		return this;
	}

    @Override
	public ItemStack getContainedItemOut()
	{
		return null;
	}

    @Override
	public ItemStack getContainedItemIn()
	{
		return null;
	}
}