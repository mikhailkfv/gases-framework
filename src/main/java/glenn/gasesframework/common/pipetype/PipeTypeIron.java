package glenn.gasesframework.common.pipetype;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.pipetype.PipeType;

public class PipeTypeIron extends PipeType
{
	public PipeTypeIron(int pipeID, String name, boolean isSolid, String textureName)
	{
		super(pipeID, name, isSolid, textureName);
	}
	
	public int getPressureTolerance()
	{
		return GasesFramework.configurations.piping_iron_pressureTolerance;
	}
}
