package glenn.gasesframework.api.pipetype;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class PipeType
{
	/**
	 * The ID of this pipe type. Must be unique. Limited to 0-15. Consult the Gases Framework documentation for unoccupied IDs.
	 */
	public final int pipeID;
	/**
	 * A name for this pipe type. Must be unique.
	 */
	public final String name;
	/**
	 * Will this gas pipe hide its contents?
	 */
	public final boolean isSolid;
	/**
	 * The base name of the icons used by this pipe type. It will be prepended to the icon location strings.
	 */
	public final String textureName;
	
	/**
	 * The icon used for the solid part of the pipe.
	 * Name: {@link PipeType#textureName textureName} + "_solid"
	 */
	public IIcon solidIcon;
	/**
	 * The icon used for the gas content of the pipe if it is not {@link PipeType#isSolid solid}. It will be colored according to the gas content.
	 * Name: {@link PipeType#textureName textureName} + "_gas_content"
	 */
	public IIcon gasContentIcon;
	/**
	 * The icon used for the connectors that connect the pipe with surrounding blocks.
	 * Name: {@link PipeType#textureName textureName} + "_connectors"
	 */
	public IIcon connectorsIcon;
	/**
	 * The icon used for the ends of the pipe.
	 * Name: {@link PipeType#textureName textureName} + "_end"
	 */
	public IIcon endIcon;

	/**
	 * Creates a new pipe type. Pipe types must be {@link glenn.gasesframework.api.IGasesFrameworkRegistry#registerPipeType(PipeType) registered}.
	 * @param pipeID - The ID of this pipe type. Must be unique. Limited to 0-15. Consult the Gases Framework documentation for unoccupied IDs.
	 * @param name - An unique name for the pipe type.
	 * @param isSolid - Will this pipe hide its contents?
	 * @param textureName - The base name of the textures used by this pipe type. It will be prepended to the icon location strings.
	 */
	public PipeType(int pipeID, String name, boolean isSolid, String textureName)
	{
		this.pipeID = pipeID;
		this.name = name;
		this.isSolid = isSolid;
		this.textureName = textureName;
	}
	
	public void registerIcons(IIconRegister iconRegister)
	{
		solidIcon = iconRegister.registerIcon(textureName + "_solid");
		if(!isSolid) gasContentIcon = iconRegister.registerIcon(textureName + "_gas_content");
		connectorsIcon = iconRegister.registerIcon(textureName + "_connectors");
		endIcon = iconRegister.registerIcon(textureName + "_end");
	}

	/**
	 * Get the unlocalized name of the pipe.
	 * @return "gf_gas." + name;
	 */
	public String getUnlocalizedName()
	{
		return "gf_gasPipe." + name;
	}
	
	/**
	 * Get the pressure this pipe can handle before bursting. If -1, it can handle any pressure.
	 */
	public int getPressureTolerance()
	{
		return -1;
	}
}