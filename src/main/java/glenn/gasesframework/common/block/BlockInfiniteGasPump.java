package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockInfiniteGasPump;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasPump;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockInfiniteGasPump extends Block implements ISample, IGasPropellor, ITileEntityProvider
{
	@SideOnly(Side.CLIENT)
	public IIcon typeIcon;
	@SideOnly(Side.CLIENT)
	public IIcon indicatorIcon;
	
    public BlockInfiniteGasPump()
    {
		super(Material.iron);
    }

	@SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return RenderBlockInfiniteGasPump.RENDER_ID;
    }

	@SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
    	super.registerBlockIcons(iconRegister);
    	typeIcon = iconRegister.registerIcon(this.getTextureName() + "_type");
    	indicatorIcon = iconRegister.registerIcon(this.getTextureName() + "_indicator");
    }

    @Override
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, ForgeDirection side)
	{
    	if(!world.isRemote)
    	{
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
			TileEntityInfiniteGasPump tileEntity = (TileEntityInfiniteGasPump)world.getTileEntity(x, y, z);
			tileEntity.setType(in, side);
    	}
    	
		return in;
	}
    
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
    {
    	TileEntityInfiniteGasPump tileEntity = (TileEntityInfiniteGasPump)world.getTileEntity(x, y, z);
    	if(tileEntity == null) return false;
    	return tileEntity.blockEvent(eventID, eventParam);
    }

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) 
	{
		return new TileEntityInfiniteGasPump();
	}

	@Override
	public int getPressureFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return GasesFramework.configurations.piping.ironMaterial.maxPressure;
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
}
