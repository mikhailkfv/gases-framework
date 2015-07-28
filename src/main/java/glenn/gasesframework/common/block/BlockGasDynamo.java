package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
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

public class BlockGasDynamo extends Block implements IGasReceptor, ITileEntityProvider
{
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconFrontActive;
    @SideOnly(Side.CLIENT)
    private IIcon iconFrontActiveBurning;
    
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
    	iconFrontActive = iconRegister.registerIcon(getTextureName() + "_front_active");
    	iconFrontActiveBurning = iconRegister.registerIcon(getTextureName() + "_front_active_burning");
    }
    
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public IIcon getIcon(int par1, int par2)
    {
        return par1 == 4 ? iconFront : this.blockIcon;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
    	int metadata = blockAccess.getBlockMetadata(x, y, z);
    	if(metadata == side)
    	{
    		TileEntityGasDynamo gasDynamo = (TileEntityGasDynamo)blockAccess.getTileEntity(x, y, z);
    		if(gasDynamo.isBurning)
    		{
    			return iconFrontActiveBurning;
    		}
    		else
    		{
    			return gasDynamo.hasEnergy ? iconFrontActive : iconFront;
    		}
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
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if(l == 0) world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        else if(l == 1) world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        else if(l == 2) world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        else if(l == 3) world.setBlockMetadataWithNotify(x, y, z, 4, 2);
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
		else if(gasDynamo.fuelLevel + 100 * gasType.combustibility.burnRate <= GasesFramework.configurations.gasDynamo_maxFuel)
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
			gasDynamo.setFuelLevel(gasDynamo.fuelLevel + 100 * gasType.combustibility.burnRate);
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
		return false;
	}
}