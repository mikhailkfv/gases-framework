package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.item.ISample;
import glenn.gasesframework.client.render.RenderBlockGasPump;
import glenn.gasesframework.common.tileentity.TileEntityPump;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGasPump extends Block implements IGasReceptor, IGasPropellor, ITileEntityProvider, ISample
{
	private static final int[] reindex = new int[]{
		1, 0, 5, 4, 3, 2
	};
	
	private boolean isBottomUnique;
	public IIcon sideIcon;
	public IIcon bottomIcon;
	public IIcon topIcon;
	public IIcon topIncludingIcon;
	public IIcon topExcludingIcon;
	public IIcon topIndicatorIcon;
	
	public BlockGasPump(boolean isBottomUnique)
	{
		super(Material.iron);
		this.isBottomUnique = isBottomUnique;
	}
    
    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack)
    {
    	int metadata;
    	
    	if(entity.rotationPitch > 45.0f)
    	{
    		metadata = 0;
    	}
    	else if(entity.rotationPitch < -45.0f)
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
        return RenderBlockGasPump.RENDER_ID;
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

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
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
			TileEntityPump tileEntity = (TileEntityPump)world.getTileEntity(x, y, z);
			
			tileEntity.containedType = gasType;
			tileEntity.pumpTime = 1;
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
		if(ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)) != side.getOpposite())
		{
			TileEntityPump tileEntity = (TileEntityPump)world.getTileEntity(x, y, z);
			
			return tileEntity != null && tileEntity.acceptsType(gasType) && tileEntity.containedType == null;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean canPropelGasFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return side == ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
	   return new TileEntityPump();
	}

	@Override
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, boolean excludes)
	{
		TileEntityPump tileEntity = (TileEntityPump)world.getTileEntity(x, y, z);
		
		if(tileEntity != null)
		{
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
			if(tileEntity.excludes == excludes && tileEntity.filterType == in)
			{
				tileEntity.excludes = false;
				tileEntity.filterType = null;
			}
			else
			{
				tileEntity.excludes = excludes;
		    	tileEntity.filterType = in;
			}
		}
    	
		world.markBlockForUpdate(x, y, z);
		
		return in;
	}
    
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
    {
    	TileEntityPump tileEntity = (TileEntityPump)world.getTileEntity(x, y, z);
    	if(tileEntity == null) return false;
    	return tileEntity.blockEvent(eventID, eventParam);
    }

	@Override
	public boolean connectToPipe()
	{
		return true;
	}
}