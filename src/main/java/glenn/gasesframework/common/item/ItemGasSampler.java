package glenn.gasesframework.common.item;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GFAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.item.IFilterProvider;
import glenn.gasesframework.api.item.ISampler;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemGasSampler extends ItemGasContainer implements ISampler, IFilterProvider
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
		return GasesFramework.registry.getGasTypeByID(itemstack.getItemDamage());
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
		if (gasType == GFAPI.gasTypeAir || gasType == null)
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
		GasType gasType = GasesFramework.registry.getGasTypeByID(par1);
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
	 * Gets an icon index based on an item's damage value and the given render
	 * pass
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
}