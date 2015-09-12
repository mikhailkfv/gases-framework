package glenn.gasesframework.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class DuctTapeGag implements IExtendedEntityProperties
{
    public static final String EXT_PROP_NAME = "DuctTapeGag";

    private boolean gagged = true;

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setBoolean("gagged", gagged);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        gagged = compound.getBoolean("gagged");
    }

    @Override
    public void init(Entity entity, World world)
    {

    }

    private static DuctTapeGag get(EntityLivingBase entity)
    {
        return (DuctTapeGag)entity.getExtendedProperties(EXT_PROP_NAME);
    }

    public static void gag(EntityLivingBase entity)
    {
        DuctTapeGag gag = get(entity);
        if (gag == null)
        {
            entity.registerExtendedProperties(EXT_PROP_NAME, new DuctTapeGag());
        }
        else
        {
            gag.gagged = true;
        }
    }

    public static void ungag(EntityLivingBase entity)
    {
        DuctTapeGag gag = get(entity);
        if (gag != null)
        {
            gag.gagged = false;
        }
    }

    public static boolean isGagged(EntityLivingBase entity)
    {
        DuctTapeGag gag = get(entity);
        return gag != null && gag.gagged;
    }
}
