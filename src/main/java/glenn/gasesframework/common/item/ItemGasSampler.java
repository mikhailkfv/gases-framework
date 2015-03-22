package glenn.gasesframework.common.item;

import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.gastype.GasType;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemGasSampler extends Item
{
	public boolean excludes;
	public IIcon overlayIcon;
	public IIcon emptyOverlayIcon;
	
	public ItemGasSampler(boolean excludes)
	{
		super();
		this.excludes = excludes;
        this.setHasSubtypes(true);
	}
	
	public GasType getGasType(ItemStack itemstack)
	{
		return GasType.getGasTypeByID(itemstack.getItemDamage());
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		if(itemstack.getItemDamage() > 0)
		{
			String s = StatCollector.translateToLocal(getGasType(itemstack).getUnlocalizedName() + ".name");
			return StatCollector.translateToLocalFormatted(getUnlocalizedNameInefficiently(itemstack) + ".name.filled", s);
		}
		return super.getItemStackDisplayName(itemstack);
    }
	
	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
	@Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityPlayer)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, entityPlayer, true);

        if(movingobjectposition == null)
        {
            return itemstack;
        }
        else
        {
            if(movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;
                Block block = world.getBlock(i, j, k);
                
                if(ISample.class.isAssignableFrom(block.getClass()))
                {
                	ISample sample = (ISample)block;
                	GasType newType = sample.sampleInteraction(world, i, j, k, getGasType(itemstack), excludes, ForgeDirection.getOrientation(movingobjectposition.sideHit));
                	
                	if(!(newType == null || !newType.isIndustrial))
                	{
                		itemstack.setItemDamage(newType.gasID);
                	}
                	else
                	{
                		itemstack.setItemDamage(0);
                	}
                }
            }
            
            return itemstack;
        }
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return par2 > 0 ? 16777215 : this.getColorFromDamage(par1ItemStack.getItemDamage());
    }
	
	@SideOnly(Side.CLIENT)
    public int getColorFromDamage(int par1)
    {
		if(par1 > 0)
		{
			return GasType.getGasTypeByID(par1).color >> 8;
		}
		else
		{
			return 16777215;
		}
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
		itemIcon = par1IconRegister.registerIcon(this.getIconString() + (excludes ? "_black" : "_white"));
		overlayIcon = par1IconRegister.registerIcon(this.getIconString() + "_overlay");
		emptyOverlayIcon = par1IconRegister.registerIcon(this.getIconString() + "_overlay_empty");
    }
	
	@SideOnly(Side.CLIENT)
    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
	@Override
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 0 ? (par1 > 0 ? overlayIcon : emptyOverlayIcon) : itemIcon;
    }
	
	@SideOnly(Side.CLIENT)
    /**
     * Gets an icon index based on an item's damage value
     */
	@Override
    public IIcon getIconFromDamage(int par1)
    {
        return itemIcon;
    }
	
	@SideOnly(Side.CLIENT)

	/**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List itemList)
    {
		GasType[] allTypes = GasType.getAllTypes();
        for (GasType type : allTypes)
        {
        	if(type.isIndustrial)
        	{
        		itemList.add(new ItemStack(item, 1, type.gasID));
        	}
        }
    }
}