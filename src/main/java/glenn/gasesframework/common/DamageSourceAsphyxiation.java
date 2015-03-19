package glenn.gasesframework.common;

import net.minecraft.util.DamageSource;

public class DamageSourceAsphyxiation extends DamageSource
{
	public DamageSourceAsphyxiation(String par1Str)
	{
		super(par1Str);
		this.setDamageBypassesArmor();
	}
}