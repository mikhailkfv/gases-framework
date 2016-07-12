package glenn.moddingutils;

import java.util.Random;

import net.minecraft.util.MathHelper;

public class FVec
{
	public float x;
	public float y;
	public float z;

	public FVec()
	{
		this.x = 0.0F;
		this.y = 0.0F;
		this.z = 0.0F;
	}

	public FVec(IVec vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public FVec(FVec vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public FVec(DVec vec)
	{
		this.x = (float) vec.x;
		this.y = (float) vec.y;
		this.z = (float) vec.z;
	}

	public FVec(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public FVec set(FVec vec)
	{
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;

		return this;
	}

	public FVec set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public FVec add(FVec vec)
	{
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;

		return this;
	}

	public FVec added(FVec vec)
	{
		return this.clone().add(vec);
	}

	public FVec add(float x, float y, float z)
	{
		this.x += x;
		this.y += y;
		this.z += z;

		return this;
	}

	public FVec added(float x, float y, float z)
	{
		return this.clone().add(x, y, z);
	}

	public FVec subtract(FVec vec)
	{
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;

		return this;
	}

	public FVec subtracted(FVec vec)
	{
		return this.clone().subtract(vec);
	}

	public FVec subtract(float x, float y, float z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;

		return this;
	}

	public FVec subtracted(float x, float y, float z)
	{
		return this.clone().subtract(x, y, z);
	}

	public FVec multiply(FVec vec)
	{
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;

		return this;
	}

	public FVec multiplied(FVec vec)
	{
		return this.clone().multiply(vec);
	}

	public FVec multiply(float x, float y, float z)
	{
		this.x *= x;
		this.y *= y;
		this.z *= z;

		return this;
	}

	public FVec multiplied(float x, float y, float z)
	{
		return this.clone().multiply(x, y, z);
	}

	public FVec divide(FVec vec)
	{
		this.x /= vec.x;
		this.y /= vec.y;
		this.z /= vec.z;

		return this;
	}

	public FVec divided(FVec vec)
	{
		return this.clone().divide(vec);
	}

	public FVec divide(float x, float y, float z)
	{
		this.x /= x;
		this.y /= y;
		this.z /= z;

		return this;
	}

	public FVec divided(float x, float y, float z)
	{
		return this.clone().divide(x, y, z);
	}

	public float fotProduct(FVec vec)
	{
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	public float fotProduct(float x, float y, float z)
	{
		return this.x * x + this.y * y + this.z * z;
	}

	public FVec scale(float f)
	{
		this.x *= f;
		this.y *= f;
		this.z *= f;

		return this;
	}

	public FVec scaled(float f)
	{
		return clone().scale(f);
	}

	public FVec iScale(float f)
	{
		this.x /= f;
		this.y /= f;
		this.z /= f;

		return this;
	}

	public FVec iScaled(float f)
	{
		return this.clone().iScale(f);
	}

	public FVec invert()
	{
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;

		return this;
	}

	public FVec inverted()
	{
		return this.clone().invert();
	}

	public FVec normalize()
	{
		return this.iScale(length());
	}

	public FVec normalized()
	{
		return clone().normalize();
	}

	public FVec xRotate(float f)
	{
		float c = (float) Math.cos(f);
		float s = (float) Math.sin(f);

		float ty = this.y;
		float tz = this.z;

		this.y = c * ty + s * tz;
		this.z = c * tz - s * ty;

		return this;
	}

	public FVec xRotated(float f)
	{
		return this.clone().xRotate(f);
	}

	public FVec yRotate(float f)
	{
		float c = (float) Math.cos(f);
		float s = (float) Math.sin(f);

		float tx = this.x;
		float tz = this.z;

		this.x = c * tx + s * tz;
		this.z = c * tz - s * tx;

		return this;
	}

	public FVec yRotated(float f)
	{
		return this.clone().yRotate(f);
	}

	public FVec zRotate(float f)
	{
		float c = (float) Math.cos(f);
		float s = (float) Math.sin(f);

		float tx = this.x;
		float ty = this.y;

		this.x = c * tx + s * ty;
		this.y = c * ty - s * tx;

		return this;
	}

	public FVec zRotated(float f)
	{
		return this.clone().zRotate(f);
	}

	public float squaredLength()
	{
		return x * x + y * y + z * z;
	}

	public float length()
	{
		return MathHelper.sqrt_float(squaredLength());
	}

	public FVec clone()
	{
		return new FVec(this.x, this.y, this.z);
	}

	public boolean isNull()
	{
		return this.x == 0.0F & this.y == 0.0F & this.z == 0.0F;
	}

	public DVec2 xy()
	{
		return new DVec2(this.x, this.y);
	}

	public DVec2 xz()
	{
		return new DVec2(this.x, this.z);
	}

	public DVec2 yz()
	{
		return new DVec2(this.y, this.z);
	}

	public static FVec randomNormalizedVec(Random random)
	{
		return new FVec(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalized();
	}

	public static FVec cross(FVec a, FVec b)
	{
		return new FVec(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}

	public static float dot(FVec a, FVec b)
	{
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
}