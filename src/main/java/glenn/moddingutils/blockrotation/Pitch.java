package glenn.moddingutils.blockrotation;

import glenn.moddingutils.ForgeDirectionUtil;
import net.minecraftforge.common.util.ForgeDirection;

public enum Pitch
{
	FORWARD(ForgeDirection.SOUTH), UP(ForgeDirection.UP), BACKWARD(ForgeDirection.NORTH), DOWN(ForgeDirection.DOWN);

	private static final int[] opposites = { 0, 2, 1, 3 };

	public final ForgeDirection direction;

	private Pitch(ForgeDirection direction)
	{
		this.direction = direction;
	}

	public int getRotationIndex()
	{
		return ordinal();
	}

	public int getRotationDegrees()
	{
		return getRotationIndex() * 90;
	}

	public Pitch getOpposite()
	{
		return getPitch(opposites[this.ordinal()]);
	}

	public static Pitch getPitch(int rotationIndex)
	{
		return values()[ForgeDirectionUtil.mod4(rotationIndex)];
	}
}