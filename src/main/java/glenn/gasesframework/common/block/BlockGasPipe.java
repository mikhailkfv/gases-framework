package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasInterface;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.common.tileentity.TileEntityGasPump;
import glenn.gasesframework.util.PipeBranch;
import glenn.gasesframework.util.PipeBranchIterator;
import glenn.gasesframework.util.PipeSearch;
import glenn.gasesframework.util.PipeSearch.PipeEnd;
import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyPair;
import glenn.moddingutils.KeyVec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGasPipe extends Block implements IGasReceptor
{
	/**
	 * A sub-type for pipes, defined by their metadata
	 * @author Glenn
	 *
	 */
	public static class SubType
	{
		public final int metadata;
		public final String name;
		public final boolean isSolid;
		
		public IIcon solidIcon;
		public IIcon gasContentIcon;
		public IIcon connectorsIcon;
		public IIcon endIcon;
		
		public SubType(int metadata, String name, boolean isSolid)
		{
			this.metadata = metadata;
			this.name = name;
			this.isSolid = isSolid;
		}
		
		public String baseTexture()
		{
			return "gasesframework:pipe_" + name;
		}
		
		public void registerIcons(IIconRegister iconRegister)
		{
			solidIcon = iconRegister.registerIcon(baseTexture() + "_solid");
			if(!isSolid) gasContentIcon = iconRegister.registerIcon(baseTexture() + "_gas_content");
			connectorsIcon = iconRegister.registerIcon(baseTexture() + "_connectors");
			endIcon = iconRegister.registerIcon(baseTexture() + "_end");
		}
	}
	
	public final SubType[] subTypes = new SubType[16];
	
	/**
	 * The gas block contained by this gas pipe.
	 */
	public GasType type;
	
	/**
	 * Constructs a new gas pipe block. Because of technical reasons, each type of gas needs its own gas pipe block for pipe usage.
	 * @param par1 - Block ID
	 * @param containedGas - The gas this pipe will carry
	 */
	public BlockGasPipe(GasType type)
	{
		super(Material.wood);
		this.type = type;
		
		this.setHardness(0.25F);
		this.setBlockTextureName("gasesframework:pipe");
		this.setStepSound(Block.soundTypeStone);
		
		subTypes[0] = new SubType(0, "iron", true);
		subTypes[1] = new SubType(1, "glass", false);
	}
	
	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		for(int i = 0; i < subTypes.length; i++)
		{
			SubType subType = subTypes[i];
			if(subType != null)
			{
				subType.registerIcons(iconRegister);
			}
		}
	}
	
	/**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return subTypes[metadata].solidIcon;
    }
	
	/**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item blockItem, CreativeTabs creativeTab, List list)
    {
        for (int i = 0; i < 16; ++i)
        {
        	if(subTypes[i] != null)
        	{
        		list.add(new ItemStack(blockItem, 1, i));
        	}
        }
    }
    
    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int metadata)
    {
        return metadata;
    }
	
	public boolean[] getRenderConnectionArray(IBlockAccess blockAccess, int x, int y, int z)
	{
		final boolean[] sidePipe = new boolean[6];
		final boolean[] renderConnections = new boolean[6];
        for(int i = 0; i < 6; i++)
		{
        	ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[i];
			int x1 = x + side.offsetX;
			int y1 = y + side.offsetY;
			int z1 = z + side.offsetZ;
			
			Block directionBlock = blockAccess.getBlock(x1, y1, z1);
			if(directionBlock instanceof IGasInterface)
			{
				sidePipe[i] = ((IGasInterface)directionBlock).connectToPipe(blockAccess, x1, y1, z1, side.getOpposite());
			}
			else
			{
				sidePipe[i] = false;
			}
		}
        
        boolean collectionAll = sidePipe[0] || sidePipe[1] || sidePipe[2] || sidePipe[3] || sidePipe[4] || sidePipe[5];
		boolean collectionY = sidePipe[2] || sidePipe[3] || sidePipe[4] || sidePipe[5];
		boolean collectionX = sidePipe[0] || sidePipe[1] || sidePipe[2] || sidePipe[3];
		boolean collectionZ = sidePipe[0] || sidePipe[1] || sidePipe[4] || sidePipe[5];
        
    	renderConnections[0] = (sidePipe[0] | !collectionY) & collectionAll;
    	renderConnections[1] = (sidePipe[1] | !collectionY) & collectionAll;
    	renderConnections[2] = (sidePipe[2] | !collectionZ) & collectionAll;
    	renderConnections[3] = (sidePipe[3] | !collectionZ) & collectionAll;
    	renderConnections[4] = (sidePipe[4] | !collectionX) & collectionAll;
    	renderConnections[5] = (sidePipe[5] | !collectionX) & collectionAll;
    	
    	return renderConnections;
	}
	
	public float[] getBounds(IBlockAccess blockAccess, int x, int y, int z)
	{
		boolean[] renderConnections = this.getRenderConnectionArray(blockAccess, x, y, z);
        float f1 = 6.0F / 16.0F;
    	float f2 = 10.0F / 16.0F;
		
		return new float[]{
				renderConnections[0] ? 0.0F : f1,
		    	renderConnections[1] ? 1.0F : f2,
		    	renderConnections[2] ? 0.0F : f1,
		    	renderConnections[3] ? 1.0F : f2,
		    	renderConnections[4] ? 0.0F : f1,
		    	renderConnections[5] ? 1.0F : f2
			};
	}
	
	/**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity)
    {
        final float[] bounds = this.getBounds(world, x, y, z);
        float f1 = 6.0F / 16.0F;
    	float f2 = 10.0F / 16.0F;
    	
    	this.setBlockBounds(f1, f1, bounds[2], f2, f2, bounds[3]);
        super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);

    	this.setBlockBounds(f1, bounds[0], f1, f2, bounds[1], f2);
        super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);

    	this.setBlockBounds(bounds[4], f1, f1, bounds[5], f2, f2);
        super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        
        this.setBlockBoundsBasedOnState(world, x, y, z);
    }
	
	/**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
    	float[] bounds = getBounds(blockAccess, x, y, z);
    	
    	this.setBlockBounds(bounds[4], bounds[0], bounds[2], bounds[5], bounds[1], bounds[3]);
    }

    @Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		PipeSearch.ReceptorSearch search = new PipeSearch.ReceptorSearch(world, x, y, z, 15);
    	
		boolean isSearchingLooseEnds = !search.looseEnds.isEmpty();
    	ArrayList<PipeSearch.PipeEnd> listToSearch = (ArrayList<PipeSearch.PipeEnd>)(isSearchingLooseEnds ? search.looseEnds : search.ends).clone();
    	Collections.shuffle(listToSearch, world.rand);
    	
    	for(PipeSearch.PipeEnd end : listToSearch)
    	{
    		IVec branchPos = end.branch.getPosition();
    		BlockGasPipe pipeSourceBlock = (BlockGasPipe)world.getBlock(branchPos.x, branchPos.y, branchPos.z);
    		boolean hasPushed = false;
    		
    		if(isSearchingLooseEnds)
    		{
    			hasPushed = GasesFrameworkAPI.fillWithGas(world, world.rand, end.endPosition.x, end.endPosition.y, end.endPosition.z, pipeSourceBlock.type);
    		}
    		else
    		{
    			IGasReceptor receptor = (IGasReceptor)world.getBlock(end.endPosition.x, end.endPosition.y, end.endPosition.z);
    			hasPushed = receptor.receiveGas(world, end.endPosition.x, end.endPosition.y, end.endPosition.z, end.endDirection.getOpposite(), pipeSourceBlock.type);
    		}
    		
    		if(hasPushed)
    		{
    			PipeBranchIterator.DescendingPipeBranchIterator iterator = new PipeBranchIterator.DescendingPipeBranchIterator(end.branch);
    			PipeBranchIterator.Iteration iteration;
    			while((iteration = iterator.narrowNext(world.rand)) != null)
    			{
    				Block receptorBlock = world.getBlock(iteration.previousPosition.x, iteration.previousPosition.y, iteration.previousPosition.z);
    				int receptorMetadata = world.getBlockMetadata(iteration.previousPosition.x, iteration.previousPosition.y, iteration.previousPosition.z);
    				Block giverBlock = world.getBlock(iteration.currentPosition.x, iteration.currentPosition.y, iteration.currentPosition.z);
    				
    				if(receptorBlock != giverBlock)
    				{
    					world.setBlock(iteration.previousPosition.x, iteration.previousPosition.y, iteration.previousPosition.z, giverBlock, receptorMetadata, 3);
    				}
    			}
    			
    			int thisMetadata = world.getBlockMetadata(x, y, z);
    			world.setBlock(x, y, z, gasType.pipeBlock, thisMetadata, 3);
    			
    			return true;
    		}
    	}
    	
    	return false;
	}
    
    @Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
    	return true;
	}
	
	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
    @Override
	public boolean isOpaqueCube()
	{
	    return false;
	}
	
	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
    @Override
	public boolean renderAsNormalBlock()
	{
	    return false;
	}
	
	/**
	 * The type of render function that is called for this block
	 */
    @Override
	public int getRenderType()
	{
	    return RenderBlockGasPipe.RENDER_ID;
	}

    @Override
	public Item getItem(World par1World, int par2, int par3, int par4)
	{
	    return Item.getItemFromBlock(GasesFrameworkAPI.gasTypeAir.pipeBlock);
	}
    
    @Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
	    return Item.getItemFromBlock(GasesFrameworkAPI.gasTypeAir.pipeBlock);
	}
	
	/**
	 * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
	 * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
	 * metadata
	 */
    @Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldBlockMetadata)
	{
		if(type.block != null && world.isAirBlock(x, y, z))
		{
			world.setBlock(x, y, z, type.block);
		}
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
	
	@Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
    {
		if(type.block != null)
		{
			((BlockGas)type.block).onFire(world, x, y, z, world.rand, 0);
		}
    }
	
	public byte[] getPossiblePropellingDirections(World world, int x, int y, int z)
	{
		final byte[] res = new byte[6];
		final IVec pipePosition = new IVec(x, y, z);
		
		PipeSearch.PropellorSearch pumpSearch = new PipeSearch.PropellorSearch(world, x, y, z, 15);
		
		for(PipeSearch.PipeEnd propellor : pumpSearch.propellors)
		{
			IVec pipePos = propellor.branch.getPosition();
			PipeSearch.ReceptorSearch search = new PipeSearch.ReceptorSearch(world, pipePos.x, pipePos.y, pipePos.z, 15);
			ArrayList<PipeSearch.PipeEnd> listToSearch = search.looseEnds.isEmpty() ? search.ends : search.looseEnds;
			
			if(listToSearch.size() > 0)
			{
				if(pipePos.x == x && pipePos.y == y && pipePos.z == z)
				{
					res[propellor.endDirection.getOpposite().ordinal()] |= 2;
				}
				
				for(PipeSearch.PipeEnd end : listToSearch)
				{
					PipeBranchIterator.DescendingPipeBranchIterator iterator = new PipeBranchIterator.DescendingPipeBranchIterator(end.branch);
					PipeBranchIterator.Iteration iteration;
					while((iteration = iterator.next()) != null)
					{
						if(iteration.currentPosition.equals(pipePosition))
						{
							res[ForgeDirection.VALID_DIRECTIONS[iteration.direction].getOpposite().ordinal()] |= 1;
						}
						else if(iteration.previousPosition.equals(pipePosition))
						{
							res[iteration.direction] |= 2;
						}
					}
					
					if(end.branch.getPosition().equals(pipePosition))
					{
						res[end.endDirection.ordinal()] |= 1;
					}
				}
			}
		}
		
		return res;
	}
}