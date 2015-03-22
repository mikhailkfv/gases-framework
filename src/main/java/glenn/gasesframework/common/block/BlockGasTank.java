package glenn.gasesframework.common.block;

import java.util.ArrayList;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasSource;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.common.item.ItemGasBottle;
import glenn.gasesframework.common.tileentity.TileEntityTank;
import glenn.moddingutils.IVec;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGasTank extends Block implements IGasSource, IGasReceptor, ITileEntityProvider, ISample
{
	public IIcon side;
	public IIcon top;
	public IIcon inside;
	
	public BlockGasTank()
	{
		super(Material.iron);
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
		TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
		
		return tileEntity.containedType != null ? tileEntity.containedType : GasesFrameworkAPI.gasTypeAir;
	}

	@Override
	public GasType takeGasTypeFromSide(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
		GasType gasType = tileEntity.containedType;
		tileEntity.decrement();
		
		return gasType != null ? gasType : GasesFrameworkAPI.gasTypeAir;
	}
	
	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
		return tileEntity.canIncrement(gasType);
	}
	
	@Override
	public boolean receiveGas(World world, int x, int y, int z, ForgeDirection side, GasType gasType)
	{
		TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
		return tileEntity.increment(gasType);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
	   return new TileEntityTank();
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
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, boolean excludes, ForgeDirection side)
	{
		TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
		return tileEntity.containedType;
	}
	
	/**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
		TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
		GasType containedType = tileEntity.containedType;
    	ItemStack inUse = entityPlayer.getCurrentEquippedItem();
    	boolean consumed = false;
    	ItemStack newItem = null;
    	
    	if(inUse != null)
    	{
    		if(inUse.getItem() == Items.glass_bottle)
        	{
        		if(tileEntity.decrement())
        		{
        			consumed = true;
        			newItem = containedType.getBottledItem();
        		}
        	}
        	else if(inUse.getItem() == GasesFrameworkAPI.gasBottle)
        	{
        		GasType heldType = ((ItemGasBottle)GasesFrameworkAPI.gasBottle).getGasType(inUse);
        		if(tileEntity.increment(heldType))
        		{
        			consumed = true;
        			newItem = new ItemStack(Items.glass_bottle);
        		}
        	}
    	}
    	
    	boolean addNewItem = newItem != null;
    	
    	if(consumed)
    	{
    		inUse.stackSize--;
			if(inUse.stackSize <= 0)
			{
				entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
			}
    	}
    	
    	if(addNewItem && !entityPlayer.inventory.addItemStackToInventory(newItem))
		{
			entityPlayer.dropPlayerItemWithRandomChoice(newItem, false);
		}
    	
    	if(newItem == null | inUse == null)
    	{
    		return false;
    	}
    	
    	return consumed & newItem.getItem() != inUse.getItem() & newItem.getItemDamage() != inUse.getItemDamage();
    }
    
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int eventParam)
    {
    	TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
    	if(tileEntity == null) return false;
    	return tileEntity.blockEvent(eventID, eventParam);
    }
    
    /**
	 * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
	 * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
	 * metadata
	 */
    @Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldBlockMetadata)
	{
    	TileEntityTank tileEntity = (TileEntityTank)world.getTileEntity(x, y, z);
    	super.breakBlock(world, x, y, z, oldBlock, oldBlockMetadata);
    	
    	if(tileEntity.containedType != null)
    	{
	    	ArrayList<IVec> stack = new ArrayList<IVec>();
	    	stack.add(new IVec(x, y, z));
	    	int pos = 0;
	    	
	    	while(pos < stack.size())
	    	{
	    		if(tileEntity.amount-- <= 0) break;
	    		
	    		IVec current = stack.get(pos++);
	    		
	    		
	    		world.setBlock(current.x, current.y, current.z, tileEntity.containedType.block);
	    		
	    		for(int side = 0; side < 6; side++)
	    		{
					int xDirection = current.x + (side == 4 ? 1 : (side == 5 ? -1 : 0));
			    	int yDirection = current.y + (side == 0 ? 1 : (side == 1 ? -1 : 0));
			    	int zDirection = current.z + (side == 2 ? 1 : (side == 3 ? -1 : 0));
			    	
			    	if(world.isAirBlock(xDirection, yDirection, zDirection)) stack.add(new IVec(xDirection, yDirection, zDirection));
	    		}
	    	}
    	}
	}

	@Override
	public boolean connectToPipe()
	{
		return true;
	}
}