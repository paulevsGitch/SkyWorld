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
		distance = 4;
		separation = 1;
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
			this.children.add(new IslandPiece(new BlockPos(px + random.nextInt(16), 64 + random.nextInt(64), pz + random.nextInt(16)), MHelper.getSquaredRange(10, 50, random), random));
			this.setBoundingBoxFromChildren();
		}
	}
}
