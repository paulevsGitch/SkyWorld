package paulevs.skyworld.structures.features;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.SkyChunkGenerator;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.math.Vector2F;
import paulevs.skyworld.structures.generators.Generators;
import paulevs.skyworld.structures.piece.IslandPiece;

public class IslandFeature extends StructureFeature<DefaultFeatureConfig>
{
	private int salt;
	private int distance;
	private int separation;
	
	public IslandFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory)
	{
		super(configFactory);
		salt = getName().hashCode();
		distance = 6;
		separation = 3;
	}

	@Override
	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ, int scaleX, int scaleZ)
	{
		int o = chunkX + distance * scaleX;
		int p = chunkZ + distance * scaleZ;
		int q = o < 0 ? o - distance + 1 : o;
		int r = p < 0 ? p - distance + 1 : p;
		int x = q / distance;
		int z = r / distance;
		((ChunkRandom) random).setStructureSeed(chunkGenerator.getSeed(), x, z, salt);
		x *= distance;
		z *= distance;
		x += random.nextInt(distance - separation);
		z += random.nextInt(distance - separation);
		return new ChunkPos(x, z);
	}
	
	@Override
	public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ, Biome biome)
	{
		if (chunkGenerator instanceof SkyChunkGenerator)
		{
			ChunkPos pos = getStart(chunkGenerator, random, chunkX, chunkZ, 0, 0);
			return chunkX == pos.x && chunkZ == pos.z;
		}
		return false;
	}
	
	protected void setDistance(int distance)
	{
		this.distance = distance;
	}
	
	protected void setSeparation(int separation)
	{
		this.separation = separation;
	}
	
	protected void setSalt(int salt)
	{
		this.salt = salt;
	}

	@Override
	public StructureStartFactory getStructureStartFactory()
	{
		return IslandStart::new;
	}

	@Override
	public String getName()
	{
		return "sky_island";
	}

	@Override
	public int getRadius()
	{
		return 4;
	}
	
	public static class IslandStart extends StructureStart
	{
		public IslandStart(StructureFeature<?> feature, int chunkX, int chunkZ, BlockBox box, int references, long l)
		{
			super(feature, chunkX, chunkZ, box, references, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome)
		{
			int px = (x << 4);
			int pz = (z << 4);
			BlockPos center = new BlockPos(px + random.nextInt(16), 64 + random.nextInt(64), pz + random.nextInt(16));
			int radius = MHelper.getSquaredRange(10, 50, random);
			String generator = Generators.getGenerator(random);
			this.children.add(new IslandPiece(center, radius, random, generator));
			if (radius > 30 && random.nextBoolean()) // archipelago
			{
				/*int count = MHelper.randRange(2, radius / 5, random);
				float angle = random.nextFloat() * 2 * (float) Math.PI;
				float offset = 0.75F;
				for (int i = 0; i < count; i++)
				{
					float mult = radius * 1.4F + i * radius * 0.3F;
					angle += offset;
					offset *= 0.9F;
					int dx = (int) (Math.cos(angle) * mult);
					int dz = (int) (Math.sin(angle) * mult);
					int rad = (int) ((count - i) * radius * 0.75 / count);
					int yDelta = (int) (radius * 0.1);
					BlockPos childPos = center.add(dx, MHelper.randRange(-yDelta, yDelta, random) - i * yDelta, dz);
					this.children.add(new IslandPiece(childPos, MHelper.getSquaredRange(rad / 2, rad, random), random, generator));
				}*/
				int spirals = MHelper.randRange(1, 3, random);
				float startAngle = random.nextFloat() * MHelper.PI2;
				float dx = MHelper.randRange(0.2F, 0.8F, random);
				float dr = MHelper.randRange(0.4F, 0.8F, random) * radius;
				float offsetR = MHelper.randRange(radius * 1.3F, radius * 1.5F, random);
				Vector2F offset = new Vector2F();
				int yDelta = (int) (radius * 0.1);
				float xDist = 0.0256F * dx + 0.7949F;
				for (int s = 0; s < spirals; s++)
				{
					float offsetX = s * MHelper.PI2 /  spirals + startAngle;
					int count = MHelper.randRange(2, radius / 5, random);
					for (int i = 0; i < count; i++)
					{
						MHelper.getSpiral(i * xDist, dx, dr, offsetX, offsetR, offset);
						BlockPos childPos = center.add(offset.getX(), (MHelper.randRange(-yDelta, yDelta, random) - i * yDelta), offset.getY());
						int rad = (int) ((count - i) * radius * 0.5 / count);
						rad = MHelper.getSquaredRange(rad / 2, rad, random);
						this.children.add(new IslandPiece(childPos, rad, random, generator));
					}
				}
			}
			
			this.setBoundingBoxFromChildren();
		}
	}
}
