package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.common.container.ContainerGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGasFurnace extends BlockContainer implements IGasReceptor, IGasPropellor
{
	/**
	 * Is the random generator used by furnace to drop the inventory contents in random directions.
	 */
	private final Random furnaceRand = new Random();

	/** True if this is an active furnace, false if idle */
	private final boolean isActive;

	/**
	 * This flag is used to prevent the furnace inventory to be dropped upon block removal, is used internally when the
	 * furnace block changes from idle to active and vice-versa.
	 */
	private static boolean keepFurnaceInventory;
	@SideOnly(Side.CLIENT)
	private IIcon furnaceIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon[] furnaceIconFront;

	public BlockGasFurnace(boolean isActive)
	{
		super(Material.iron);
		this.isActive = isActive;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		this.setDefaultDirection(world, x, y, z);
	}

	/**
	 * set a blocks direction
	 */
	private void setDefaultDirection(World world, int x, int y, int z)
	{
		if (!world.isRemote)
		{
			Block localBlock1 = world.getBlock(x, y, z - 1);
			Block localBlock2 = world.getBlock(x, y, z + 1);
			Block localBlock3 = world.getBlock(x - 1, y, z);
			Block localBlock4 = world.getBlock(x + 1, y, z);

			int l = 3;
			if ((localBlock1.func_149730_j()) && (!localBlock2.func_149730_j())) l = 3;
			if ((localBlock2.func_149730_j()) && (!localBlock1.func_149730_j())) l = 2;
			if ((localBlock3.func_149730_j()) && (!localBlock4.func_149730_j())) l = 5;
			if ((localBlock4.func_149730_j()) && (!localBlock3.func_149730_j())) l = 4;

			world.setBlockMetadataWithNotify(x, y, z, l, 2);
		}
	}


	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return par1 == 1 ? this.furnaceIconTop : (par1 == 0 ? this.furnaceIconTop : (par1 == 4 ? this.furnaceIconFront[0] : this.blockIcon));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		TileEntityGasFurnace tileEntity = (TileEntityGasFurnace)blockAccess.getTileEntity(x, y, z);
		return side == 1 ? this.furnaceIconTop : (side == 0 ? this.furnaceIconTop : (metadata != side ? this.blockIcon : this.furnaceIconFront[tileEntity.prevStage]));
	}

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.furnaceIconFront = new IIcon[5];
		this.blockIcon = iconRegister.registerIcon(getTextureName() + "_side");
		this.furnaceIconTop = iconRegister.registerIcon(getTextureName() + "_top");
		for(int i = 0; i < 5; i++)
		{
			this.furnaceIconFront[i] = iconRegister.registerIcon(getTextureName() + "_front_" + i);
		}
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			TileEntityGasFurnace tileEntity = (TileEntityGasFurnace)world.getTileEntity(x, y, z);

			if (tileEntity != null)
			{
				entityPlayer.openGui(GasesFramework.instance, ContainerGasFurnace.GUI_ID, world, x, y, z);
			}

			return true;
		}
	}

	/**
	 * Update which block ID the furnace is using depending on whether or not it is burning
	 */
	public static void updateFurnaceBlockState(int stage, World world, int x, int y, int z)
	{
		int l = world.getBlockMetadata(x, y, z);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		keepFurnaceInventory = true;

		if(stage == 0)
		{
			world.setBlock(x, y, z, GasesFramework.gasFurnaceIdle);
		}
		else
		{
			world.setBlock(x, y, z, GasesFramework.gasFurnaceActive);
		}

		keepFurnaceInventory = false;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);

		if (tileentity != null)
		{
			tileentity.validate();
			world.setTileEntity(x, y, z, tileentity);
		}
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityGasFurnace();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack)
	{
		int l = MathHelper.floor_double((double)(entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (l == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}

		if (l == 1)
		{
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}

		if (l == 2)
		{
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}

		if (l == 3)
		{
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}

		if (itemStack.hasDisplayName())
		{
			((TileEntityGasFurnace)world.getTileEntity(x, y, z)).setGuiDisplayName(itemStack.getDisplayName());
		}
	}

	/**
	 * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
	 * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
	 * metadata
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMetadata)
	{
		if (!keepFurnaceInventory)
		{
			TileEntityGasFurnace tileentityfurnace = (TileEntityGasFurnace)world.getTileEntity(x, y, z);

			if (tileentityfurnace != null)
			{
				for (int j1 = 0; j1 < tileentityfurnace.getSizeInventory(); ++j1)
				{
					ItemStack itemstack = tileentityfurnace.getStackInSlot(j1);

					if (itemstack != null)
					{
						float f = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
						float f1 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
						float f2 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

						while (itemstack.stackSize > 0)
						{
							int k1 = this.furnaceRand.nextInt(21) + 10;

							if (k1 > itemstack.stackSize)
							{
								k1 = itemstack.stackSize;
							}

							itemstack.stackSize -= k1;
							EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));

							if (itemstack.hasTagCompound())
							{
								entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
							}

							float f3 = 0.05F;
							entityitem.motionX = (double)((float)this.furnaceRand.nextGaussian() * f3);
							entityitem.motionY = (double)((float)this.furnaceRand.nextGaussian() * f3 + 0.2F);
							entityitem.motionZ = (double)((float)this.furnaceRand.nextGaussian() * f3);
							world.spawnEntityInWorld(entityitem);
						}
					}
				}

				world.func_147453_f(x, y, z, oldBlock);
			}
		}

		super.breakBlock(world, x, y, z, oldBlock, oldMetadata);
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from
	 * getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
	 * strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		return Container.calcRedstoneFromInventory((IInventory)world.getTileEntity(x, y, z));
	}

	@SideOnly(Side.CLIENT)

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public Item getItem(World world, int x, int y, int z)
	{
		return Item.getItemFromBlock(GasesFramework.gasFurnaceIdle);
	}
	
	@Override
	public Item getItemDropped(int par1, Random random, int par3)
	{
		return Item.getItemFromBlock(GasesFramework.gasFurnaceIdle);
	}
	
	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		TileEntityGasFurnace gasFurnace = (TileEntityGasFurnace)world.getTileEntity(x, y, z);
		if(gasType == GasesFrameworkAPI.gasTypeAir)
		{
			return true;
		}
		else if(gasType.combustibility.burnRate == 0)
		{
			return false;
		}
		else if(gasFurnace.furnaceBurnTime + 100 * gasType.combustibility.burnRate <= TileEntityGasFurnace.maxFurnaceBurnTime)
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
			TileEntityGasFurnace gasFurnace = (TileEntityGasFurnace)world.getTileEntity(x, y, z);
			gasFurnace.furnaceBurnTime += 100 * gasType.combustibility.burnRate;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean canPropelGasFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		if(side == ForgeDirection.UP)
		{
			TileEntityGasFurnace gasFurnace = (TileEntityGasFurnace)world.getTileEntity(x, y, z);
			return gasFurnace.isBurning();
		}
		return false;
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
}
