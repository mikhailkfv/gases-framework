package glenn.gasesframework.common.item;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.item.IFilterProvider;
import glenn.gasesframework.api.item.ISampler;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemGasSampler extends Item implements ISampler, IFilterProvider
{
	public IIcon overlayIcon;
	public IIcon emptyOverlayIcon;
	
	public ItemGasSampler()
	{
		super();
		this.setHasSubtypes(true);
	}
	
	@Override
	public GasType getGasType(ItemStack itemstack)
	{
		return GasType.getGasTypeByID(itemstack.getItemDamage());
	}
	
	@Override
	public ItemStack setGasType(ItemStack itemstack, GasType gasType)
	{
		if (gasType != null && gasType.isIndustrial)
		{
			itemstack.setItemDamage(GasType.getGasID(gasType));
		}
		return itemstack;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		GasType gasType = getGasType(itemstack);
		if(gasType == GasesFrameworkAPI.gasTypeAir || gasType == null)
		{
			return super.getItemStackDisplayName(itemstack);
		}
		else
		{
			String s = StatCollector.translateToLocal(gasType.getUnlocalizedName() + ".name");
			return StatCollector.translateToLocalFormatted(getUnlocalizedNameInefficiently(itemstack) + ".name.filled", s);
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
		return par2 > 0 ? 0xFFFFFF : this.getColorFromDamage(par1ItemStack.getItemDamage());
	}
	
	@SideOnly(Side.CLIENT)
	public int getColorFromDamage(int par1)
	{
		GasType gasType = GasType.getGasTypeByID(par1);
		return gasType != null ? (gasType.color >> 8) : 0xFFFFFF;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		overlayIcon = iconRegister.registerIcon(this.getIconString() + "_overlay");
		emptyOverlayIcon = iconRegister.registerIcon(this.getIconString() + "_overlay_empty");
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