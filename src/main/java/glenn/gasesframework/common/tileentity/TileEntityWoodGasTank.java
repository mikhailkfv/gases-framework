package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.moddingutils.ForgeDirectionUtil;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityWoodGasTank extends TileEntityGasTank
{
	public TileEntityWoodGasTank()
	{
		super(GasesFramework.configurations.blocks.woodGasTank.storageMultiplier);
	}
	
	@Override
	protected boolean canOverincrement(GasType gasType)
	{
		if (!super.canOverincrement(gasType))
		{
			if (GasesFramework.configurations.blocks.woodGasTank.leaky)
			{
				for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				{
					if (GasesFrameworkAPI.canFillWithGas(worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, gasType))
					{
						return true;
					}
				}
			}

			return false;
		}
		else
		{
			return true;
		}
	}
	
	@Override
	protected boolean overincrement(GasType gasType)
	{
		if (!super.overincrement(gasType))
		{
			if (GasesFramework.configurations.blocks.woodGasTank.leaky)
			{
				for (ForgeDirection direction : ForgeDirectionUtil.shuffledList(worldObj.rand))
				{
					if (GasesFrameworkAPI.fillWithGas(worldObj, worldObj.rand, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, gasType))
					{
						return true;
					}
				}
			}
			
			return false;
		}
		else
		{
			return false;
		}
	}
}
