package glenn.gasesframework.api.worldgen;

import glenn.gasesframework.api.type.GasType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenGas extends WorldGenerator
{
    /** The gas to be placed using this generator. */
    protected GasType type;
    /** The metadata of the gas to be placed using this generator. */
    protected int minableBlockMetadata;

    /** The number of 16-block blobs to generate. */
    protected int blobs;
    protected Block replacedBlock;
    
    private static class KeyVec
    {
    	public final int x;
    	public final int y;
    	public final int z;
    	
    	public KeyVec(int x, int y, int z)
    	{
    		this.x = x;
    		this.y = y;
    		this.z = z;
    	}
    	
    	@Override
    	public boolean equals(Object otherObject)
    	{
    		if(this == otherObject)
    		{
    			return true;
    		}
    		else if(otherObject instanceof KeyVec)
    		{
    			KeyVec other = (KeyVec)otherObject;
    			return this.x == other.x && this.y == other.y && this.z == other.z;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	
    	@Override
    	public int hashCode()
    	{
    		return (Integer.hashCode(x) * 3) ^ (Integer.hashCode(y) * 7) ^ (Integer.hashCode(z) * 31);
    	}
    }
    
    protected Map<KeyVec, Boolean> cache;
    
    /**
     * Creates a new gas world generator. Gas generators are much like ore generators with the exception of the size being variable, and no generated blocks are visible on cave surfaces.
     * @param type - The gas type to be generated
     * @param blockMetadata - Metadata of the gas to be generated
     * @param blobs - The amount of 16-block blobs to generate
     */
    public WorldGenGas(GasType type, int blockMetadata, int blobs)
    {
        this.type = type;
        this.minableBlockMetadata = blockMetadata;
        this.blobs = blobs;
        this.replacedBlock = Blocks.stone;
    }

	@Override
    public boolean generate(World world, Random random, int x, int y, int z)
    {
		cache = new HashMap<KeyVec, Boolean>();
    	
    	for(int i = 0; i < blobs; i++)
    	{
			int x2 = x + random.nextInt(blobs * 2 - 1) - blobs - 1;
			int y2 = y + random.nextInt(blobs * 2 - 1) - blobs - 1;
			int z2 = z + random.nextInt(blobs * 2 - 1) - blobs - 1;
			
			generatePart(world, random, x2, y2, z2, 16);
    	}

        return true;
    }
	
	protected void generatePart(World world, Random random, int x, int y, int z, int numberOfBlocks)
	{
		/*for(int i = 0; i < numberOfBlocks; i++)
		{
			int x1 = x + random.nextInt(5) - 2;
			int y1 = y + random.nextInt(5) - 2;
			int z1 = z + random.nextInt(5) - 2;
			if(isPlacementValid(world, x1, y1, z1))
			{
				world.setBlock(x1, y1, z1, minableBlock);
			}
		}*/
		
        float var6 = random.nextFloat() * (float)Math.PI;
        double var7 = (double)((float)(x + 8) + MathHelper.sin(var6) * (float)numberOfBlocks / 8.0F);
        double var9 = (double)((float)(x + 8) - MathHelper.sin(var6) * (float)numberOfBlocks / 8.0F);
        double var11 = (double)((float)(z + 8) + MathHelper.cos(var6) * (float)numberOfBlocks / 8.0F);
        double var13 = (double)((float)(z + 8) - MathHelper.cos(var6) * (float)numberOfBlocks / 8.0F);
        double var15 = (double)(y + random.nextInt(3) - 2);
        double var17 = (double)(y + random.nextInt(3) - 2);

        for (int var19 = 0; var19 <= numberOfBlocks; ++var19)
        {
            double var20 = var7 + (var9 - var7) * (double)var19 / (double)numberOfBlocks;
            double var22 = var15 + (var17 - var15) * (double)var19 / (double)numberOfBlocks;
            double var24 = var11 + (var13 - var11) * (double)var19 / (double)numberOfBlocks;
            double var26 = random.nextDouble() * (double)numberOfBlocks / 16.0D;
            double var28 = (double)(MathHelper.sin((float)var19 * (float)Math.PI / (float)numberOfBlocks) + 1.0F) * var26 + 1.0D;
            double var30 = (double)(MathHelper.sin((float)var19 * (float)Math.PI / (float)numberOfBlocks) + 1.0F) * var26 + 1.0D;
            int var32 = MathHelper.floor_double(var20 - var28 / 2.0D);
            int var33 = MathHelper.floor_double(var22 - var30 / 2.0D);
            int var34 = MathHelper.floor_double(var24 - var28 / 2.0D);
            int var35 = MathHelper.floor_double(var20 + var28 / 2.0D);
            int var36 = MathHelper.floor_double(var22 + var30 / 2.0D);
            int var37 = MathHelper.floor_double(var24 + var28 / 2.0D);

            for (int var38 = var32; var38 <= var35; ++var38)
            {
                double var39 = ((double)var38 + 0.5D - var20) / (var28 / 2.0D);

                if (var39 * var39 < 1.0D)
                {
                    for (int var41 = var33; var41 <= var36; ++var41)
                    {
                        double var42 = ((double)var41 + 0.5D - var22) / (var30 / 2.0D);

                        if (var39 * var39 + var42 * var42 < 1.0D)
                        {
                            for (int var44 = var34; var44 <= var37; ++var44)
                            {
                                double var45 = ((double)var44 + 0.5D - var24) / (var28 / 2.0D);

                                if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D)
                                {
                                	Block block = getBlockToPlace(world, var38, var41, var44, random);
                                	if(block == type.block)
                                	{
                                		world.setBlock(var38, var41, var44, block, this.minableBlockMetadata, 2);
                                	}
                                	else if(block != null)
                                	{
                                		world.setBlock(var38, var41, var44, block);
                                	}
                                }
                            }
                        }
                    }
                }
            }
        }
	}
    
    protected boolean isPlacementValid(World world, int x, int y, int z)
    {
        for(int i = 0; i < 6; i++)
    	{
    		int x2 = x + (i == 4 ? -1 : (i == 5 ? 1 : 0));
    		int y2 = y + (i == 0 ? -1 : (i == 1 ? 1 : 0));
    		int z2 = z + (i == 2 ? -1 : (i == 3 ? 1 : 0));
    		
			if(isInvalidBorderingBlock(world, x2, y2, z2))
			{
				return false;
			}
    	}
    	
    	return true;
    }
    
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
    	
    	return null;
    }
    
    private boolean isInvalidBorderingBlock(World world, int x, int y, int z)
    {
    	KeyVec vec = new KeyVec(x, y, z);
    	Boolean b = cache.get(vec);
    	if(b == null)
    	{
    		Block block = world.getBlock(x, y, z);
    		b = block != type.block && (block == Blocks.gravel || block == Blocks.sand || !world.getBlock(x, y, z).isOpaqueCube());
    		cache.put(vec, b);
    	}
    	
    	return b;
    }
}
