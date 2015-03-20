package glenn.gasesframework.common.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@IFMLLoadingPlugin.Name(value = "Gases Framework Core")
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
@TransformerExclusions(value="glenn.gasesframework.common.core")
public class GFFMLLoadingPlugin implements IFMLLoadingPlugin
{
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
	
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{GFClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass()
	{
		return DummyContainerGasesFramework.class.getName();
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		
	}
}
