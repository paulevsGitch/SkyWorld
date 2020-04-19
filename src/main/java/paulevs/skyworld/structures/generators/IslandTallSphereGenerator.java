package paulevs.skyworld.structures.generators;

import java.util.Random;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.math.SDF;

public class IslandTallSphereGenerator extends IslandGenerator
{
	private float noisePower;
	private float noiseScale;
	private int radius2;
	
	@Override
	public void initValues(BlockPos center, int radius)
	{
		this.noisePower = radius / 6F;
		this.noiseScale = 0.5F / (float) Math.log(radius);
		this.radius2 = Math.round(radius * 0.3F);
	}

	@Override
	public void setBoundingBox(BlockBox box, BlockPos center, int radius)
	{
		box.minX = center.getX() - radius2;
		box.minY = center.getY() - radius;
		box.minZ = center.getZ() - radius2;
		box.maxX = center.getX() + radius2;
		box.maxY = center.getY() + radius;
		box.maxZ = center.getZ() + radius2;
	}

	@Override
	public void generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos, BlockPos center, int radius)
	{
		HEIGHTMAP.clear();
		for (int x = box.minX; x <= box.maxX; x++)
		{
			B_POS.setX(x);
			for (int z = box.minZ; z <= box.maxZ; z++)
			{
				B_POS.setZ(z);
				SurfaceConfig config = world.getBiome(B_POS).getSurfaceConfig();
				int h = random.nextInt(3) + 2;
				for (int y = box.maxY; y >= box.minY; y--)
				{
					B_POS.setY(y);
					float d = SDF.sdfEllipsoid(B_POS, center, radius2, radius, radius2);
					if (d < 0)
					{
						d += MHelper.noise(B_POS, noiseScale) * noisePower;
						if (d < 0)
						{
							if (isAir(world, B_POS.up()))
							{
								world.setBlockState(B_POS, config.getTopMaterial(), 0);
								HEIGHTMAP.addHeight(x - box.minX, y, z - box.minZ);
							}
							else if (isAir(world, B_POS.up(h)))
								world.setBlockState(B_POS, config.getUnderMaterial(), 0);
							else
								world.setBlockState(B_POS, STONE, 0);
						}
					}
				}
			}
		}
		generateOres(box, world, random, radius);
		generateBushes(box, world, random, radius);
	}
	
	@Override
	public int getMaxSize()
	{
		return 60;
	}
	
	@Override
	public int getMinSize()
	{
		return 20;
	}
	
	@Override
	public float groupDistanceMultiplier()
	{
		return 0.4F;
	}
	
	@Override
	public float getSpiralPower()
	{
		return 2;
	}
}
