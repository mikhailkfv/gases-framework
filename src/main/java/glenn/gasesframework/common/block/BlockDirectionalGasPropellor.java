package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasFilter;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.filter.GasTypeFilterSimple;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockDirectionalGasPropellor;
import glenn.gasesframework.common.tileentity.TileEntityDirectionalGasPropellor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockDirectionalGasPropellor extends Block implements IGasReceptor, IGasPropellor, ITileEntityProvider, IGasFilter
{
	private boolean isBottomUnique;
	private int maxPressure;

	public IIcon sideIcon;
	public IIcon bottomIcon;
	public IIcon topIcon;
	public IIcon topIncludingIcon;
	public IIcon topExcludingIcon;
	public IIcon topIndicatorIcon;
	
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
    	int metadata;
    	
    	if(entity.rotationPitch < -45.0f)
    	{
    		metadata = 0;
    	}
    	else if(entity.rotationPitch > 45.0f)
    	{
    		metadata = 1;
    	}
    	else
    	{
    		int side = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    		switch(side)
    		{
    		case 0:
    			metadata = 2;
    			break;
    		case 1:
    			metadata = 5;
    			break;
    		case 2:
    			metadata = 3;
    			break;
    		default:
    			metadata = 4;
    		}
    	}
    	
    	world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
        return RenderBlockDirectionalGasPropellor.RENDER_ID;
    }
	
	@SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        sideIcon = iconRegister.registerIcon(this.getTextureName() + "_side");
        bottomIcon = iconRegister.registerIcon(this.getTextureName() + (isBottomUnique ? "_bottom" : "_side"));
        topIcon = iconRegister.registerIcon(this.getTextureName() + "_top");
        topIncludingIcon = iconRegister.registerIcon(this.getTextureName() + "_top_including");
        topExcludingIcon = iconRegister.registerIcon(this.getTextureName() + "_top_excluding");
        topIndicatorIcon = iconRegister.registerIcon(this.getTextureName() + "_top_indicator");
    }
	
	@SideOnly(Side.CLIENT)
    @Override
	public IIcon getIcon(int side, int metadata)
    {
        ForgeDirection blockDirection = ForgeDirection.UP;
        ForgeDirection sideDirection = ForgeDirection.getOrientation(side);
        
        if(sideDirection == blockDirection)
        {
        	return topIcon;
        }
        else if(sideDirection == blockDirection.getOpposite())
        {
        	return bottomIcon;
        }
        else
        {
        	return sideIcon;
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
		if(ForgeDirection.getOrientation((world.getBlockMetadata(x, y, z)) % 6) != side)
		{
			TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)world.getTileEntity(x, y, z);
			
			return tileEntity != null && tileEntity.acceptsType(gasType) && (tileEntity.containedType == null || tileEntity.containedType == GasesFrameworkAPI.gasTypeAir);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int getPressureFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return side == ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z) % 6) ? maxPressure : 0;
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
	public GasTypeFilter getFilter(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)world.getTileEntity(x, y, z);
		return tileEntity != null ? tileEntity.filter : null;
	}
	
	@Override
	public GasTypeFilter setFilter(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{
		if (filter instanceof GasTypeFilterSimple)
		{
			GasTypeFilterSimple simpleFilter = (GasTypeFilterSimple)filter;
			TileEntityDirectionalGasPropellor tileEntity = (TileEntityDirectionalGasPropellor)world.getTileEntity(x, y, z);
			if (tileEntity != null)
			{
				return tileEntity.setFilter(simpleFilter);
			}
		}
		return null;
	}
}