package glenn.gasesframework.client.render.filter;

import glenn.gasesframework.api.filter.GasTypeFilterSingleIncluding;
import glenn.gasesframework.client.SharedBlockIcons;

public class GasTypeFilterSingleIncludingRenderer extends GasTypeFilterSingleRenderer
{
	protected GasTypeFilterSingleIncludingRenderer(GasTypeFilterSingleIncluding filter)
	{
		super(filter, SharedBlockIcons.circularIncludingOutlineIcon);
	}
}
