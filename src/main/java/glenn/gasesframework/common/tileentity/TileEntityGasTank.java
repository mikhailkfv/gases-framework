package glenn.gasesframework.common.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GFAPI;
import glenn.gasesframework.api.gastype.GasType;
import glenn.moddingutils.ForgeDirectionUtil;
import glenn.moddingutils.IVec;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityGasTank extends TileEntity
{
	public static final int SET_AMOUNT = 0;
	public static final int SET_TYPE = 1;

	private final int storageMultiplier;

	private GasType gasTypeStored;
	private int gasStored;

	public double[][] ps;
	public double[][] vs;

	private static Random rand = new Random();

	public TileEntityGasTank(int storageMultiplier)
	{
		this.storageMultiplier = storageMultiplier;

		this.gasTypeStored = null;
		this.gasStored = 0;

		if (GasesFramework.configurations.blocks.gasTanks.fancyTank)
		{
			ps = new double[9][9];
			vs = new double[9][9];
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		gasStored = tagCompound.getInteger("amount");
		gasTypeStored = GasesFramework.registry.getGasTypeByID(tagCompound.getInteger("containedType"));

		if (GasesFramework.configurations.blocks.gasTanks.fancyTank)
		{
			double gasHeight = getRelativeGasStored();
			for (double[] fs : ps)
			{
				Arrays.fill(fs, gasHeight);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("amount", gasStored);
		tagCompound.setInteger("containedType", GasType.getGasID(gasTypeStored));
	}

	/**
	 * Overriden in a sign to provide the text.
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	/**
	 * Called when you receive a TileEntityData packet for the location this
	 * TileEntity is currently in. On the client, the NetworkManager will always
	 * be the remote server. On the server, it will be whomever is responsible
	 * for sending the packet.
	 *
	 * @param net
	 *            The NetworkManager the packet originated from
	 * @param packet
	 *            The data packet
	 */
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		readFromNBT(packet.func_148857_g());
	}

	private double get(int x, int y)
	{
		if (x < 0)
			x = 0;
		else if (x > 8)
			x = 8;
		if (y < 0)
			y = 0;
		else if (y > 8)
			y = 8;
		return ps[x][y];
	}

	private void wobble(double direction)
	{
		if (!GasesFramework.configurations.blocks.gasTanks.fancyTank)
		{
			return;
		}

		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				vs[i][j] += direction * (rand.nextDouble() * 3.0D - 1.0D);
			}
		}
	}

	@Override
	public void updateEntity()
	{
		if (!worldObj.isRemote)
		{
			tryFlowDown();
		}

		if (worldObj.isRemote && GasesFramework.configurations.blocks.gasTanks.fancyTank)
		{
			updateFancyTank();
		}
	}

	private void updateFancyTank()
	{
		double gasHeight = getRelativeGasStored();
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				vs[i][j] = vs[i][j];
				vs[i][j] += (gasHeight - ps[i][j]) * 0.4D;
				vs[i][j] += (get(i - 1, j) + get(i, j + 1) + get(i + 1, j) + get(i, j - 1) - ps[i][j] * 4) * 0.05D;
				vs[i][j] += (get(i - 1, j) + get(i, j + 1) + get(i + 1, j) + get(i, j - 1) - ps[i][j] * 4) * 0.035355D;
				vs[i][j] *= 0.9D;
			}
		}

		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				ps[i][j] += vs[i][j];

				if (ps[i][j] < 0.0D)
				{
					ps[i][j] = 0.0D;
					vs[i][j] = -vs[i][j];
				}
				else if (ps[i][j] > 1.0D)
				{
					ps[i][j] = 1.0D;
					vs[i][j] = -vs[i][j];
				}
			}
		}
	}

	private void tryFlowDown()
	{
		if (!isEmpty())
		{
			TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
			if (tileEntity instanceof TileEntityGasTank)
			{
				TileEntityGasTank tileEntityTank = (TileEntityGasTank) tileEntity;
				if (tileEntityTank.increment(gasTypeStored))
				{
					decrement();
				}
			}
		}
	}

	public void emptyInAir()
	{
		ArrayList<IVec> stack = new ArrayList<IVec>();
		stack.add(new IVec(xCoord, yCoord, zCoord));
		int pos = 0;

		while (!isEmpty() && pos < stack.size())
		{
			IVec current = stack.get(pos++);

			GasesFramework.implementation.placeGas(worldObj, current.x, current.y, current.z, gasTypeStored, 16);
			gasStored--;

			for (ForgeDirection direction : ForgeDirectionUtil.shuffledList(worldObj.rand))
			{
				IVec branch = current.added(ForgeDirectionUtil.getOffsetVec(direction));
				Block block = worldObj.getBlock(branch.x, branch.y, branch.z);
				if (block.isReplaceable(worldObj, branch.x, branch.y, branch.z))
				{
					stack.add(branch);
				}
			}
		}
	}

	public int getMaxGasStored()
	{
		if (gasTypeStored != null)
		{
			return ((64 - gasTypeStored.density) * 2 + 16) * storageMultiplier;
		}
		else
		{
			return 0;
		}
	}

	public double getRelativeGasStored()
	{
		return getMaxGasStored() != 0 ? (double) getGasStored() / getMaxGasStored() : 0.0D;
	}

	public int getGasStored()
	{
		if (gasTypeStored != null)
		{
			return gasStored;
		}
		else
		{
			return 0;
		}
	}

	public GasType getGasTypeStored()
	{
		return gasTypeStored;
	}

	public boolean isEmpty()
	{
		return getGasStored() <= 0;
	}

	public boolean isFull()
	{
		if (gasTypeStored != null)
		{
			return gasStored >= getMaxGasStored();
		}
		else
		{
			return false;
		}
	}

	public boolean canIncrement(GasType gasType)
	{
		if (gasType == null || gasType == GFAPI.gasTypeAir)
		{
			return true;
		}
		else if (isEmpty())
		{
			return true;
		}
		else if (gasTypeStored == gasType)
		{
			if (gasStored < getMaxGasStored())
			{
				return true;
			}
			else
			{
				return canOverincrement(gasType);
			}
		}
		else
		{
			return false;
		}
	}

	protected boolean canOverincrement(GasType gasType)
	{
		TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
		if (tileEntity instanceof TileEntityGasTank)
		{
			TileEntityGasTank tileEntityGasTank = (TileEntityGasTank) tileEntity;
			return tileEntityGasTank.canIncrement(gasType);
		}
		else
		{
			return false;
		}
	}

	public boolean increment(GasType gasType)
	{
		if (gasType == null || gasType == GFAPI.gasTypeAir)
		{
			return true;
		}
		else if (isEmpty())
		{
			gasTypeStored = gasType;
			gasStored++;
			sync();

			return true;
		}
		else if (gasTypeStored == gasType)
		{
			if (gasStored < getMaxGasStored())
			{
				gasStored++;
				sync();

				return true;
			}
			else
			{
				return overincrement(gasType);
			}
		}
		else
		{
			return false;
		}
	}

	protected boolean overincrement(GasType gasType)
	{
		TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
		if (tileEntity instanceof TileEntityGasTank)
		{
			TileEntityGasTank tileEntityGasTank = (TileEntityGasTank) tileEntity;
			return tileEntityGasTank.increment(gasType);
		}
		else
		{
			return false;
		}
	}

	public boolean canDecrement()
	{
		return !isEmpty();
	}

	public boolean decrement()
	{
		if (gasStored > 0)
		{
			gasStored--;
			if (isEmpty())
			{
				gasTypeStored = null;
			}
			sync();

			return true;
		}
		else
		{
			return false;
		}
	}

	private void sync()
	{
		if (!worldObj.isRemote)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), SET_AMOUNT, gasStored);
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), SET_TYPE, GasType.getGasID(gasTypeStored));
		}
	}

	public boolean blockEvent(int eventID, int eventParam)
	{
		if (!worldObj.isRemote)
			return true;

		switch (eventID)
		{
			case SET_AMOUNT:
				if (gasStored < eventParam)
					wobble(0.005D);
				else if (gasStored > eventParam)
					wobble(-0.005D);
				gasStored = eventParam;
				break;
			case SET_TYPE:
				gasTypeStored = GasesFramework.registry.getGasTypeByID(eventParam);
				break;
		}
		return true;
	}
}