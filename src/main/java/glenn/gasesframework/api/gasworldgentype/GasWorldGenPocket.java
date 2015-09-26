package glenn.gasesframework.api.gasworldgentype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import glenn.gasesframework.api.GasesFrameworkAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import glenn.gasesframework.api.gastype.GasType;

public class GasWorldGenPocket extends GasWorldGenType
{
	public final Set<Block> replaceBlocks = Collections.newSetFromMap(new IdentityHashMap<Block, Boolean>());
	
	/**
	 * Creates a new gas world gen pocket. Gas world gen types are necessary for adding gases to the terrain.
	 * If the Gases Framework retrogen is enabled, this type will be generated in chunks where it has not previously been generated.
	 * @param name A name for this gas world gen type. Must be unique
	 * @param gasType The gas type to be placed by this world generator
	 * @param generationFrequency The average amount of pockets/clouds per 16x16x16 chunk of blocks
	 * @param averageVolume The average number of gas blocks of the gas pockets/clouds this will generate
	 * @param evenness The evenness of the gas pocket/cloud. 0.0f gives very uneven gas pockets/clouds. 1.0f gives completely round gas pockets/clouds
	 * @param minY The minimal y coordinate of a gas pocket/cloud
	 * @param maxY The maximal y coordinate of a gas pocket/cloud
	 * @param replaceBlocks Blocks replaceable by this gas pocket. Strings will be interpreted as ore dictionary strings
	 */
	public GasWorldGenPocket(String name, GasType gasType, float generationFrequency, float averageVolume, float evenness, int minY, int maxY, Object... replaceBlocks)
	{
		super(name, gasType, generationFrequency, averageVolume, evenness, minY, maxY);
		
		for (Object obj : replaceBlocks)
		{
			if (obj instanceof Block)
			{
				this.replaceBlocks.add((Block)obj);
			}
			else if (obj instanceof String)
			{
				ArrayList<ItemStack> itemstacks =  OreDictionary.getOres((String)obj);
				for (ItemStack itemstack : itemstacks)
				{
					Item item = itemstack.getItem();
					Block block = Block.getBlockFromItem(item);
					if (block != null)
					{
						this.replaceBlocks.add(block);
					}
				}
			}
		}
	}

	/**
	 * Get the volume of gas placed at this location, if any. Must be a number between 0 and 16
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param placementScore The greater this value is, the more central this block of gas is.
	 * @return The volume of gas to place
	 */
	@Override
	public int getPlacementVolume(World world, int x, int y, int z, float placementScore)
	{
		if(this.replaceBlocks.contains(world.getBlock(x, y, z)))
		{
			for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
	    	{
				if (GasesFrameworkAPI.implementation.getGasType(world, x, y, z) != gasType)
				{
					Block block = world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
					if(block instanceof BlockFalling || !block.isOpaqueCube())
					{
						return 0;
					}
				}
	    	}
			return 16;
		}
		return 0;
	}
}