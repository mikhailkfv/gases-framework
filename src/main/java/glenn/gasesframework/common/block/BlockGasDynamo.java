package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.container.ContainerGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGasDynamo extends Block implements IGasReceptor, ITileEntityProvider
{
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconFrontBurning;
    
	public BlockGasDynamo()
	{
		super(Material.iron);
	}
	
	 /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
    	super.registerBlockIcons(iconRegister);
    	iconFront = iconRegister.registerIcon(getTextureName() + "_front");
    	iconFrontBurning = iconRegister.registerIcon(getTextureName() + "_front_burning");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
    	int metadata = blockAccess.getBlockMetadata(x, y, z);
    	if(metadata == side)
    	{
    		TileEntityGasDynamo gasDynamo = (TileEntityGasDynamo)blockAccess.getTileEntity(x, y, z);
    		return gasDynamo.isBurning ? iconFrontBurning : iconFront;
    	}
    	else
    	{
    		return this.blockIcon;
    	}
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
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityGasDynamo tileEntity = (TileEntityGasDynamo)world.getTileEntity(x, y, z);

            if (tileEntity != null)
            {
            	entityPlayer.openGui(GasesFramework.instance, ContainerGasDynamo.GUI_ID, world, x, y, z);
            }

            return true;
        }
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
    {
    	TileEntityGasDynamo tileEntity = (TileEntityGasDynamo)world.getTileEntity(x, y, z);
    	if(tileEntity == null) return false;
    	return tileEntity.blockEvent(eventID, eventParam);
    }

	@Override
    public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
    {
    	TileEntityGasDynamo gasDynamo = (TileEntityGasDynamo)world.getTileEntity(x, y, z);
		if(gasType == GasesFrameworkAPI.gasTypeAir)
		{
			return true;
		}
		else if(gasType.combustibility.burnRate == 0)
		{
			return false;
		}
		else if(gasDynamo.getFuelStored() + 100 * gasType.combustibility.burnRate <= gasDynamo.getMaxFuelStored())
		{
			return true;
		}
		
		return false;
    }

	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		if(canReceiveGas(world, x, y, z, side, gasType))
		{
			TileEntityGasDynamo gasDynamo = (TileEntityGasDynamo)world.getTileEntity(x, y, z);
			gasDynamo.setFuelLevel(gasDynamo.getFuelStored() + 100 * gasType.combustibility.burnRate);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityGasDynamo();
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
}