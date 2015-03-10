package glenn.gasesframework.api.worldgen;

import glenn.gasesframework.api.gastype.GasType;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenGasWithEdgeBlock extends WorldGenGas
{
	protected Block edgeBlock;
	protected int edgeBlockChance;
	
	public WorldGenGasWithEdgeBlock(GasType type, int blockMetadata, int blobs, Block edgeBlock, int edgeBlockChance)
	{
		super(type, blockMetadata, blobs);
		
		this.edgeBlock = edgeBlock;
		this.edgeBlockChance = edgeBlockChance;
	}
	
	@Override
	protected Block getBlockToPlace(World world, int x, int y, int z, Random rand)
    {
		Block block = world.getBlock(x, y, z);
    	if(block != null && !block.isReplaceableOreGen(world, x, y, z, replacedBlock))
    	{
    		return null;
    	}
    	
    	if(isPlacementValid(world, x, y, z))
    	{
    		return type.block;
    	}
    	
    	if(rand.nextInt(edgeBlockChance) == 0)
    	{
    		return edgeBlock;
    	}
    	
    	return null;
    }
}