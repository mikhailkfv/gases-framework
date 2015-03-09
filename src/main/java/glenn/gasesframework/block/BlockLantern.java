package glenn.gasesframework.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.client.render.RenderBlockLantern;
import glenn.gasesframework.util.LanternRecipe;
import glenn.gasesframework.util.LanternRecipeGas;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLantern extends Block
{
	public int tickrate;
	
	public IIcon topIcon;
	public IIcon sideIcon;
	public IIcon sideConnectedIcon;
	public IIcon connectorsIcon;
	
	protected static final ArrayList<LanternRecipe> lanternRecipes = new ArrayList<LanternRecipe>();
	
	{
		lanternRecipes.add(new LanternRecipeGas());
	}
	
	protected BlockLantern(int tickrate)
	{
		super(Material.wood);
		this.tickrate = tickrate;
		setHardness(0.25F);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 10.0F / 16.0F, 0.75F);
        
        if(tickrate > 0)
        {
        	setTickRandomly(true);
        }
	}
	
	protected BlockLantern(int tickrate, ItemStack containedItemIn)
	{
		this(tickrate);
		
		lanternRecipes.add(new LanternRecipe(containedItemIn, this));
		GasesFramework.queueLanternRecipe(this, containedItemIn);
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
	
	public abstract Block getExpirationBlock();
	
	public abstract ItemStack getContainedItemOut();
	
	public abstract ItemStack getContainedItemIn();
	
	public boolean isValidInItem(ItemStack itemStack)
	{
		for(LanternRecipe lanternRecipe : lanternRecipes)
		{
			if(lanternRecipe.equals(itemStack))
			{
				return true;
			}
		}
		
		return false;
	}

    @Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
		if(tickrate > 0)
		{
			int metadata = par1World.getBlockMetadata(par2, par3, par4);
			if(metadata <= 0)
			{
				par1World.setBlock(par2, par3, par4, getExpirationBlock());
			} else
			{
				par1World.setBlockMetadataWithNotify(par2, par3, par4, metadata - 1, 3);
			}
		}
    }

    @Override
    public int tickRate(World par1World)
    {
        return tickrate;
    }

	/**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
    	ItemStack inUse = par5EntityPlayer.getCurrentEquippedItem();
    	ItemStack containedItem = getContainedItemIn();
    	ItemStack ejectedItem = getContainedItemOut();
    	boolean consumeItem = false;
    	boolean blockAlreadyPlaced = false;
    	
    	boolean isNotEqual = !(!(ejectedItem == null | inUse == null) && (ejectedItem.getItem() == inUse.getItem() & ejectedItem.getItemDamage() == inUse.getItemDamage()));

    	if(inUse != null)
    	{
    		BlockLantern lanternBlock = null;
    		
    		for(LanternRecipe lanternRecipe : lanternRecipes)
    		{
    			if(lanternRecipe.equals(inUse))
    			{
    				lanternBlock = lanternRecipe.getLantern(inUse);
    				break;
    			}
    		}
			
    		if(lanternBlock != null)
			{
				consumeItem = true;
				par1World.setBlock(par2, par3, par4, lanternBlock);
				blockAlreadyPlaced = true;
			}

    		if((consumeItem & isNotEqual) && --inUse.stackSize <= 0)
			{
				par5EntityPlayer.destroyCurrentEquippedItem();
			}
    	}

    	if(ejectedItem != null & isNotEqual)
    	{
    		if(!par5EntityPlayer.inventory.addItemStackToInventory(ejectedItem))
            {
                this.dropBlockAsItem(par1World, par2, par3, par4, ejectedItem);
            }
    	}

		if(!blockAlreadyPlaced)
		{
			par1World.setBlock(par2, par3, par4, GasesFramework.lanternEmpty);
		}

    	return (ejectedItem != null | consumeItem) & isNotEqual;
    }

    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
    	if(tickrate != 0)
    	{
    		par1World.setBlockMetadataWithNotify(par2, par3, par4, 15, 3);
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
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
    	if(!this.canBlockStay(par1World, par2, par3, par4))
        {
        	this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
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
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return canBlockStay(par1World, par2, par3, par4);
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return Item.getItemFromBlock(getExpirationBlock());
    }
    
    @Override
    public Item getItem(World par1World, int par2, int par3, int par4)
    {
        return Item.getItemFromBlock(getExpirationBlock());
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
}