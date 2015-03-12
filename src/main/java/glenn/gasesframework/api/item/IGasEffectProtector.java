package glenn.gasesframework.api.item;

import glenn.gasesframework.api.gastype.GasType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IGasEffectProtector
{
	public boolean protectVision(EntityLivingBase entity, ItemStack stack, GasType type);
	public boolean protectBreath(EntityLivingBase entity, ItemStack stack, GasType type);
}