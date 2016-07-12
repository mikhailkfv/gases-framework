package glenn.gasesframework.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import glenn.gasesframework.GasesFramework;
import glenn.moddingutils.ForgeDirectionUtil;

public class TileEntityWoodGasDynamo extends TileEntityGasDynamo
{
	public TileEntityWoodGasDynamo()
	{
		super(GasesFramework.configurations.blocks.woodGasDynamo.maxEnergy, GasesFramework.configurations.blocks.woodGasDynamo.maxEnergyTransfer, GasesFramework.configurations.blocks.woodGasDynamo.maxFuel, GasesFramework.configurations.blocks.woodGasDynamo.fuelPerTick, GasesFramework.configurations.blocks.woodGasDynamo.energyPerFuel);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (GasesFramework.configurations.blocks.woodGasFurnace.catchesFire)
		{
			if (!worldObj.isRemote && isBurning() && worldObj.rand.nextInt(200) == 0)
			{
				List<ForgeDirection> directions = ForgeDirectionUtil.shuffledList(worldObj.rand);
				for (ForgeDirection direction : directions)
				{
					int x = xCoord + direction.offsetX;
					int y = yCoord + direction.offsetY;
					int z = zCoord + direction.offsetZ;

					Block block = worldObj.getBlock(x, y, z);
					if (block.isReplaceable(worldObj, x, y, z) && Blocks.fire.canPlaceBlockAt(worldObj, x, y, z))
					{
						worldObj.setBlock(x, y, z, Blocks.fire);
						break;
					}
				}
			}
		}
	}
}