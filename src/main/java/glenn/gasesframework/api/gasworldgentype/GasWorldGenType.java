package glenn.gasesframework.api.gasworldgentype;

import glenn.gasesframework.api.gastype.GasType;

import java.util.HashMap;

import net.minecraft.world.World;

public abstract class GasWorldGenType
{
	private static HashMap<String, GasWorldGenType> gasWorldGenTypesByName = new HashMap<String, GasWorldGenType>();
	
	/**
	 * A name for this gas world gen type. Must be unique.
	 */
	public final String name;
	/**
	 * The gas type to be placed by this world generator.
	 */
	public final GasType gasType;
	/**
	 * The average amount of pockets/clouds per 16x16x16 chunk of blocks.
	 */
	public final float generationFrequency;
	/**
	 * The average number of gas blocks of the gas pockets/clouds this will generate.
	 */
	public final float averageVolume;
	/**
	 * The evenness of the gas pocket/cloud. 0.0f gives very uneven gas pockets/clouds. 1.0f gives completely round gas pockets/clouds.
	 */
	public final float evenness;
	/**
	 * The minimal y coordinate of a gas pocket/cloud.
	 */
	public final int minY;
	/**
	 * The maximal y coordinate of a gas pocket/cloud.
	 */
	public final int maxY;
	
	/**
	 * Get a gas world gen type by its name.
	 * @param name
	 * @return
	 */
	public static GasWorldGenType getGasWorldGenTypeByName(String name)
	{
		return gasWorldGenTypesByName.get(name);
	}
	
	private void map()
	{
		GasWorldGenType prev = getGasWorldGenTypeByName(name);
		if(prev != null)
		{
			throw new RuntimeException("A gas world gen type named " + name + " attempted to override a gas world gen type with the same name");
		}
		
		gasWorldGenTypesByName.put(name, this);
	}
	
	/**
	 * Creates a new gas world gen type. Gas world gen types are necessary for adding gases to the terrain.
	 * If the Gases Framework retrogen is enabled, this type will be generated in chunks where it has not previously been generated.
	 * @param name - A name for this gas world gen type. Must be unique.
	 * @param gasType - The gas type to be placed by this world generator.
	 * @param generationFrequency - The average amount of pockets/clouds per 16x16x16 chunk of blocks.
	 * @param averageVolume - The average number of gas blocks of the gas pockets/clouds this will generate.
	 * @param evenness - The evenness of the gas pocket/cloud. 0.0f gives very uneven gas pockets/clouds. 1.0f gives completely round gas pockets/clouds.
	 * @param minY - The minimal y coordinate of a gas pocket/cloud.
	 * @param maxY - The maximal y coordiante of a gas pocket/cloud.
	 */
	public GasWorldGenType(String name, GasType gasType, float generationFrequency, float averageVolume, float evenness, int minY, int maxY)
	{
		this.name = name;
		this.gasType = gasType;
		this.generationFrequency = generationFrequency;
		this.averageVolume = averageVolume;
		this.evenness = evenness < 0.0f ? 0.0f : (evenness > 1.0f ? 1.0f : evenness);
		this.minY = minY;
		this.maxY = maxY;
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
	public abstract int getPlacementVolume(World world, int x, int y, int z, float placementScore);
}