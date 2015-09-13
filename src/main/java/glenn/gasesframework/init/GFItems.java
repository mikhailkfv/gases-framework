package glenn.gasesframework.init;

import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.common.item.ItemDuctTape;
import glenn.gasesframework.common.item.ItemGasBottle;
import glenn.gasesframework.common.item.ItemGasSamplerExcluding;
import glenn.gasesframework.common.item.ItemGasSamplerIncluding;
import glenn.gasesframework.common.item.ItemPrimitiveAdhesive;
import net.minecraft.item.Item;

public class GFItems
{
	public final Item gasBottle = new ItemGasBottle().setUnlocalizedName("gf_gasBottle").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:gas_bottle");
	public final Item gasSamplerIncluder = new ItemGasSamplerIncluding().setUnlocalizedName("gf_gasSamplerIncluder").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:sampler_including");
	public final Item gasSamplerExcluder = new ItemGasSamplerExcluding().setUnlocalizedName("gf_gasSamplerExcluder").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:sampler_excluding");
	public final Item adhesive = new ItemPrimitiveAdhesive().setUnlocalizedName("gf_adhesive").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:adhesive");
	public final Item ductTape = new ItemDuctTape().setUnlocalizedName("gf_ductTape").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:duct_tape");

	public GFItems()
	{
		for (Field field : getClass().getFields())
		{
			try
			{
				GameRegistry.registerItem((Item)field.get(this), field.getName());
			}
			catch (IllegalAccessException e)
			{}
		}
	}
}
