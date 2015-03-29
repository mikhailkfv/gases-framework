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

public class ExtendedGasEffects extends ExtendedGasEffectsBase
{
	private ExtendedGasEffects(EntityLivingBase entity)
	{
		super(entity);
		
		entity.getDataWatcher().addObject(WATCHER, 0);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();
		
		properties.setInteger("blindnessTimer", get(0));
		properties.setInteger("suffocationTimer", get(1));
		properties.setInteger("slownessTimer", get(2));
		
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(EXT_PROP_NAME);
		
		set(1, properties.getInteger("blindnessTimer"));
		set(2, properties.getInteger("suffocationTimer"));
		set(3, properties.getInteger("slownessTimer"));
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
		
		int blindnessTimer = get(1);
		int suffocationTimer = get(2);
		int slownessTimer = get(3);
		
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
		
		set(0, blindnessTimer);
		set(1, suffocationTimer);
		set(2, slownessTimer);
	}
	
	@Override
	public int get(int watchObject)
	{
		int d = entity.getDataWatcher().getWatchableObjectInt(WATCHER);
		switch(watchObject)
		{
		case 0: return d / 1000000;
		case 1: return (d - (d / 1000000 * 1000000)) / 1000;
		case 2: return (d - (d / 1000 * 1000));
		default: return -1;
		}
	}
	
	@Override
	public int set(int watchObject, int i)
	{
		int d = entity.getDataWatcher().getWatchableObjectInt(WATCHER);
		
		int blindness = 0;
		int suffocation = 0;
		int slowness = 0;
		switch(watchObject)
		{
		case 0: blindness = d / 1000000; // returns XXX 000 000
		case 1: suffocation = (d - (d / 1000000 * 1000000)) / 1000; // returns 000 XXX 000
		case 2: slowness = (d - (d / 1000 * 1000)); // returns 000 000 XXX
		}
		
		int combined = (slowness * 1) + (suffocation * 1000) + (blindness * 1000000);;
		switch(watchObject)
		{
		case 0: entity.getDataWatcher().updateObject(WATCHER, combined); return blindness; //blindness (000 /// ///)
		case 1: entity.getDataWatcher().updateObject(WATCHER, combined); return suffocation; //suffocation (/// 111 ///)
		case 2: entity.getDataWatcher().updateObject(WATCHER, combined); return slowness; //slowness (/// /// 222)
		default: return 0; //welp you fucked up
		}
		
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