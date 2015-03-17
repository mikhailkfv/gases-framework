package glenn.gasesframework.api.gasworldgentype;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import glenn.gasesframework.api.gastype.GasType;

public class GasWorldGenPocket extends GasWorldGenType
{
	public final Block replacedBlock;
	
	/**
	 * Creates a new gas world gen pocket. Gas world gen types are necessary for adding gases to the terrain.
	 * If the Gases Framework retrogen is enabled, this type will be generated in chunks where it has not previously been generated.
	 * @param name - A name for this gas world gen type. Must be unique.
	 * @param gasType - The gas type to be placed by this world generator.
	 * @param generationFrequency - The average amount of pockets/clouds per 16x16x16 chunk of blocks.
	 * @param averageVolume - The average number of gas blocks of the gas pockets/clouds this will generate.
	 * @param evenness - The evenness of the gas pocket/cloud. 0.0f gives very uneven gas pockets/clouds. 1.0f gives completely round gas pockets/clouds.
	 * @param minY - The minimal y coordinate of a gas pocket/cloud.
	 * @param maxY - The maximal y coordiante of a gas pocket/cloud.
	 * @param replacedBlock - The block that will be replaced by this gas pocket.
	 */
	public GasWorldGenPocket(String name, GasType gasType, float generationFrequency, float averageVolume, float evenness, int minY, int maxY, Block replacedBlock)
	{
		super(name, gasType, generationFrequency, averageVolume, evenness, minY, maxY);
		this.replacedBlock = replacedBlock;
	}
	
	/**
	 * Get the volume of gas placed at this location, if any. Must be a number between 0 and 16.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param placementScore - The greater this value is, the more central this block of gas is.
	 * @return
	 */
	@Override
	public int getPlacementVolume(World world, int x, int y, int z, float placementScore)
	{
		if(world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, replacedBlock))
		{
			for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
	    	{
	    		Block block = world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
	    		if(block != gasType.block && (block instanceof BlockFalling || !block.isOpaqueCube()))
				{
					return 0;
				}
	    	}
			return 16;
		}
		return 0;
	}
}