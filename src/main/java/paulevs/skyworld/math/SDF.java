package paulevs.skyworld.math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SDF
{
	public static float sphereSDF(BlockPos pos, BlockPos center, float radius)
	{
		return (float) Math.sqrt(pos.getSquaredDistance(center)) - radius;
	}
	
	public static float gradient(BlockPos pos, BlockPos center)
	{
		if (pos.getY() <= center.getY())
			return 0;
		else
		{
			return pos.getY() - center.getY();
		}
	}
	
	public static float coneSDF(BlockPos pos, BlockPos center, float height, float rad1, float rad2)
	{
		Vector2F q = new Vector2F(length(pos.getX() - center.getX(), pos.getZ() - center.getZ()), pos.getY() - center.getY());
		Vector2F k1 = new Vector2F(rad2, height);
		Vector2F k2 = new Vector2F(rad2 - rad1, 2F * height);
		Vector2F ca = new Vector2F(q.getX() - Math.min(q.getX(), (q.getY() < 0.0) ? rad1 : rad2), Math.abs(q.getY()) - height);
		Vector2F cb = q.clone().subtract(k1).add(k2.clone().multiple(MathHelper.clamp(k1.clone().subtract(q).dot(k2) / k2.dot(k2), 0F, 1F)));
		float s = (cb.getX() < 0F && ca.getY() < 0F) ? -1F : 1F;
		return s * (float) Math.sqrt(Math.min(ca.dot(ca), cb.dot(cb)));
	}
	
	public static float smoothUnion(float d1, float d2, float k)
	{
		float h = MathHelper.clamp(0.5F + 0.5F * (d2 - d1) / k, 0F, 1F);
		return MathHelper.lerp(h, d2, d1) - k * h * (1F - h);
	}
	
	public static float sdfEllipsoid(BlockPos pos, BlockPos center, float rx, float ry, float rz)
	{
		BlockPos bpos = pos.subtract(center);
		float k0 = length(bpos.getX() / rx, bpos.getY() / ry, bpos.getZ() / rz);
		float k1 = length(bpos.getX() / (rx * rx), bpos.getY() / (ry * ry), bpos.getZ() / (rz * rz));
		return k0 * (k0 - 1) / k1;
	}
	
	public static float length(float a, float b)
	{
		return (float) Math.sqrt(a * a + b * b);
	}
	
	public static float length(float a, float b, float c)
	{
		return (float) Math.sqrt(a * a + b * b + c * c);
	}
}
