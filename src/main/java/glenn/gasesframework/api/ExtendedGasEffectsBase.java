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
	public static enum EffectType
	{
		/**
		 * This is a blindness effect much like the one already in Minecraft, except it is gradual.
		 */
		BLINDNESS,
		/**
		 * Whenever suffocation reaches a certain value, it drops to some value below and damages the entity.
		 */
		SUFFOCATION,
		/**
		 * This is a slowness effect much like the one already in Minecraft, except it is gradual.
		 */
		SLOWNESS
	}
	
	public static final String EXT_PROP_NAME = "ExtendedGasEffects";
	
	public final EntityLivingBase entity;
	
	protected ExtendedGasEffectsBase(EntityLivingBase entity)
	{
		this.entity = entity;
	}
	
	public abstract int get(EffectType effectType);
	
	public abstract int set(EffectType effectType, int i);
	
	public abstract int increment(EffectType effectType, int i);
	
	public static ExtendedGasEffectsBase get(EntityLivingBase entity)
	{
		return (ExtendedGasEffectsBase)entity.getExtendedProperties(EXT_PROP_NAME);
	}
}