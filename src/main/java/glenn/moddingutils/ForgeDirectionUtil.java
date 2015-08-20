package glenn.moddingutils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.util.ForgeDirection;

public class ForgeDirectionUtil
{
	public static List<ForgeDirection> shuffledList(Random random)
	{
		return shuffledList(random, ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST);
	}
	
	public static List<ForgeDirection> shuffledList(Random random, ForgeDirection... directionsArray)
	{
		List<ForgeDirection> directions = Arrays.asList(directionsArray);
		Collections.shuffle(directions, random);
		return directions;
	}
}
