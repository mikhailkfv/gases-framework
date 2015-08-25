package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockGasDynamo;
import glenn.gasesframework.common.container.ContainerGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.moddingutils.blockrotation.BlockRotation;
import glenn.moddingutils.blockrotation.AbstractRenderRotatedBlock;
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

public abstract class BlockGasDynamo extends Block implements IGasReceptor, ITileEntityProvider
{
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconFrontBurning;
    
	public BlockGasDynamo(Material material)
	{
		super(material);
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
    	BlockRotation blockRotation = BlockRotation.getRotation(blockAccess.getBlockMetadata(x, y, z));

    	ForgeDirection sideDirection = ForgeDirection.getOrientation(side);
    	ForgeDirection actualSide = blockRotation.rotate(sideDirection);
    	
    	if(actualSide == ForgeDirection.NORTH)
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
    	world.setBlockMetadataWithNotify(x, y, z, BlockRotation.getRotation(-entity.rotationYaw, entity.rotationPitch).ordinal(), 2);
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

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
    	if (!AbstractRenderRotatedBlock.renderingInventoryBlock)
    	{
    		return RenderBlockGasDynamo.RENDER_ID;
    	}
    	else
    	{
    		return super.getRenderType();
    	}
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
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
}