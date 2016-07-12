package glenn.gasesframework.api.gastype;

import glenn.gasesframework.api.Combustibility;
import net.minecraft.entity.EntityLivingBase;

public class GasTypeAir extends GasType
{
	public GasTypeAir()
	{
		super(true, 0, "air", 0, 0, 0, Combustibility.NONE);
	}

	@Override
	public boolean isVisible()
	{
		return false;
	}

	@Override
	public void onBreathed(EntityLivingBase entity)
	{
	}

	@Override
	public boolean canBeDestroyedBy(int thisVolume, GasType type, int otherVolume)
	{
		return true;
	}
}