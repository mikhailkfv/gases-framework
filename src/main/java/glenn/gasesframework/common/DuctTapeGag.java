package glenn.gasesframework.common;

import cpw.mods.fml.common.network.NetworkRegistry;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.network.message.MessageDuctTapeGag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class DuctTapeGag implements IExtendedEntityProperties
{
	public static final String EXT_PROP_NAME = "DuctTapeGag";

	public final EntityLivingBase entity;
	private boolean gagged = false;

	private DuctTapeGag(EntityLivingBase entity)
	{
		this.entity = entity;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();
		properties.setBoolean("gagged", gagged);
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(EXT_PROP_NAME);
		gagged = properties.getBoolean("gagged");

		if (!entity.worldObj.isRemote)
		{
			sendMessage();
		}
	}

	@Override
	public void init(Entity entity, World world)
	{}

	public void sendMessage()
	{
		GasesFramework.networkWrapper.sendToAllAround(
				new MessageDuctTapeGag(entity, gagged),
				new NetworkRegistry.TargetPoint(entity.worldObj.provider.dimensionId, entity.posX, entity.posY, entity.posZ, 50.0D)
		);
	}
	
	public static DuctTapeGag register(EntityLivingBase entity)
	{
		DuctTapeGag gag = new DuctTapeGag(entity);
		entity.registerExtendedProperties(EXT_PROP_NAME, gag);
		return gag;
	}

	public static DuctTapeGag get(EntityLivingBase entity)
	{
		return (DuctTapeGag)entity.getExtendedProperties(EXT_PROP_NAME);
	}

	public static DuctTapeGag getOrRegister(EntityLivingBase entity)
	{
		DuctTapeGag gag = get(entity);
		if (gag == null)
		{
			gag = register(entity);
		}
		return gag;
	}

	public static void gag(EntityLivingBase entity)
	{
		DuctTapeGag gag = getOrRegister(entity);
		gag.gagged = true;

		if (!entity.worldObj.isRemote)
		{
			gag.sendMessage();
		}
	}

	public static void ungag(EntityLivingBase entity)
	{
		DuctTapeGag gag = getOrRegister(entity);
		gag.gagged = false;

		if (!entity.worldObj.isRemote)
		{
			gag.sendMessage();
		}
	}

	public static boolean isGagged(EntityLivingBase entity)
	{
		DuctTapeGag gag = get(entity);
		return gag != null && gag.gagged;
	}
}
