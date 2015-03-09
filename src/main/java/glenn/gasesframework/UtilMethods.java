package glenn.gasesframework;

import glenn.gasesframework.api.item.IGasEffectProtector;
import glenn.gasesframework.api.type.GasType;
import glenn.gasesframework.block.BlockGas;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Ignore, only used to simplify core modding.
 * @author Glenn
 *
 */
public class UtilMethods
{
	public static ItemStack getBottledItem(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGas)
		{
			return ((BlockGas)block).type.getBottledItem();
		}
		else
		{
			return null;
		}
	}
	
	public static boolean blind(EntityLivingBase entity, GasType type)
	{
		if(entity instanceof EntityPlayer)
		{
			boolean protect = false;
			
			InventoryPlayer inventory = ((EntityPlayer)entity).inventory;
			
			for(int i = 0; i < 4; i++)
			{
				ItemStack stack = inventory.armorItemInSlot(i);
				if(stack != null && IGasEffectProtector.class.isAssignableFrom(stack.getItem().getClass()))
				{
					IGasEffectProtector item = (IGasEffectProtector)stack.getItem();
					
					if(item.protectVision(entity, stack, type)) protect = true;
				}
			}
			
			return !protect;
		}
		return true;
	}
	
	public static boolean suffocate(EntityLivingBase entity, GasType type)
	{
		if(entity instanceof EntityPlayer)
		{
			boolean protect = false;
			
			InventoryPlayer inventory = ((EntityPlayer)entity).inventory;
			
			for(int i = 0; i < 4; i++)
			{
				ItemStack stack = inventory.armorItemInSlot(i);
				if(stack != null && IGasEffectProtector.class.isAssignableFrom(stack.getItem().getClass()))
				{
					IGasEffectProtector item = (IGasEffectProtector)stack.getItem();
					
					if(item.protectBreath(entity, stack, type)) protect = true;
				}
			}
			
			return !protect;
		}
		return true;
	}
}