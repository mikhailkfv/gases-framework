package glenn.gasesframework.api;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialGas extends Material
{
	public static final Material INSTANCE = new MaterialGas(MapColor.airColor);

	protected MaterialGas(MapColor mapColor)
	{
		super(mapColor);
		this.setReplaceable();
		this.setNoPushMobility();
	}

	@Override
	public boolean isSolid()
	{
		return false;
	}

	/**
	 * Returns if this material is considered solid or not
	 */
	@Override
	public boolean blocksMovement()
	{
		return false;
	}

	@Override
	public boolean getCanBlockGrass()
	{
		return false;
	}
}