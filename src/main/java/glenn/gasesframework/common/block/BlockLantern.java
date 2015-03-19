package glenn.gasesframework.common.block;

import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.client.render.RenderBlockLantern;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
		this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 10.0F / 16.0F, 0.75F);
		
		if(type.expirationRate > 0)
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
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
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
		if(type.expirationRate > 0)
		{
			int metadata = world.getBlockMetadata(x, y, z) + 1;
			if(metadata >= 16)
			{
				world.setBlock(x, y, z, type.expirationLanternType.block);
			}
			else
			{
				world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
			}
		}
    }

    @Override
    public int tickRate(World world)
    {
        return type.expirationRate > 0 ? type.expirationRate : 0;
    }

	/**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
    	ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
    	ItemKey itemIn = new ItemKey(heldItem);
    	
    	LanternType replacementType = LanternType.getLanternTypeByItemIn(itemIn);
    	
    	if(replacementType == null)
    	{
    		replacementType = GasesFrameworkAPI.lanternTypeEmpty;
    	}
    	
    	world.setBlock(x, y, z, replacementType.block);
    	
    	if(!entityPlayer.capabilities.isCreativeMode && !itemIn.equals(type.itemOut))
    	{
    		if(heldItem != null && replacementType.accepts(itemIn) && --heldItem.stackSize <= 0)
    		{
    			entityPlayer.destroyCurrentEquippedItem();
    		}
    		
    		ItemStack itemStackOut = type.itemOut.itemStack();
    		if(itemStackOut != null && !entityPlayer.inventory.addItemStackToInventory(itemStackOut) && !world.isRemote)
            {
                this.dropBlockAsItem(world, x, y, z, itemStackOut);
            }
    	}
    	
    	return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
    	if(type.expirationRate > 0)
    	{
    		world.setBlockMetadataWithNotify(x, y, z, 15, 3);
    	}
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
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
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
    	if(!world.isRemote && !this.canBlockStay(world, x, y, z))
        {
        	this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return isValidConnection(world, x - 1, y, z) ||
        		isValidConnection(world, x + 1, y, z) ||
        		isValidConnection(world, x, y - 1, z) ||
        		isValidConnection(world, x, y + 1, z) ||
        		isValidConnection(world, x, y, z - 1) ||
        		isValidConnection(world, x, y, z + 1);
    }
    
    public boolean isValidConnection(World world, int x, int y, int z)
    {
    	Block block = world.getBlock(x, y, z);
    	return block.isOpaqueCube() || block instanceof BlockGasPipe;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return canBlockStay(world, x, y, z);
    }
    
    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return An ArrayList containing all items this block drops
     */
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
    	ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    	
    	ret.add(new ItemStack(GasesFrameworkAPI.lanternTypeEmpty.block));
    	if(type.itemOut != null)
    	{
    		ret.add(new ItemStack(type.itemOut.item, 1, type.itemOut.damage));
    	}
    	
    	return ret;
    }
    
    @Override
    public Item getItem(World world, int x, int y, int z)
    {
    	if(type.expirationLanternType != null)
    	{
    		return Item.getItemFromBlock(type.expirationLanternType.block);
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
		switch(side)
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
	public boolean connectToPipe()
	{
		return false;
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		if(canReceiveGas(world, x, y, z, side, gasType))
		{
			world.setBlock(x, y, z, GasesFrameworkAPI.lanternTypesGas[gasType.combustibility.burnRate].block);
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
		return type == GasesFrameworkAPI.lanternTypeGasEmpty && gasType.combustibility != Combustibility.NONE;
	}
}