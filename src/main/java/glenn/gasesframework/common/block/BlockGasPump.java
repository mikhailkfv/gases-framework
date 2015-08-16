package glenn.gasesframework.common.block;

import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.ISample;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;

public abstract class BlockGasPump extends BlockDirectionalGasPropellor
{
	public BlockGasPump(Material material, int maxPressure)
	{
		super(material, true, maxPressure);
	}
}