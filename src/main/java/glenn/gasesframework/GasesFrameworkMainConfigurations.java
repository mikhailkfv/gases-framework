package glenn.gasesframework;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.moddingutils.Configurations;

public class GasesFrameworkMainConfigurations extends Configurations
{
	public float gases_gasExplosionFactor;
	public int other_gasFurnaceHeatingSpeed;
	public boolean other_fancyTank;
	public int gases_fireSmokeAmount;
	public int gases_maxGasHeight;
	public String[] other_additionalIgnitionBlocks;
	public String[] other_removedIgnitionBlocks;
	public String[] other_additionalIgnitionItems;
	public String[] other_removedIgnitionItems;
	public String[] other_customGasFurnaceRecipes;
	
	public GasesFrameworkMainConfigurations(File configurationsFile)
	{
		super(configurationsFile);
	}
	
	@Override
	protected void setDefaults()
	{
		gases_gasExplosionFactor = 2.5F;
		other_gasFurnaceHeatingSpeed = 2;
		other_fancyTank = true;
		gases_fireSmokeAmount = 8;
		gases_maxGasHeight = 256;
		other_additionalIgnitionBlocks = new String[0];
		other_removedIgnitionBlocks = new String[0];
		other_additionalIgnitionItems = new String[0];
		other_removedIgnitionItems = new String[0];
		other_customGasFurnaceRecipes = new String[0];
	}

	@Override
	protected void onLoaded()
	{
		for(String s : other_additionalIgnitionBlocks)
		{
			Block block = Block.getBlockFromName(s);
			if(block != null) GasesFrameworkAPI.registerIgnitionBlock(block);
		}
		
		for(String s : other_additionalIgnitionItems)
		{
			Item item = (Item)Item.itemRegistry.getObject(s);
			if(item != null) GasesFrameworkAPI.registerIgnitionItem(item);
		}
	}
}