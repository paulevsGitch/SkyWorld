package paulevs.skyworld.math;

import java.util.Locale;

public class Vector2F
{
	private float x;
	private float y;
	
	public Vector2F()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2F(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}
	
	public Vector2F clone()
	{
		return new Vector2F(x, y);
	}

	public Vector2F invert()
	{
		x = -x;
		y = -y;
		return this;
	}
	
	public float getLengthSqared()
	{
		return x * x + y * y;
	}
	
	public float getLength()
	{
		return (float) Math.sqrt(getLengthSqared());
	}

	public Vector2F normalize()
	{
		float l = getLengthSqared();
		if (l > 0)
		{
			l = (float) Math.sqrt(l);
			x /= l;
			y /= l;
		}
		return this;
	}
	
	public String toString()
	{
		return String.format(Locale.ROOT, "[%f, %f]", x, y);
	}

	public Vector2F multiple(float n)
	{
		x *= n;
		y *= n;
		return this;
	}

	public Vector2F add(Vector2F vec)
	{
		x += vec.x;
		y += vec.y;
		return this;
	}
	
	public Vector2F add(float x, float y, float z)
	{
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2F set(Vector2F vec)
	{
		x = vec.x;
		y = vec.y;
		return this;
	}
	
	public Vector2F set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2F subtract(Vector2F vec)
	{
		x -= vec.x;
		y -= vec.y;
		return this;
	}
	
	public float dot(Vector2F vec)
	{
		return x * vec.x + y * vec.y;
	}
	
	public Vector2F rotateCW()
	{
		float nx = y;
		float ny = -x;
		x = nx;
		y = ny;
		return this;
	}
	
	public float distance(Vector2F vec)
	{
		float x = this.x - vec.x;
		float y = this.y - vec.y;
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public float distanceSquared(Vector2F vec)
	{
		float x = this.x - vec.x;
		float y = this.y - vec.y;
		return x * x + y * y;
	}
	
	public float angle(Vector2F vec)
	{
		return (float) Math.acos(this.dot(vec));
	}
	
	public float signAngle(Vector2F vec)
	{
		float angle = angle(vec);
		boolean sign = this.clone().rotateCW().angle(vec) * 2 > Math.PI;
		return sign ? -angle : angle;
	}
	
	@Override
	public int hashCode()
	{
		return ((Float.hashCode(x) & 65535) << 16) | (Float.hashCode(y) & 65535);
	}
}
