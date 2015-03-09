package glenn.gasesframework.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.type.GasType;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockLanternGasEmpty extends BlockLanternSpecial implements IGasReceptor
{
	/**
	 * Creates a new lantern which contains certain items and can eject another item in return.
	 * @param blockID - The ID of the block.
	 * @param tickrate - The rate at which the lantern will burn out. Set to 0 for non-expiring lanterns.
	 * @param containedItemIn - The item/block which can be used with a lantern to create this lantern.
	 * @param containedItemOut - The item/block which will be ejected by this lantern. For instance, gas lanterns accept bottles of gas but eject empty bottles.
	 * @param expirationBlock - The block this lantern will become when expired or destroyed.
	 */
	public BlockLanternGasEmpty()
	{
		super(0, new ItemStack(Items.glass_bottle), new ItemStack(Items.glass_bottle), GasesFramework.lanternEmpty);
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, int side, GasType gasType)
	{
		if(gasType == GasesFrameworkAPI.gasTypeAir) return true;
		
		BlockLanternGas lanternBlock = (BlockLanternGas)gasType.combustibility.lanternBlock;
		if(lanternBlock != null)
		{
			world.setBlock(x, y, z, lanternBlock);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, int side, GasType gasType)
	{
		if(gasType == GasesFrameworkAPI.gasTypeAir) return true;
		
		BlockLanternGas lanternBlock = (BlockLanternGas)gasType.combustibility.lanternBlock;
		if(lanternBlock != null)
		{
			return true;
		}
		
		return false;
	}

	@Override
	public boolean connectToPipe()
	{
		return false;
	}
}