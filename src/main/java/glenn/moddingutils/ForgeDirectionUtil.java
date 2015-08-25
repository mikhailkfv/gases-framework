package glenn.moddingutils;

import java.util.HashMap;

import net.minecraftforge.common.util.ForgeDirection;

public class ForgeDirectionUtil
{
	private static HashMap<KeyVec, ForgeDirection> offsetLookup = new HashMap<KeyVec, ForgeDirection>();
	
	private static ForgeDirection[][] crossLookup = new ForgeDirection[ForgeDirection.values().length][ForgeDirection.values().length];
	
	private static ForgeDirection[][][] rotationLookup = new ForgeDirection[ForgeDirection.values().length][ForgeDirection.values().length][4];
	
	static
	{
		for (ForgeDirection direction : ForgeDirection.values())
		{
			offsetLookup.put(new KeyVec(getOffsetVec(direction)), direction);
		}
		
		for (ForgeDirection a : ForgeDirection.values())
		{
			IVec aVec = getOffsetVec(a);
			for (ForgeDirection b : ForgeDirection.values())
			{
				IVec bVec = getOffsetVec(b);
				IVec crossVec = IVec.cross(aVec, bVec);
				
				crossLookup[a.ordinal()][b.ordinal()] = getByOffsetVec(crossVec);
			}
		}
		
		for (ForgeDirection a : ForgeDirection.values())
		{
			for (ForgeDirection b : ForgeDirection.values())
			{
				ForgeDirection c = b;
				for (int i = 0; i < 4; i++)
				{
					rotationLookup[a.ordinal()][b.ordinal()][i] = c;
					c = c.getRotation(a);
				}
			}
		}
		
		IVec kek = getOffsetVec(ForgeDirection.DOWN);
	}
	
	public static ForgeDirection getByOffsetVec(IVec vec)
	{
		return offsetLookup.get(new KeyVec(vec));
	}
	
	public static IVec getOffsetVec(ForgeDirection direction)
	{
		return new IVec(direction.offsetX, direction.offsetY, direction.offsetZ);
	}
	
	public static ForgeDirection cross(ForgeDirection a, ForgeDirection b)
	{
		return crossLookup[a.ordinal()][b.ordinal()];
	}
	
	public static ForgeDirection rotate(ForgeDirection direction, ForgeDirection axis, int units)
	{
		return rotationLookup[axis.ordinal()][direction.ordinal()][Math.floorMod(units, 4)];
	}
}