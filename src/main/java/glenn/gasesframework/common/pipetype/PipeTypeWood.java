package glenn.gasesframework.common.pipetype;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.pipetype.PipeType;

public class PipeTypeWood extends PipeType
{
	public PipeTypeWood(int pipeID, String name, boolean isSolid, String textureName)
	{
		super(pipeID, name, isSolid, textureName);
	}

	public int getPressureTolerance()
	{
		return GasesFramework.configurations.piping.woodMaterial.pressureTolerance;
	}
}
