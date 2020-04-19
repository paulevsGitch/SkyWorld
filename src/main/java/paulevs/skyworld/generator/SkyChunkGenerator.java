package paulevs.skyworld.generator;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.structures.features.StructureFeatures;

public class SkyChunkGenerator extends ChunkGenerator<SkyWorldChunkGeneratorConfig>
{
	public static final ChunkGeneratorType<SkyWorldChunkGeneratorConfig, SkyChunkGenerator> FLOATING_CHUNK_GEN;
	
	public SkyChunkGenerator(IWorld world, BiomeSource biomeSource, SkyWorldChunkGeneratorConfig config)
	{
		super(world, biomeSource, config);
	}
	
	public static void register() {}
	
	static
	{
		FLOATING_CHUNK_GEN = new ChunkGeneratorType<SkyWorldChunkGeneratorConfig, SkyChunkGenerator>(null, false, SkyWorldChunkGeneratorConfig::new)
		{
			@Override
			public SkyChunkGenerator create(World world, BiomeSource biomeSource, SkyWorldChunkGeneratorConfig config)
			{
			      return new SkyChunkGenerator(world, biomeSource, config);
			}
		};
		Registry.register(Registry.CHUNK_GENERATOR_TYPE, "sky_world", FLOATING_CHUNK_GEN);
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
	{
		/*long[] heightmap = new long[256];
		for (int i = 0; i < 256; i++)
			heightmap[i] = 64;
		chunk.setHeightmap(Heightmap.Type.WORLD_SURFACE_WG, heightmap);*/
	}

	@Override
	public int getSpawnHeight()
	{
		return 0;
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk) {}

	@Override
	public int getHeightOnGround(int x, int z, Type heightmapType)
	{
		return 0;
	}
	
	@Override
	public int getSeaLevel()
	{
		return 0;
	}
	
	@Override
	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> structureFeature)
	{
		//System.out.println(structureFeature.getName());
		return structureFeature == StructureFeatures.SKY_ISLAND;// || structureFeature == Feature.VILLAGE;// StructureFeatures.hasFeature(structureFeature);// || super.hasStructure(biome, structureFeature);
	}
}
