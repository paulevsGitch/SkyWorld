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

public class IslandConeGenerator extends IslandGenerator
{
	private float noisePower;
	private float noiseScale;
	private BlockPos cone1Pos;
	private BlockPos cone2Pos;
	private int h1;
	private int h2;
	private float r2;
	private float r3;
	private float blend;
	
	@Override
	public void initValues(BlockPos center, int radius)
	{
		this.h1 = (int) Math.ceil(radius * 0.125F);
		this.h2 = radius - h1;
		if (this.h2 > 64)
			this.h2 = 64;
		this.cone1Pos = center.down(h1);
		this.cone2Pos = center.down(h1 + h2);
		this.r2 = radius * 0.5F;
		this.r3 = radius * 0.1F;
		this.blend = radius * 0.5F;
		this.noisePower = radius * 0.15F;
		this.noiseScale = 0.5F / (float) Math.log(radius);
		if (radius > 64)
		{
			this.noiseScale *= 0.3F;
		}
	}

	@Override
	public void setBoundingBox(BlockBox box, BlockPos center, int radius)
	{
		box.minX = center.getX() - radius;
		box.minY = center.getY() - radius * 2;
		box.minZ = center.getZ() - radius;
		box.maxX = center.getX() + radius;
		box.maxY = center.getY() + radius / 8;
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
					float d = SDF.smoothUnion(SDF.coneSDF(B_POS, cone1Pos, h1, r2, radius), SDF.coneSDF(B_POS, cone2Pos, h2, r3, r2), blend);
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
		generateFoliage(box, world, random, radius, (SkyWorldChunkGeneratorConfig) generator.getConfig());
	}
}
