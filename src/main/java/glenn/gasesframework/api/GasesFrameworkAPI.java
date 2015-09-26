package glenn.gasesframework.api;

import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gastype.GasTypeAir;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

/**
 * <b>The Gases Framework API</b>
 * <br>
 * <br>
 * <i>This API will work both with and without the Gases Framework installed. Certain methods will not function properly if the mod is not installed.</i>
 * <br>
 * <ul>
 * <li>You can determine the mod installation state by a query to {@link #isInstalled()}.</li>
 * <li><b>IMPORTANT NOTE: To ensure the API will work properly when the mod is loaded, your mod must have the following added to its {@link cpw.mods.fml.common.Mod Mod} annotation:</b><br>
 * <i>{@link cpw.mods.fml.common.Mod#dependencies dependencies}="after:gasesFrameworkCore"</i></li>
 * <li>If you want the mod to work only if the Gases Framework mod installed, add the following instead:</br>
 * <i>{@link cpw.mods.fml.common.Mod#dependencies dependencies}="require-after:gasesFrameworkCore"</i></li>
 * <br>
 * </ul>
 * This piece of software is covered under the LGPL license. Redistribution and modification is permitted.
 * @author Erlend
 * @author Trent
 */
public class GasesFrameworkAPI
{
	public static final String OWNER = "gasesFramework";
	public static final String VERSION = "1.1.2";
	public static final String TARGETVERSION = "1.7.10";
	public static final String PROVIDES = "gasesFrameworkAPI";

	private static boolean isInstalled = false;
	
	/**
	 * The actual implementation of the Gases Framework. This serves as a connection point between the API and the functionality of the mod.
	 * If the mod is not installed, this will be a dummy implementation.
	 */
	public static IGasesFrameworkImplementation implementation = new DummyImplementation();
	/**
	 * The registry of the Gases Framework. This serves as a connection point between the API and the registry of the mod.
	 * If the mod is not installed, this will be a dummy registry.
	 */
	public static IGasesFrameworkRegistry registry = new DummyRegistry();
	
	/**
	 * The default overlay image used when the player is submerged in gas.
	 */
	public static final ResourceLocation gasOverlayImage = new ResourceLocation("gasesframework:textures/misc/gas_overlay.png");
	/**
	 * The overlay image used when the player is inside ignited gas.
	 */
	public static final ResourceLocation fireOverlayImage = new ResourceLocation("gasesframework:textures/misc/fire_overlay.png");
	/**
	 * An empty overlay image used when the player is submerged in gas.
	 */
	public static final ResourceLocation emptyOverlayImage = new ResourceLocation("gasesframework:textures/misc/empty_overlay.png");

	/**
	 * The gas type for air. The Gases Framework will register this.
	 */
	public static final GasType gasTypeAir = new GasTypeAir();
	/**
	 * The damage source used when a player asphyxiates in gas.
	 */
	public static final DamageSource asphyxiationDamageSource = new DamageSource("gf_asphyxiation");

	/**
	 * Returns true if an implementation of the Gases Framework is installed.
	 * This method may give false negatives if Gases Framework is loaded after this method is called. See {@link GasesFrameworkAPI}.
	 * @return True if the Gases Framework is installed
	 */
	public static boolean isInstalled()
	{
		return isInstalled;
	}

	/**
	 * Install a Gases Framework implementation and registry. Used by the Gases Framework.
	 * @param implementation The Gases Framework implementation
	 * @param registry The Gases Framework registry
	 */
	public static void install(IGasesFrameworkImplementation implementation, IGasesFrameworkRegistry registry)
	{
		GasesFrameworkAPI.implementation = implementation;
		GasesFrameworkAPI.registry = registry;
		GasesFrameworkAPI.isInstalled = true;
	}
}