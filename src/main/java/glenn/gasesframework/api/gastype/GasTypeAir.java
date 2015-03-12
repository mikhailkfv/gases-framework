package glenn.gasesframework.api.gastype;

import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.GasesFrameworkAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class GasTypeAir extends GasType
{
	public GasTypeAir()
	{
		super(true, 0, "air", 0, 0, 0, Combustibility.NONE);
	}
	
	/**
	 * This method is called upon gas pipe block construction when the gas type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 * @return
	 */
	@Override
	public Block tweakPipeBlock(Block block)
	{
		block.setCreativeTab(GasesFrameworkAPI.creativeTab);
		return super.tweakPipeBlock(block);
	}
	
	/**
	 * Is this gas visible?
	 */
	@Override
	public boolean isVisible()
	{
		return false;
	}

	/**
	 * Get the bottled item. Is {@link GasesFramework#gasBottle} by default.
	 * @return
	 */
    @Override
	public ItemStack getBottledItem()
	{
		return new ItemStack(Items.glass_bottle);
	}

    /**
	 * Apply effects onto an entity when breathed. A gas is breathed when the player runs out of air in their hidden air meter.
	 * How quickly this happens, and how frequently this method is called depends on this gas type's {@link GasType#suffocationRate}.
	 * @param entity
	 */
    @Override
	public void onBreathed(EntityLivingBase entity)
	{
		
	}
}