package glenn.gasesframework.item;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.type.GasType;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemGasBottle extends Item
{
	public IIcon overlayIcon;
	
	public ItemGasBottle()
	{
		super();
        this.setHasSubtypes(true);
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
		itemIcon = par1IconRegister.registerIcon(this.getIconString() + "_empty");
		overlayIcon = par1IconRegister.registerIcon(this.getIconString() + "_overlay");
    }
	
	@SideOnly(Side.CLIENT)
    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @Override
    public IIcon getIconFromDamageForRenderPass(int par1, int par2)
    {
        return par2 == 0 ? overlayIcon : itemIcon;
    }
	
	@SideOnly(Side.CLIENT)
    /**
     * Gets an icon index based on an item's damage value
     */
    @Override
    public IIcon getIconFromDamage(int par1)
    {
        return this.itemIcon;
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
        	if(type.isIndustrial && type != GasesFrameworkAPI.gasTypeAir)
        	{
        		itemList.add(new ItemStack(item, 1, type.gasID));
        	}
        }
    }
	
	@Override
	public int getItemStackLimit()
	{
		return 16;
	}
	
	public GasType getGasType(ItemStack itemStack)
	{
		return GasType.getGasTypeByID(itemStack.getItemDamage());
	}
}