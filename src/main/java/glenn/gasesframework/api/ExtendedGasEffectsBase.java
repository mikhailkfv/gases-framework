package glenn.gasesframework.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * A class containing the variables for gas effects per EntityLivingBase. This
 * includes blindness, suffocation and slowness.
 */
public abstract class ExtendedGasEffectsBase implements IExtendedEntityProperties
{
	public enum EffectType
	{
		/**
		 * This is a blindness effect much like the one already in Minecraft,
		 * except it is gradual.
		 */
		BLINDNESS,
		/**
		 * Whenever suffocation reaches a certain value, it drops to some value
		 * below and triggers the effects of breathing the gas.
		 */
		SUFFOCATION,
		/**
		 * This is a slowness effect much like the one already in Minecraft,
		 * except it is gradual. It is usually applied when the player starts
		 * suffocating in gas.
		 */
		SLOWNESS
	}

	public static final String EXT_PROP_NAME = "ExtendedGasEffects";

	public final EntityLivingBase entity;

	protected ExtendedGasEffectsBase(EntityLivingBase entity)
	{
		this.entity = entity;
	}

	/**
	 * Synchronize gas effects from server to client.
	 */
	public abstract void sendMessage();

	/**
	 * Get the amount of an effect type.
	 * 
	 * @param effectType
	 *            The gas effect type
	 * @return The amount of the effect
	 */
	public abstract int get(EffectType effectType);

	/**
	 * Set the amount of an effect type.
	 * 
	 * @param effectType
	 *            The gas effect type
	 * @param i
	 *            The amount of the effect
	 * @return The new amount of the effect. May be clamped
	 */
	public abstract int set(EffectType effectType, int i);

	/**
	 * Increment the amount of an effect type.
	 * 
	 * @param effectType
	 *            The gas effect type
	 * @param i
	 *            The amount to increment the effect by
	 * @return The new amount of the effect. May be clamped
	 */
	public abstract int increment(EffectType effectType, int i);

	/**
	 * Get the ExtendedGasEffectsBase instance of an entity. If the Gases
	 * Framework is not installed, this will return null.
	 * 
	 * @param entity
	 *            The entity holding the gas effects
	 * @return The ExtendedGasEffectsBase bound to the entity, or null
	 */
	public static ExtendedGasEffectsBase get(EntityLivingBase entity)
	{
		return (ExtendedGasEffectsBase) entity.getExtendedProperties(EXT_PROP_NAME);
	}
}