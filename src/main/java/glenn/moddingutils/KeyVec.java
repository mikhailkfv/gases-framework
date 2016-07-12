package glenn.moddingutils;

public class KeyVec
{
	public final int x, y, z;

	public KeyVec(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public KeyVec(KeyVec other)
	{
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	public KeyVec(IVec vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	@Override
	public boolean equals(Object otherObject)
	{
		if (this == otherObject)
		{
			return true;
		}
		else if (otherObject instanceof KeyVec)
		{
			KeyVec other = (KeyVec) otherObject;
			return this.x == other.x && this.y == other.y && this.z == other.z;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return (new Integer(x).hashCode() * 3) ^ (new Integer(y).hashCode() * 7) ^ (new Integer(z).hashCode() * 31);
	}
}