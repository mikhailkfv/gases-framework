package glenn.moddingutils.blockrotation;

import net.minecraftforge.common.util.ForgeDirection;

public enum Yaw
{
	NORTH(ForgeDirection.NORTH),
	WEST(ForgeDirection.WEST),
	SOUTH(ForgeDirection.SOUTH),
	EAST(ForgeDirection.EAST);
	
	public final ForgeDirection direction;
	
	private Yaw(ForgeDirection direction)
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
	
	public Yaw getOpposite()
	{
		return getYaw(this.ordinal() + 2);
	}
	
	public static Yaw getYaw(int rotationIndex)
	{
		return values()[Math.floorMod(rotationIndex, 4)];
	}
}
