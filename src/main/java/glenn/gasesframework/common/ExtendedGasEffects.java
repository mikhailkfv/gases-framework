package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.gasesframework.api.MaterialGas;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.item.IGasEffectProtector;
import glenn.gasesframework.common.block.BlockGas;
import glenn.gasesframework.network.message.MessageGasEffects;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class ExtendedGasEffects extends ExtendedGasEffectsBase
{
	private EnumMap<EffectType, Integer> prevValues = new EnumMap<EffectType, Integer>(EffectType.class);
	private EnumMap<EffectType, Integer> values = new EnumMap<EffectType, Integer>(EffectType.class);

	private ExtendedGasEffects(EntityLivingBase entity)
	{
		super(entity);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();

		for (Map.Entry<EffectType, Integer> entry : values.entrySet())
		{
			properties.setInteger(entry.getKey().name(), entry.getValue());
		}

		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(EXT_PROP_NAME);

		Set<String> keys = properties.func_150296_c();
		for (String key : keys)
		{
			try
			{
				EffectType type = EffectType.valueOf(key);
				if (type != null)
				{
					set(type, properties.getInteger(key));
				}
			} catch (IllegalArgumentException e)
			{

			}
		}
	}

	@Override
	public void init(Entity entity, World world)
	{
	}

	@Override
	public void sendMessage()
	{
		GasesFramework.networkWrapper.sendToAllAround(new MessageGasEffects(entity, (short) get(EffectType.BLINDNESS), (short) get(EffectType.SUFFOCATION), (short) get(EffectType.SUFFOCATION)), new TargetPoint(entity.worldObj.provider.dimensionId, entity.posX, entity.posY, entity.posZ, 50.0D));
	}

	private boolean protect(boolean[] appliedProtectors, IGasEffectProtector[] protectors, GasType gasType, EffectType effect)
	{
		boolean res = false;
		for (int slot = 0; slot < protectors.length; slot++)
		{
			IGasEffectProtector protector = protectors[slot];
			if (protector != null && protector.protect(entity, entity.getEquipmentInSlot(slot), slot, gasType, effect))
			{
				res = true;
				appliedProtectors[slot] = true;
			}
		}
		return res;
	}

	public void tick()
	{
		if (!entity.worldObj.isRemote)
		{
			boolean[] appliedProtectors = new boolean[5];
			IGasEffectProtector[] protectors = new IGasEffectProtector[5];
			for (int slot = 0; slot < 5; slot++)
			{
				ItemStack itemstack = entity.getEquipmentInSlot(slot);
				if (itemstack != null && itemstack.getItem() instanceof IGasEffectProtector)
				{
					IGasEffectProtector protector = (IGasEffectProtector) itemstack.getItem();
					protectors[slot] = protector;
				}
			}

			GasType gasType = null;

			if (entity.isInsideOfMaterial(MaterialGas.INSTANCE))
			{
				double dy = entity.posY + (double) entity.getEyeHeight();
				int x = MathHelper.floor_double(entity.posX);
				int y = MathHelper.floor_double(dy);
				int z = MathHelper.floor_double(entity.posZ);

				Block block = entity.worldObj.getBlock(x, y, z);
				if (block instanceof BlockGas)
				{
					gasType = ((BlockGas) block).type;
				}
			}

			int blindnessTimer = get(EffectType.BLINDNESS);
			int suffocationTimer = get(EffectType.SUFFOCATION);
			int slownessTimer = get(EffectType.SLOWNESS);

			if (gasType != null && gasType.getEffectRate(EffectType.BLINDNESS) > 0 && !protect(appliedProtectors, protectors, gasType, EffectType.BLINDNESS))
			{
				blindnessTimer += gasType.getEffectRate(EffectType.BLINDNESS) + 4;
			}
			blindnessTimer -= 4;

			if (gasType != null && gasType.getEffectRate(EffectType.SUFFOCATION) > 0 && !protect(appliedProtectors, protectors, gasType, EffectType.SUFFOCATION))
			{
				suffocationTimer += gasType.getEffectRate(EffectType.SUFFOCATION) + 32;

				if (suffocationTimer > 350 && gasType.getEffectRate(EffectType.SLOWNESS) > 0 && !protect(appliedProtectors, protectors, gasType, EffectType.SLOWNESS))
				{
					slownessTimer += gasType.getEffectRate(EffectType.SLOWNESS) + 10;
				}

				if (suffocationTimer > 400)
				{
					gasType.onBreathed(entity);
					suffocationTimer -= 32;
				}
			}
			suffocationTimer -= 32;
			slownessTimer -= 10;

			if (blindnessTimer > 500)
				blindnessTimer = 500;
			else if (blindnessTimer < 0)
				blindnessTimer = 0;

			if (suffocationTimer < 0)
				suffocationTimer = 0;

			if (slownessTimer > 1000)
				slownessTimer = 1000;
			else if (slownessTimer < 0)
				slownessTimer = 0;

			set(EffectType.BLINDNESS, blindnessTimer);
			set(EffectType.SUFFOCATION, suffocationTimer);
			set(EffectType.SLOWNESS, slownessTimer);

			for (int slot = 0; slot < protectors.length; slot++)
			{
				if (appliedProtectors[slot])
				{
					entity.setCurrentItemOrArmor(slot, protectors[slot].getItemstackOnProtect(entity, entity.getEquipmentInSlot(slot), slot, gasType));
				}
			}

			if (hasChanged())
			{
				sendMessage();
				prevValues = values.clone();
			}
		}
	}

	private boolean hasChanged()
	{
		if (values.size() != prevValues.size())
			return true;

		for (Map.Entry<EffectType, Integer> entry : values.entrySet())
		{
			Integer value = entry.getValue();
			Integer prevValue = prevValues.get(entry.getKey());
			if (value != null && prevValue != null)
			{
				if (!value.equals(prevValue))
					return true;
			}
			else if (value != null || prevValue != null)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public int get(EffectType effectType)
	{
		Integer res = values.get(effectType);
		if (res != null)
		{
			return res;
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int set(EffectType effectType, int i)
	{
		values.put(effectType, Integer.valueOf(i));
		return i;
	}

	@Override
	public int increment(EffectType effectType, int i)
	{
		return set(effectType, get(effectType) + i);
	}

	public static void register(EntityLivingBase entity)
	{
		entity.registerExtendedProperties(EXT_PROP_NAME, new ExtendedGasEffects(entity));
	}
}