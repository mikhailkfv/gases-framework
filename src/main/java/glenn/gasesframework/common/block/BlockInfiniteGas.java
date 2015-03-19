package glenn.gasesframework.common.block;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.item.ISample;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGas;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockInfiniteGas extends Block implements ISample, ITileEntityProvider
{
	private IIcon iconHole;
	private IIcon iconWall;
    //Bottom (0), Top (1), North (2), South (3), West (4), East (5).
	private static final int[] reindex = new int[]{
		1, 0, 5, 4, 3, 2
	};
	
    public BlockInfiniteGas()
    {
        super(Material.rock);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
		return reindex[side];
    }

	public IIcon getIcon(int par1, int par2)
    {
        boolean flag = true;
        
        switch(par1)
        {
        case 0:
        case 1:
        	flag = par2 == 0 | par2 == 1;
        	break;
        case 2:
        case 3:
        	flag = par2 == 5 | par2 == 4;
        	break;
        case 4:
        case 5:
        	flag = par2 == 3 | par2 == 2;
        	break;
        }
        
        return flag ? iconHole : iconWall;
    }
    
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        iconHole = par1IconRegister.registerIcon(this.getTextureName() + "_hole");
        iconWall = par1IconRegister.registerIcon(this.getTextureName() + "");
    }

    @Override
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, boolean excludes)
	{
		TileEntityInfiniteGas tileEntity = (TileEntityInfiniteGas)world.getTileEntity(x, y, z);
		
		if(tileEntity != null)
		{
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.3F, 0.6F);
	    	tileEntity.setType(in);
	    	if(excludes)
	    	{
	    		tileEntity.setDirectional(false);
	    	}
	    	else
	    	{
	    		tileEntity.setDirectional(true);
	    	}
		}
    	
		return in;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) 
	{
		return new TileEntityInfiniteGas();
	}
}
