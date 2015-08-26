package glenn.gasesframework.common.block;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasSource;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.common.item.ItemGasBottle;
import glenn.gasesframework.common.tileentity.TileEntityGasTank;
import glenn.moddingutils.IVec;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockGasTank extends Block implements IGasSource, IGasReceptor, ITileEntityProvider, ISample
{
	public IIcon side;
	public IIcon top;
	public IIcon inside;
	
	public BlockGasTank(Material material)
	{
		super(material);
	}
	
	@SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        side = par1IconRegister.registerIcon(this.getTextureName() + "_side");
        top = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        inside = par1IconRegister.registerIcon(this.getTextureName() + "_inside");
    }
	
	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return par1 < 2 ? top : side;
	}
	
	@Override
	public GasType getGasTypeFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		GasType gasType = tileEntity.getGasTypeStored();
		
		return gasType != null ? gasType : GasesFrameworkAPI.gasTypeAir;
	}

	@Override
	public GasType takeGasTypeFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		GasType gasType = tileEntity.getGasTypeStored();
		tileEntity.decrement();
		
		return gasType != null ? gasType : GasesFrameworkAPI.gasTypeAir;
	}
	
	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		return tileEntity.canIncrement(gasType);
	}
	
	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		return tileEntity.increment(gasType);
	}
	
    @Override
	public boolean isOpaqueCube()
	{
		return false;
	}

    /**
     * The type of render function that is called for this block
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return RenderBlockGasTank.RENDER_ID;
    }

	@Override
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, ForgeDirection side)
	{
		TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
		return tileEntity.getGasTypeStored();
	}
    
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
    {
    	TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
    	if (tileEntity != null)
    	{
		    return tileEntity.blockEvent(eventID, eventParam);
    	}
    	return false;
    }
    
    /**
	 * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
	 * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
	 * metadata
	 */
    @Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldBlockMetadata)
	{
    	TileEntityGasTank tileEntity = (TileEntityGasTank)world.getTileEntity(x, y, z);
    	tileEntity.emptyInAir();

    	super.breakBlock(world, x, y, z, oldBlock, oldBlockMetadata);
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
}