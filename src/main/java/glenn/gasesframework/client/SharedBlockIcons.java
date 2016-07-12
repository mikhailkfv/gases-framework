package glenn.gasesframework.client;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class SharedBlockIcons
{
	public static IIcon circularTypeIndicatorIcon;
	public static IIcon circularExcludingOutlineIcon;
	public static IIcon circularIncludingOutlineIcon;

	public static void registerIcons(IIconRegister iconRegister)
	{
		circularTypeIndicatorIcon = iconRegister.registerIcon("gasesframework:filter_circular_indicator");
		circularExcludingOutlineIcon = iconRegister.registerIcon("gasesframework:filter_circular_outline_excluding");
		circularIncludingOutlineIcon = iconRegister.registerIcon("gasesframework:filter_circular_outline_including");
	}
}
