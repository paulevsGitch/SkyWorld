package paulevs.skyworld.math;

import java.util.Random;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import paulevs.skyworld.noise.OpenSimplexNoise;

public class MHelper
{
	private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);
	
	public static int getSquaredRange(int min, int max, Random random)
	{
		float a = random.nextFloat();
		return Math.round(min + (max - min) * a * a);
	}
	
	public static float noise(BlockPos pos, double scale)
	{
		return (float) NOISE.eval(pos.getX() * scale, pos.getY() * scale, pos.getZ() * scale) * 0.5F + 0.5F;
	}
	
	public static float noise(BlockPos pos, double scale, int octaves)
	{
		float result = noise(pos, scale);
		float delta = 0.6F;
		for (int i = 0; i < octaves; i++)
		{
			scale *= 2;
			delta *= 0.5F;
			result = MathHelper.lerp(delta, result, noise(pos, scale));
		}
		return result;
	}
	
	public static BlockBox intersection(BlockBox box1, BlockBox box2)
	{
		int x1 = Math.max(box1.minX, box2.minX);
		int y1 = Math.max(box1.minY, box2.minY);
		int z1 = Math.max(box1.minZ, box2.minZ);
		int x2 = Math.min(box1.maxX, box2.maxX);
		int y2 = Math.min(box1.maxY, box2.maxY);
		int z2 = Math.min(box1.maxZ, box2.maxZ);
		return new BlockBox(x1, y1, z1, x2, y2, z2);
	}
	
	public static int randRange(int min, int max, Random random)
	{
		return min + random.nextInt(max - min + 1);
	}
}
