package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.container.ContainerGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGasTransposer extends Block implements ITileEntityProvider, IGasReceptor, IGasPropellor
{
    public BlockGasTransposer()
	{
		super(Material.iron);
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityGasTransposer tileentityfurnace = (TileEntityGasTransposer)world.getTileEntity(x, y, z);

            if (tileentityfurnace != null)
            {
            	entityPlayer.openGui(GasesFramework.instance, ContainerGasTransposer.GUI_ID, world, x, y, z);
            }

            return true;
        }
    }
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
	{
		TileEntityGasTransposer tileEntity = (TileEntityGasTransposer)world.getTileEntity(x, y, z);
		
		if(tileEntity != null)
		{
			for(int i = 0; i < tileEntity.getSizeInventory(); i++)
			{
				ItemStack itemstack = tileEntity.getStackInSlot(i);
				
				if(itemstack != null)
				{
					float f = world.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
					float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
					
					while(itemstack.stackSize > 0)
					{
						int k1 = world.rand.nextInt(21) + 10;
						
						if(k1 > itemstack.stackSize)
						{
							k1 = itemstack.stackSize;
						}
						
						itemstack.stackSize -= k1;
						EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
						
						if(itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						}
						
						float f3 = 0.05F;
						entityitem.motionX = (double)((float)world.rand.nextGaussian() * f3);
						entityitem.motionY = (double)((float)world.rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double)((float)world.rand.nextGaussian() * f3);
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
			
			world.func_147453_f(x, y, z, block);
		}
		
		super.breakBlock(world, x, y, z, block, metadata);
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		return false;
	}

	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		return false;
	}

	@Override
	public boolean canPropelGasFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityGasTransposer();
	}
}