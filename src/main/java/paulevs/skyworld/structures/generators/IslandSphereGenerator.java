package paulevs.skyworld.structures.generators;

import java.util.Random;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import paulevs.skyworld.generator.SkyWorldChunkGeneratorConfig;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.math.SDF;

public class IslandSphereGenerator extends IslandGenerator
{
	private int octaves;
	private float noisePower;
	private float noiseScale;
	
	@Override
	public void initValues(BlockPos center, int radius)
	{
		this.octaves = (int) Math.round(Math.log(radius * 0.25));
		this.noisePower = (float) Math.log(radius) * 13F;
		this.noiseScale = 0.2F / (float) Math.log(radius);
	}

	@Override
	public void setBoundingBox(BlockBox box, BlockPos center, int radius)
	{
		box.minX = center.getX() - radius;
		box.minY = center.getY() - radius;
		box.minZ = center.getZ() - radius;
		box.maxX = center.getX() + radius;
		box.maxY = center.getY() + radius;
		box.maxZ = center.getZ() + radius;
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
					float d = SDF.sphereSDF(B_POS, center, radius);
					if (d < 0)
					{
						d += MHelper.noise(B_POS, noiseScale, octaves) * noisePower;
						//float rn = MHelper.rigidNoise(B_POS, noiseScale, octaves);
						//d += rn * 10;
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
		generateFoliage(box, world, random, radius, (SkyWorldChunkGeneratorConfig) generator.getConfig());
	}
}
