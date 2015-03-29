package glenn.gasesframework.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * A class containing the variables for gas effects per EntityLivingBase.
 * This includes blindness, suffocation and slowness.
 * @author Glenn
 */
public abstract class ExtendedGasEffectsBase implements IExtendedEntityProperties
{
	public static final String EXT_PROP_NAME = "ExtendedGasEffects";
	
	public static final int BLINDNESS_CAP = 0;
	public static final int SUFFOCATION_CAP = 1;
	public static final int SLOWNESS_CAP = 2;
	
	public final EntityLivingBase entity;
	
	protected ExtendedGasEffectsBase(EntityLivingBase entity)
	{
		this.entity = entity;
	}
	
	public abstract int get(int watchObject);
	
	public abstract int set(int watchObject, int i);
	
	public abstract int increment(int watchObject, int i);
	
	public static ExtendedGasEffectsBase get(EntityLivingBase entity)
	{
		return (ExtendedGasEffectsBase)entity.getExtendedProperties(EXT_PROP_NAME);
	}
}