package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasTransporter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.client.render.RenderBlockLantern;

import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.core.appender.SyslogAppender;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLantern extends Block implements IGasReceptor
{
	public IIcon topIcon;
	public IIcon sideIcon;
	public IIcon sideConnectedIcon;
	public IIcon connectorsIcon;

	public final LanternType type;

	public BlockLantern(LanternType type)
	{
		super(Material.wood);
		this.type = type;
		setHardness(0.25F);
		setLightLevel(type.lightLevel);
		setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 10.0F / 16.0F, 0.75F);
		setCreativeTab(type.creativeTab);

		if (type.expirationRate > 0)
		{
			setTickRandomly(true);
		}
		setBlockTextureName(type.textureName);
	}

	@Override
	public String getUnlocalizedName()
	{
		return type.getUnlocalizedName();
	}

	/**
	 * When this method is called, your block should register all the icons it
	 * needs with the given IconRegister. This is the only chance you get to
	 * register icons.
	 */
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		super.registerBlockIcons(iconRegister);
		topIcon = iconRegister.registerIcon("gasesFramework:lantern_top");
		sideIcon = iconRegister.registerIcon("gasesFramework:lantern_side");
		sideConnectedIcon = iconRegister.registerIcon("gasesFramework:lantern_side_connected");
		connectorsIcon = iconRegister.registerIcon("gasesFramework:lantern_connectors");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (type.expirationRate > 0 && random.nextInt(type.expirationRate) == 0)
		{
			int metadata = world.getBlockMetadata(x, y, z) + 1;
			if (metadata >= 16)
			{
				world.setBlock(x, y, z, GasesFramework.registry.getLanternBlock(type.expirationLanternType));
			}
			else
			{
				world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
			}
		}
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
		ItemKey itemIn = new ItemKey(heldItem);

		LanternType replacementType = GasesFramework.registry.getLanternTypeByInput(itemIn);
		ItemStack itemStackOut = type.itemOut.itemStack();

		if (replacementType == null)
		{
			replacementType = GasesFramework.lanternTypeEmpty;
			if (!entityPlayer.capabilities.isCreativeMode)
			{
				entityPlayer.destroyCurrentEquippedItem();
				if (itemStackOut != null && !entityPlayer.inventory.addItemStackToInventory(itemStackOut) && !world.isRemote)
				{
					this.dropBlockAsItem(world, x, y, z, itemStackOut);
				}
			}
		}
		else
		{
			if (world.getBlock(x, y, z) != GasesFramework.registry.getLanternBlock(replacementType))
			{
				ItemStack a = entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem];
				if (!entityPlayer.capabilities.isCreativeMode)
				{
					a.stackSize--;
					if(a.stackSize == 0) entityPlayer.destroyCurrentEquippedItem();
					if (itemStackOut != null && !entityPlayer.inventory.addItemStackToInventory(itemStackOut) && !world.isRemote)
					{
						this.dropBlockAsItem(world, x, y, z, itemStackOut);
					}
				}
			}
		}

		world.setBlock(x, y, z, GasesFramework.registry.getLanternBlock(replacementType));

		return true;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether
	 * or not to render the shared face of two adjacent blocks and also whether
	 * the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False
	 * (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType()
	{
		return RenderBlockLantern.RENDER_ID;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z,
	 * neighbor
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		if (!world.isRemote && !this.canBlockStay(world, x, y, z))
		{
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}

	/**
	 * Can this block stay at this position. Similar to canPlaceBlockAt except
	 * gets checked often with plants.
	 */
	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return isValidConnection(world, x - 1, y, z) || isValidConnection(world, x + 1, y, z) || isValidConnection(world, x, y - 1, z) || isValidConnection(world, x, y + 1, z) || isValidConnection(world, x, y, z - 1) || isValidConnection(world, x, y, z + 1);
	}

	public boolean isValidConnection(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		return block.isOpaqueCube() || block instanceof IGasTransporter;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this
	 * box can change after the pool has been cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	/**
	 * Checks to see if its valid to put this block at the specified
	 * coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return canBlockStay(world, x, y, z);
	}

	/**
	 * This returns a complete list of items dropped from this block.
	 *
	 * @param world
	 *            The current world
	 * @param x
	 *            X Position
	 * @param y
	 *            Y Position
	 * @param z
	 *            Z Position
	 * @param metadata
	 *            Current metadata
	 * @param fortune
	 *            Breakers fortune level
	 * @return An ArrayList containing all items this block drops
	 */
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		ret.add(new ItemStack(GasesFramework.registry.getLanternBlock(GasesFramework.lanternTypeEmpty)));
		if (type.itemOut != null)
		{
			ret.add(new ItemStack(type.itemOut.item, 1, type.itemOut.damage));
		}

		return ret;
	}

	@Override
	public Item getItem(World world, int x, int y, int z)
	{
		if (type.expirationLanternType != null)
		{
			return Item.getItemFromBlock(GasesFramework.registry.getLanternBlock(type.expirationLanternType));
		}
		else
		{
			return super.getItem(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		switch (side)
		{
			case 2:
				return blockAccess.getBlock(x + 1, y, z) instanceof BlockGasPipe ? sideConnectedIcon : sideIcon;
			case 3:
				return blockAccess.getBlock(x - 1, y, z) instanceof BlockGasPipe ? sideConnectedIcon : sideIcon;
			case 4:
				return blockAccess.getBlock(x, y, z + 1) instanceof BlockGasPipe ? sideConnectedIcon : sideIcon;
			case 5:
				return blockAccess.getBlock(x, y, z - 1) instanceof BlockGasPipe ? sideConnectedIcon : sideIcon;
		}

		return topIcon;
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, ForgeDirection side)
	{
		return false;
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		if (canReceiveGas(world, x, y, z, side, gasType))
		{
			world.setBlock(x, y, z, GasesFramework.registry.getLanternBlock(GasesFramework.lanternTypesGas[gasType.combustibility.burnRate]));
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
		return type == GasesFramework.lanternTypeGasEmpty && gasType.combustibility != Combustibility.NONE;
	}
}