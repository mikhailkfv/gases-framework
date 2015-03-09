package glenn.gasesframework.api.worldgen;

import glenn.gasesframework.api.type.GasType;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenFloatingGas extends WorldGenGas
{
	/**
	 * Constructs a new floating gas generator. Floating gas generators are much like gas generators, but will only replace air.
	 * @param type - The gas type to be generated
     * @param blockMetadata - Metadata of the gas to be generated
     * @param blobs - The amount of 16-block blobs to generate
	 */
	public WorldGenFloatingGas(GasType type, int blockMetadata, int blobs)
	{
		super(type, blockMetadata, blobs);
	}

	@Override
	protected Block getBlockToPlace(World world, int x, int y, int z, Random rand)
    {
    	Block block = world.getBlock(x, y, z);
    	return block == Blocks.air ? type.block : null;
    }
}
