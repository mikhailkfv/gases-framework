package glenn.gasesframework.common;

import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.gasesframework.api.block.MaterialGas;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.item.IGasEffectProtector;
import glenn.gasesframework.common.block.BlockGas;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedGasEffects extends ExtendedGasEffectsBase
{
	private ExtendedGasEffects(EntityLivingBase entity)
	{
		super(entity);
		
		entity.getDataWatcher().addObject(BLINDNESS_WATCHER, 0);
		entity.getDataWatcher().addObject(SUFFOCATION_WATCHER, 0);
		entity.getDataWatcher().addObject(SLOWNESS_WATCHER, 0);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();
		
		properties.setInteger("blindnessTimer", get(BLINDNESS_WATCHER));
		properties.setInteger("suffocationTimer", get(SUFFOCATION_WATCHER));
		properties.setInteger("slownessTimer", get(SLOWNESS_WATCHER));
		
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(EXT_PROP_NAME);
		
		set(BLINDNESS_WATCHER, properties.getInteger("blindnessTimer"));
		set(SUFFOCATION_WATCHER, properties.getInteger("suffocationTimer"));
		set(SLOWNESS_WATCHER, properties.getInteger("slownessTimer"));
	}

	@Override
	public void init(Entity entity, World world)
	{
		
	}
	
	private boolean blind(GasType gasType)
	{
		if(entity instanceof EntityPlayer)
		{
			boolean protect = false;
			
			InventoryPlayer inventory = ((EntityPlayer)entity).inventory;
			
			for(int i = 0; i < 4; i++)
			{
				ItemStack stack = inventory.armorItemInSlot(i);
				if(stack != null && stack.getItem() instanceof IGasEffectProtector)
				{
					IGasEffectProtector item = (IGasEffectProtector)stack.getItem();
					
					if(item.protectVision(entity, stack, gasType)) protect = true;
				}
			}
			
			return !protect;
		}
		return true;
	}
	
	private boolean suffocate(GasType gasType)
	{
		if(entity instanceof EntityPlayer)
		{
			boolean protect = false;
			
			InventoryPlayer inventory = ((EntityPlayer)entity).inventory;
			
			for(int i = 0; i < 4; i++)
			{
				ItemStack stack = inventory.armorItemInSlot(i);
				if(stack != null && stack.getItem() instanceof IGasEffectProtector)
				{
					IGasEffectProtector item = (IGasEffectProtector)stack.getItem();
					
					if(item.protectBreath(entity, stack, gasType)) protect = true;
				}
			}
			
			return !protect;
		}
		return true;
	}
	
	public void tick()
	{
		GasType gasType = null;
		
		if(entity.isInsideOfMaterial(MaterialGas.INSTANCE))
		{
			double dy = entity.posY + (double)entity.getEyeHeight();
			int x = MathHelper.floor_double(entity.posX);
			int y = MathHelper.floor_double(dy);
			int z = MathHelper.floor_double(entity.posZ);
			
			Block block = entity.worldObj.getBlock(x, y, z);
			if(block instanceof BlockGas)
			{
				gasType = ((BlockGas)block).type;
			}
		}
		
		int blindnessTimer = get(BLINDNESS_WATCHER);
		int suffocationTimer = get(SUFFOCATION_WATCHER);
		int slownessTimer = get(SLOWNESS_WATCHER);
		
		if(gasType != null && gasType.blindnessRate > 0 && blind(gasType))
		{
			blindnessTimer += gasType.blindnessRate + 4;
		}
		blindnessTimer -= 4;
		
		if(gasType != null && gasType.suffocationRate > 0 && suffocate(gasType))
		{
			suffocationTimer += gasType.suffocationRate + 32;
			
			if(suffocationTimer > 350)
			{
				slownessTimer += gasType.slownessRate + 10;
			}
			
			if(suffocationTimer > 400)
			{
				gasType.onBreathed(entity);
				suffocationTimer -= 32;
			}
		}
		suffocationTimer -= 32;
		slownessTimer -= 10;
		
		if(blindnessTimer > 500) blindnessTimer = 500;
		else if(blindnessTimer < 0) blindnessTimer = 0;
		
		if(suffocationTimer < 0) suffocationTimer = 0;
		
		if(slownessTimer > 1000) slownessTimer = 1000;
		else if(slownessTimer < 0) slownessTimer = 0;
		
		set(BLINDNESS_WATCHER, blindnessTimer);
		set(SUFFOCATION_WATCHER, suffocationTimer);
		set(SLOWNESS_WATCHER, slownessTimer);
	}
	
	@Override
	public int get(int watchObject)
	{
		return entity.getDataWatcher().getWatchableObjectInt(watchObject);
	}

	@Override
	public int set(int watchObject, int i)
	{
		entity.getDataWatcher().updateObject(watchObject, i);
		return i;
	}

	@Override
	public int increment(int watchObject, int i)
	{
		return set(watchObject, get(watchObject) + i);
	}
	
	public static void register(EntityLivingBase entity)
	{
		entity.registerExtendedProperties(EXT_PROP_NAME, new ExtendedGasEffects(entity));
	}
}