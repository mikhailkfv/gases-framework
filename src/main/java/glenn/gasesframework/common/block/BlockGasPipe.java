package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasInterface;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasTransporter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.util.GasTransporterIterator;
import glenn.gasesframework.util.GasTransporterSearch;
import glenn.moddingutils.IVec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGasPipe extends Block implements IGasTransporter
{
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
	}
	
	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		for(PipeType type : PipeType.getAllTypes())
		{
			type.registerIcons(iconRegister);
		}
	}
	
	/**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return PipeType.getPipeTypeByID(metadata).solidIcon;
    }
	
	/**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item blockItem, CreativeTabs creativeTab, List list)
    {
        for (PipeType type : PipeType.getAllTypes())
        {
        	list.add(new ItemStack(blockItem, 1, type.pipeID));
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
	    return Item.getItemFromBlock(GasesFramework.registry.getGasPipeBlock(GasesFrameworkAPI.gasTypeAir));
	}
    
    @Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
	{
		return Item.getItemFromBlock(GasesFramework.registry.getGasPipeBlock(GasesFrameworkAPI.gasTypeAir));
	}
	
	/**
	 * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
	 * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
	 * metadata
	 */
    @Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldBlockMetadata)
	{
    	if (world.isAirBlock(x, y, z))
    	{
		    burst(world, x, y, z);
    	}
	}
    
    public void burst(World world, int x, int y, int z)
    {
    	if (type != GasesFrameworkAPI.gasTypeAir)
    	{
			GasesFramework.implementation.placeGas(world, x, y, z, type, 16);
    	}
    	else
    	{
    		world.setBlockToAir(x, y, z);
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
		BlockGas block = GasesFramework.registry.getGasBlock(type);
		if(block != null)
		{
			block.onFire(world, x, y, z, world.rand, 0);
		}
    }
	
	public byte[] getPossiblePropellingDirections(World world, int x, int y, int z)
	{
		final byte[] res = new byte[6];
		final IVec pipePosition = new IVec(x, y, z);
		
		GasTransporterSearch.PropellorSearch pumpSearch = new GasTransporterSearch.PropellorSearch(world, x, y, z, 31);
		
		for(GasTransporterSearch.End propellor : pumpSearch.propellors)
		{
			IGasPropellor propellorBlock = (IGasPropellor)world.getBlock(propellor.endPosition.x, propellor.endPosition.y, propellor.endPosition.z);
			int pressure = propellorBlock.getPressureFromSide(world, propellor.endPosition.x, propellor.endPosition.y, propellor.endPosition.z, propellor.endDirection);
			IVec pipePos = propellor.branch.getPosition();
			GasTransporterSearch.ReceptorSearch search = new GasTransporterSearch.ReceptorSearch(world, pipePos.x, pipePos.y, pipePos.z, pressure);
			ArrayList<GasTransporterSearch.End> listToSearch = search.looseEnds.isEmpty() ? search.ends : search.looseEnds;
			
			if(listToSearch.size() > 0)
			{
				if(pipePos.x == x && pipePos.y == y && pipePos.z == z)
				{
					res[propellor.endDirection.getOpposite().ordinal()] |= 2;
				}
				
				for(GasTransporterSearch.End end : listToSearch)
				{
					GasTransporterIterator.DescendingGasTransporterIterator iterator = new GasTransporterIterator.DescendingGasTransporterIterator(end.branch);
					GasTransporterIterator.Iteration iteration;
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
	
	public PipeType getPipeType(int metadata)
	{
		return PipeType.getPipeTypeByID(metadata);
	}
	
	public PipeType getPipeType(IBlockAccess blockAccess, int x, int y, int z)
	{
		return getPipeType(blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	public GasType getCarriedType(World world, int x, int y, int z)
	{
		return type;
	}

	@Override
	public IGasTransporter setCarriedType(World world, int x, int y, int z, GasType type)
	{
		if (this.type != type)
		{
			world.setBlock(x, y, z, GasesFramework.registry.getGasPipeBlock(type), world.getBlockMetadata(x, y, z), 3);
			return (IGasTransporter)world.getBlock(x, y, z);
		}
		else
		{
			return this;
		}
	}

	@Override
	public void handlePressure(World world, Random random, int x, int y, int z, int pressure)
	{
		PipeType type = getPipeType(world, x, y, z);
		int pressureTolerance = type.getPressureTolerance();
		
		if (pressureTolerance != -1 && pressureTolerance < pressure)
		{
			int burstChance = (pressureTolerance * 4 - pressure) * 10 + 20;
			if (burstChance <= 0 || random.nextInt(burstChance * 2) == 0)
			{
				burst(world, x, y, z);
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.break", 1.0F, random.nextFloat() * 0.1F + 0.9F);
			}
			else
			{
				float volume = 0.01F + random.nextFloat() * 0.01F + (pressure - pressureTolerance) * 0.001F;
				if(random.nextInt(burstChance) == 0)
				{
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.chestopen", volume * 2.0F, 0.25F);
				}
				else if(random.nextInt(burstChance) == 0)
				{
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "mob.irongolem.death", volume * 2.0F, 0.25F);
				}
				else if(random.nextInt(burstChance * 2) == 0)
				{
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "minecart.base", volume, 0.25F);
				}
				else if(random.nextInt(burstChance * 2) == 0)
				{
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "minecart.inside", volume, 0.25F);
				}
			}
		}
	}
}