package paulevs.skyworld.generator;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
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
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import paulevs.skyworld.noise.OpenSimplexNoise;
import paulevs.skyworld.structures.features.StructureFeatures;

public class SkyChunkGenerator extends ChunkGenerator<SkyWorldChunkGeneratorConfig>
{
	public static final ChunkGeneratorType<SkyWorldChunkGeneratorConfig, SkyChunkGenerator> FLOATING_CHUNK_GEN;
	private static final Mutable POS = new Mutable();
	private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState WATER = Blocks.WATER.getDefaultState();
	private OpenSimplexNoise oceanFloorNoise;
	private OpenSimplexNoise villageBasisNoise;
	private boolean hasOcean = false;
	
	public SkyChunkGenerator(IWorld world, BiomeSource biomeSource, SkyWorldChunkGeneratorConfig config)
	{
		super(world, biomeSource, config);
		this.oceanFloorNoise = new OpenSimplexNoise(world.getSeed());
		this.villageBasisNoise = new OpenSimplexNoise(world.getSeed() + 1);
		this.hasOcean = config.hasOcean();
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
		if (hasOcean)
		{
			for (int x = 0; x < 16; x++)
			{
				POS.setX(x);
				double nx = ((chunk.getPos().x << 4) | x) * 0.02;
				for (int z = 0; z < 16; z++)
				{
					POS.setZ(z);
					double nz = ((chunk.getPos().z << 4) | z) * 0.02;
					double h = oceanFloorNoise.eval(nx, nz) * 0.75 + oceanFloorNoise.eval(nx * 3, nz * 3) * 0.1875 + oceanFloorNoise.eval(nx * 9, nz * 9) * 0.0625;
					h = h * 3.5 + 5.7;
					int hsurf = (int) (h - 1);
					for (int y = 0; y < h; y++)
					{
						POS.setY(y);
						if (y == hsurf)
						{
							SurfaceConfig config = chunkRegion.getBiome(POS).getSurfaceConfig();
							chunk.setBlockState(POS, config.getUnderMaterial(), false);
						}
						else if (y < 1 || (y < 2 && chunkRegion.getRandom().nextBoolean()))
							chunk.setBlockState(POS, BEDROCK, false);
						else
							chunk.setBlockState(POS, STONE, false);
					}
					for (int y = (int) h; y < 10; y++)
					{
						POS.setY(y);
						chunk.setBlockState(POS, WATER, false);
					}
				}
			}
		}
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
		return (int) (villageBasisNoise.eval(x * 0.04, z * 0.04) * 8 + 64);
	}
	
	@Override
	public int getSeaLevel()
	{
		return 0;
	}
	
	@Override
	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> structureFeature)
	{
		return structureFeature == StructureFeatures.SKY_ISLAND || super.hasStructure(biome, structureFeature);
	}

	public boolean hasOcean()
	{
		return hasOcean;
	}

	@Override
	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory category, BlockPos pos)
	{
		return this.getBiomeSource().getBiomeForNoiseGen(pos.getX(), pos.getY(), pos.getZ()).getEntitySpawnList(category);
	}
}
