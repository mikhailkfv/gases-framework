package glenn.moddingutils.blockrotation;

import java.util.EnumMap;

import glenn.moddingutils.ForgeDirectionUtil;
import net.minecraftforge.common.util.ForgeDirection;

public enum BlockRotation
{
	NORTH_FORWARD(Yaw.NORTH, Pitch.FORWARD, new int[] { 0, 0, 0, 0, 2, 1 }), SOUTH_FORWARD(Yaw.SOUTH, Pitch.FORWARD, new int[] { 3, 3, 0, 0, 1, 2 }), WEST_FORWARD(Yaw.WEST, Pitch.FORWARD, new int[] { 1, 2, 2, 1, 0, 0 }), EAST_FORWARD(Yaw.EAST, Pitch.FORWARD, new int[] { 2, 1, 1, 2, 0, 0 }), NORTH_UP(Yaw.NORTH, Pitch.UP, new int[] { 3, 3, 0, 0, 0, 0 }), SOUTH_UP(Yaw.SOUTH, Pitch.UP, new int[] { 0, 0, 0, 0, 0, 0 }), WEST_UP(Yaw.WEST, Pitch.UP, new int[] { 1, 1, 0, 0, 0, 0 }), EAST_UP(Yaw.EAST, Pitch.UP, new int[] { 2, 2, 0, 0, 0, 0 }), NORTH_BACKWARD(Yaw.NORTH, Pitch.BACKWARD, new int[] { 3, 3, 3, 3, 1, 2 }), SOUTH_BACKWARD(Yaw.SOUTH, Pitch.BACKWARD, new int[] { 0, 0, 3, 3, 2, 1 }), WEST_BACKWARD(Yaw.WEST, Pitch.BACKWARD, new int[] { 2, 1, 1, 2, 3, 3 }), EAST_BACKWARD(Yaw.EAST, Pitch.BACKWARD, new int[] { 1, 2, 2, 1, 3, 3 }), NORTH_DOWN(Yaw.NORTH, Pitch.DOWN, new int[] { 0, 0, 3, 3, 3, 3 }), SOUTH_DOWN(Yaw.SOUTH, Pitch.DOWN, new int[] { 3, 3, 3, 3, 3, 3 }), WEST_DOWN(Yaw.WEST,
			Pitch.DOWN, new int[] { 1, 1, 3, 3, 3, 3 }), EAST_DOWN(Yaw.EAST, Pitch.DOWN, new int[] { 2, 2, 3, 3, 3, 3 });

	public static final BlockRotation PASSIVE_ROTATION;

	private static final BlockRotation[][] lookup = new BlockRotation[Yaw.values().length][Pitch.values().length];

	static
	{
		PASSIVE_ROTATION = SOUTH_UP;

		for (BlockRotation blockFacing : BlockRotation.values())
		{
			registerBlockFacing(blockFacing);
		}
	}

	public final Yaw yaw;
	public final Pitch pitch;
	private final int[] uvRotations;
	private final ForgeDirection[] rotations = new ForgeDirection[ForgeDirection.values().length];
	private final ForgeDirection[] rotationsInverse = new ForgeDirection[ForgeDirection.values().length];

	private BlockRotation(Yaw yaw, Pitch pitch, int[] uvRotations)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		this.uvRotations = new int[] { uvRotations[0], uvRotations[1], uvRotations[4], uvRotations[5], uvRotations[2], uvRotations[3], 0 };

		for (int i = 0; i < rotations.length; i++)
		{
			ForgeDirection direction = ForgeDirection.values()[i];
			direction = ForgeDirectionUtil.rotate(direction, ForgeDirection.UP, yaw.getRotationIndex());
			direction = ForgeDirectionUtil.rotate(direction, ForgeDirection.EAST, pitch.getRotationIndex());
			this.rotations[i] = direction;

			ForgeDirection directionInverse = ForgeDirection.values()[i];
			directionInverse = ForgeDirectionUtil.rotate(directionInverse, ForgeDirection.EAST, -pitch.getRotationIndex());
			directionInverse = ForgeDirectionUtil.rotate(directionInverse, ForgeDirection.UP, -yaw.getRotationIndex());
			this.rotationsInverse[i] = directionInverse;
		}
	}

	public ForgeDirection rotate(ForgeDirection direction)
	{
		return rotations[direction.ordinal()];
	}

	public ForgeDirection rotateInverse(ForgeDirection direction)
	{
		return rotationsInverse[direction.ordinal()];
	}

	public int getUvRotation(ForgeDirection direction)
	{
		return uvRotations[direction.ordinal()];
	}

	public BlockRotation otherPitch(Pitch pitch)
	{
		return getRotation(this.yaw, pitch);
	}

	public BlockRotation otherYaw(Yaw yaw)
	{
		return getRotation(yaw, this.pitch);
	}

	public BlockRotation getPitchOpposite()
	{
		return getRotation(this.yaw, pitch.getOpposite());
	}

	public BlockRotation getYawOpposite()
	{
		return getRotation(this.yaw.getOpposite(), this.pitch);
	}

	public ForgeDirection getDirection()
	{
		if (this.pitch.direction != null)
		{
			return this.pitch.direction;
		}
		else
		{
			return this.yaw.direction;
		}
	}

	private static void registerBlockFacing(BlockRotation blockFacing)
	{
		lookup[blockFacing.yaw.ordinal()][blockFacing.pitch.ordinal()] = blockFacing;
	}

	public static BlockRotation getRotation(int ordinal)
	{
		return values()[ordinal];
	}

	public static BlockRotation getRotation(Yaw yaw, Pitch pitch)
	{
		return lookup[yaw.ordinal()][pitch.ordinal()];
	}

	public static BlockRotation getRotation(float rotationYaw, float rotationPitch)
	{
		int yawRotationIndex = (int) Math.floor((rotationYaw * 4.0F / 360.0F) + 0.5F);
		Yaw yaw = Yaw.getYaw(yawRotationIndex);

		int pitchRotationIndex = (int) Math.floor((rotationPitch * 4.0F / 360.0F) + 0.5F);
		Pitch pitch = Pitch.getPitch(pitchRotationIndex);

		return getRotation(yaw, pitch);
	}
}