package glenn.gasesframework.common;

import glenn.gasesframework.GasesFramework;
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
	public static final int WATCHER = GasesFramework.configurations.watcher_id;
	private static final int BITS_PER_CAP = 10;
	
	private ExtendedGasEffects(EntityLivingBase entity)
	{
		super(entity);
		
		entity.getDataWatcher().addObject(WATCHER, 0);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();
		
		properties.setInteger("blindnessTimer", get(BLINDNESS_CAP));
		properties.setInteger("suffocationTimer", get(SUFFOCATION_CAP));
		properties.setInteger("slownessTimer", get(SLOWNESS_CAP));
		
		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(EXT_PROP_NAME);
		
		set(BLINDNESS_CAP, properties.getInteger("blindnessTimer"));
		set(SUFFOCATION_CAP, properties.getInteger("suffocationTimer"));
		set(SLOWNESS_CAP, properties.getInteger("slownessTimer"));
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
		
		int blindnessTimer = get(BLINDNESS_CAP);
		int suffocationTimer = get(SUFFOCATION_CAP);
		int slownessTimer = get(SLOWNESS_CAP);
		
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
		
		set(BLINDNESS_CAP, blindnessTimer);
		set(SUFFOCATION_CAP, suffocationTimer);
		set(SLOWNESS_CAP, slownessTimer);
	}
	
	@Override
	public int get(int cap)
	{
		int bitmask = getBitmask(cap);
		int combined = entity.getDataWatcher().getWatchableObjectInt(WATCHER);
		return (combined & bitmask) >>> (BITS_PER_CAP * cap);
	}
	
	@Override
	public int set(int cap, int i)
	{
		if(i >= 1 << BITS_PER_CAP)
		{
			i = (1 << BITS_PER_CAP) - 1;
		}
		else if(i < 0)
		{
			i = 0;
		}
		
		int bitmask = getBitmask(cap);
		int combined = entity.getDataWatcher().getWatchableObjectInt(WATCHER);
		combined = (combined & ~bitmask) | ((i << (BITS_PER_CAP * cap)) & bitmask);
		entity.getDataWatcher().updateObject(WATCHER, combined);
		
		return i;
	}

	@Override
	public int increment(int watchObject, int i)
	{
		return set(watchObject, get(watchObject) + i);
	}
	
	private int getBitmask(int cap)
	{
		return ((1 << BITS_PER_CAP) - 1) << (BITS_PER_CAP * cap);
	}
	
	public static void register(EntityLivingBase entity)
	{
		entity.registerExtendedProperties(EXT_PROP_NAME, new ExtendedGasEffects(entity));
	}
}