package glenn.gasesframework.client.render.filter;

import glenn.gasesframework.api.filter.GasTypeFilterSingleExcluding;
import glenn.gasesframework.client.SharedBlockIcons;

public class GasTypeFilterSingleExcludingRenderer extends GasTypeFilterSingleRenderer
{
	protected GasTypeFilterSingleExcludingRenderer(GasTypeFilterSingleExcluding filter)
	{
		super(filter, SharedBlockIcons.circularExcludingOutlineIcon);
	}
}
