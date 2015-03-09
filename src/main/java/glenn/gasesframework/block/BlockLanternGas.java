package glenn.gasesframework.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.type.GasType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockLanternGas extends BlockLantern implements IGasReceptor
{
	public final Combustibility combustibility;
	
	public BlockLanternGas(Combustibility combustibility)
	{
		super(combustibility.burnRate);
		this.combustibility = combustibility;
        
        combustibility.lanternBlock = this;
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, int side, GasType gasType)
	{
		if(gasType == GasesFrameworkAPI.gasTypeAir) return true;
		
		BlockLanternGas lanternBlock = (BlockLanternGas)gasType.combustibility.lanternBlock;
		if(lanternBlock != null && world.getBlockMetadata(x, y, z) == 0)
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
		if(lanternBlock != null && world.getBlockMetadata(x, y, z) == 0)
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
	
	public ItemStack getContainedItemOut()
	{
		return new ItemStack(Items.glass_bottle);
	}
	
	public Block getExpirationBlock()
	{
		return GasesFramework.lanternGasEmpty;
	}

	@Override
	public ItemStack getContainedItemIn()
	{
		return null;
	}
}