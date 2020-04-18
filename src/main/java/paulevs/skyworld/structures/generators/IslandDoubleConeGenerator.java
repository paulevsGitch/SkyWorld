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

public class IslandDoubleConeGenerator extends IslandGenerator
{
	private float noisePower;
	private float noiseScale;
	private BlockPos cone1Pos;
	private BlockPos cone2Pos;
	private BlockPos cone4Pos;
	private int h1;
	private int h2;
	private int h3;
	private float r2;
	private float r3;
	private float r4;
	private float blend;
	
	@Override
	public void initValues(BlockPos center, int radius)
	{
		this.h1 = (int) Math.ceil(radius * 0.125F);
		this.h2 = radius - h1;
		this.h3 = (int) Math.ceil(radius * 0.4F);
		this.cone1Pos = center.down(h1);
		this.cone2Pos = center.down(h1 + h2);
		this.cone4Pos = center.up(h3);
		this.r2 = radius * 0.5F;
		this.r3 = radius * 0.1F;
		this.r4 = radius * 0.25F;
		this.blend = radius * 0.75F;
		this.noisePower = radius * 0.15F;
		this.noiseScale = 0.5F / (float) Math.log(radius);
	}

	@Override
	public void setBoundingBox(BlockBox box, BlockPos center, int radius)
	{
		box.minX = center.getX() - radius;
		box.minY = center.getY() - radius * 3;
		if (box.minY < 0)
			box.minY = 0;
		box.minZ = center.getZ() - radius;
		box.maxX = center.getX() + radius;
		box.maxY = center.getY() + radius;
		box.maxZ = center.getZ() + radius;
	}

	@Override
	public void generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos, BlockPos center, int radius)
	{
		if ((box.minX >> 4) == 34 && (box.minZ >> 4) == -23)
		{
			System.out.println(radius);
			System.out.println(center);
			System.out.println(cone1Pos + " " + h1 + " " + r2);
			System.out.println(cone2Pos + " " + h2 + " " + r3);
		}
		
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
					d = SDF.smoothUnion(d, SDF.coneSDF(B_POS, center, h3, r2, r4), blend);
					d = SDF.smoothUnion(d, SDF.coneSDF(B_POS, cone4Pos, h3, r4, r3), blend);
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
		generateBushes(box, world, random);
	}
}
