package paulevs.skyworld;

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
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class SkyChunkGenerator extends ChunkGenerator<FloatingIslandsChunkGeneratorConfig>
{
	public static final ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, SkyChunkGenerator> FLOATING_CHUNK_GEN;
	
	public SkyChunkGenerator(IWorld world, BiomeSource biomeSource, FloatingIslandsChunkGeneratorConfig config)
	{
		super(world, biomeSource, config);
	}
	
	public static void register() {}
	
	static
	{
		FLOATING_CHUNK_GEN = new ChunkGeneratorType<FloatingIslandsChunkGeneratorConfig, SkyChunkGenerator>(null, false, FloatingIslandsChunkGeneratorConfig::new)
		{
			@Override
			public SkyChunkGenerator create(World world, BiomeSource biomeSource, FloatingIslandsChunkGeneratorConfig config)
			{
			      return new SkyChunkGenerator(world, biomeSource, config);
			}
		};
		Registry.register(Registry.CHUNK_GENERATOR_TYPE, "sky_world", FLOATING_CHUNK_GEN);
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {}

	@Override
	public int getSpawnHeight()
	{
		return 64;
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
		return StructureFeatures.hasFeature(structureFeature);// || super.hasStructure(biome, structureFeature);
	}
}
