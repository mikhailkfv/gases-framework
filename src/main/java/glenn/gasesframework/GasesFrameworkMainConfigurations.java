package glenn.gasesframework;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.moddingutils.Configurations;

public class GasesFrameworkMainConfigurations extends Configurations
{
	@ConfigField(path="gases.Gas explosion factor", comment="The power of gas explosions", defaultValue="2.5")
	public float gases_gasExplosionFactor;
	
	@ConfigField(path="other.Furnace heating speed", comment="The speed at which gas furnaces heat up and cool down", defaultValue="2")
	public int other_gasFurnaceHeatingSpeed;
	
	@ConfigField(path="other.Fancy tanks", comment="Whether or not gas tanks will be rendered with fancy fluid physics", defaultValue="true")
	public boolean other_fancyTank;
	
	@ConfigField(path="gases.Fire smoke amount", comment="The amount of smoke that will be generated by fire from 0 to 16", defaultValue="8")
	public int gases_fireSmokeAmount;

	@ConfigField(path="gases.Max gas height", comment="The maximal height gas can reach before it disappears", defaultValue="256")
	public int gases_maxGasHeight;

	@ConfigField(path="gases.Additional ignition blocks", comment="A list of block names for blocks that will be added to the gas ignition block registry", defaultValue="")
	public String[] other_additionalIgnitionBlocks;

	@ConfigField(path="gases.Removed ignition blocks", comment="A list of block names for blocks that will be removed from the gas ignition block registry", defaultValue="")
	public String[] other_removedIgnitionBlocks;

	@ConfigField(path="gases.Additional ignition items", comment="A list of item names for items that will be added to the gas ignition item registry", defaultValue="")
	public String[] other_additionalIgnitionItems;

	@ConfigField(path="gases.Removed ignition items", comment="A list of item names for items that will be removed from the gas ignition item registry", defaultValue="")
	public String[] other_removedIgnitionItems;
	
	@ConfigField(path="gases.stuff.Stuff", comment="Stuff", defaultValue="1\n2\n3")
	public int[] stuff;
	
	public GasesFrameworkMainConfigurations(File configurationsFile)
	{
		super(configurationsFile);
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