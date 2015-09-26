package glenn.gasesframework.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import glenn.gasesframework.api.GFAPI;
import glenn.gasesframework.api.block.IGasTypeFilter;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IRenderedGasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterSimple;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockGasTypeFilter;
import glenn.gasesframework.client.render.RenderRotatedBlock;
import glenn.gasesframework.common.tileentity.TileEntityDirectionalGasPropellor;
import glenn.moddingutils.blockrotation.BlockRotation;
import glenn.moddingutils.blockrotation.IRotatedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockDirectionalGasPropellor extends Block implements IGasReceptor, IGasPropellor, ITileEntityProvider, IGasTypeFilter, IRenderedGasTypeFilter, IRotatedBlock
{
	private boolean isBottomUnique;
	private int maxPressure;

	public IIcon bottomIcon;
	public IIcon topIcon;
	
	private boolean renderFilter = true;
	private boolean renderRotated = true;
	
	public BlockDirectionalGasPropellor(Material material, boolean isBottomUnique, int maxPressure)
	{
		super(material);
		this.isBottomUnique = isBottomUnique;
		this.maxPressure = maxPressure;
	}
    
    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
    {
    	world.setBlockMetadataWithNotify(x, y, z, BlockRotation.getRotation(-entity.rotationYaw, entity.rotationPitch).ordinal(), 2);
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
    	if (renderFilter)
    	{
    		return RenderBlockGasTypeFilter.RENDER_ID;
    	}
    	else
    	{
		    if (renderRotated)
		    {
				return RenderRotatedBlock.RENDER_ID;
		    }
		    else
		    {
			    return super.getRenderType();
		    }
    	}
    }
	
	@SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon(this.getTextureName() + "_side");
        bottomIcon = iconRegister.registerIcon(this.getTextureName() + (isBottomUnique ? "_bottom" : "_side"));
        topIcon = iconRegister.registerIcon(this.getTextureName() + "_top");
    }
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata)
	{
		BlockRotation rotation = BlockRotation.getRotation(metadata);
		ForgeDirection sideDirection = ForgeDirection.getOrientation(side);
		ForgeDirection blockSide = rotation.rotate(sideDirection);

		switch (blockSide)
		{
		case NORTH:
			return topIcon;
		case SOUTH:
			return bottomIcon;
		default:
			return blockIcon;
		}
	}
	
	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		if(canReceiveGas(world, x, y, z, side, gasType))
		{
			TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)world.getTileEntity(x, y, z);
			
			tileEntity.containedType = gasType;
			tileEntity.pumpTime /= 2;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		BlockRotation rotation = BlockRotation.getRotation(world.getBlockMetadata(x, y, z));
		if(rotation.rotateInverse(ForgeDirection.NORTH) != side)
		{
			TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)world.getTileEntity(x, y, z);
			
			return tileEntity != null && tileEntity.acceptsType(gasType) && (tileEntity.containedType == null || tileEntity.containedType == GFAPI.gasTypeAir);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int getPressureFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		BlockRotation rotation = BlockRotation.getRotation(world.getBlockMetadata(x, y, z));
		return side == rotation.rotateInverse(ForgeDirection.NORTH) ? maxPressure : 0;
	}
	
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
    {
    	TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)world.getTileEntity(x, y, z);
    	if(tileEntity == null) return false;
    	return tileEntity.blockEvent(eventID, eventParam);
    }

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
	
	@Override
	public GasTypeFilter getFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side)
	{
		TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)blockAccess.getTileEntity(x, y, z);
		return tileEntity != null ? tileEntity.filter : null;
	}
	
	@Override
	public GasTypeFilter setFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{
		if (filter instanceof GasTypeFilterSimple)
		{
			GasTypeFilterSimple simpleFilter = (GasTypeFilterSimple)filter;
			TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)blockAccess.getTileEntity(x, y, z);
			if (tileEntity != null)
			{
				return tileEntity.setFilter(simpleFilter);
			}
		}
		return null;
	}
	
	@Override
	public GasTypeFilter getRenderedFilter(IBlockAccess blockAccess, int x, int y, int z, ForgeDirection side)
	{
		BlockRotation rotation = getBlockRotation(blockAccess, x, y, z);
		if (side == rotation.rotateInverse(ForgeDirection.NORTH))
		{
			return getFilter(blockAccess, x, y, z, side);
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void swapRenderedGasTypeFilterRenderType()
	{
		renderFilter = !renderFilter;
	}
	
	@Override
	public BlockRotation getBlockRotationAsItem(int metadata)
	{
		return BlockRotation.EAST_FORWARD;
	}
	
	@Override
	public BlockRotation getBlockRotation(IBlockAccess blockAccess, int x, int y, int z)
	{
		return BlockRotation.getRotation(blockAccess.getBlockMetadata(x, y, z));
	}
	
	@Override
	public void swapRotatedBlockRenderType()
	{
		renderRotated = !renderRotated;
	}
}