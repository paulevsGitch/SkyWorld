package paulevs.skyworld.structures.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.Target;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.structures.features.FoliagePair;

@SuppressWarnings("unchecked")
public abstract class IslandGenerator
{
	protected static final BlockState STONE = Blocks.STONE.getDefaultState();
	protected static final Mutable B_POS = new Mutable();
	protected static final List<Integer>[][] HEIGHTMAP;
	protected String name;
	
	public abstract void initValues(BlockPos center, int radius);

	public abstract void setBoundingBox(BlockBox box, BlockPos center, int radius);

	public abstract void generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos, BlockPos center, int radius);

	protected boolean isAir(IWorld world, BlockPos pos)
	{
		return !world.getBlockState(pos).isFullCube(world, pos);
	}

	public String getName()
	{
		return name;
	}
	
	public IslandGenerator setName(String name)
	{
		this.name = name;
		return this;
	}
	
	protected void resetHeightMap()
	{
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				HEIGHTMAP[x][z].clear();
	}
	
	protected void setHeight(int x, int z, int height)
	{
		HEIGHTMAP[x][z].add(height);
	}
	
	protected void generateBushes(BlockBox box, IWorld world, Random random)
	{
		B_POS.set(box.minX + 8, 0, box.minZ + 8);
		Set<FoliagePair> pairs = getFoliage(world.getBiome(B_POS));
		if (!pairs.isEmpty())
		{
			FoliagePair[] pairArr = pairs.toArray(new FoliagePair[] {});
			for (int i = 0; i < 4; i++)
			{
				B_POS.setX(MHelper.randRange(box.minX, box.maxX, random));
				B_POS.setZ(MHelper.randRange(box.minZ, box.maxZ, random));
				int hx = B_POS.getX() - box.minX;
				int hz = B_POS.getZ() - box.minZ;
				if (!HEIGHTMAP[hx][hz].isEmpty())
				{
					B_POS.setY(HEIGHTMAP[hx][hz].get(random.nextInt(HEIGHTMAP[hx][hz].size())));
					FoliagePair pair = pairArr[random.nextInt(pairArr.length)];
					makeBush(world, B_POS.toImmutable(), pair, random);
				}
			}
		}
	}
	
	protected void generateOres(BlockBox box, IWorld world, Random random)
	{
		int oreRange = box.maxY - box.minY;
		int middle = box.minY + oreRange / 2;
		float countCoef = 128F / oreRange;
		B_POS.set(box.minX + 8, 0, box.minZ + 8);
		List<ConfiguredFeature<?,?>> ores = world.getBiome(B_POS).getFeaturesForStep(Feature.UNDERGROUND_ORES);
		for (ConfiguredFeature<?,?> feature: ores)
		{
			DecoratedFeatureConfig conf = (DecoratedFeatureConfig) feature.config;
			if (conf.feature.config instanceof OreFeatureConfig)
			{
				OreFeatureConfig oreConfig = (OreFeatureConfig) conf.feature.config;
				if (oreConfig.target == Target.NATURAL_STONE)
				{
					ConfiguredDecorator<? extends DecoratorConfig> decorator = (ConfiguredDecorator<? extends DecoratorConfig>) conf.decorator;
					int maxY = 0;
					int count = 0;
					if (decorator.config instanceof CountDepthDecoratorConfig)
					{
						CountDepthDecoratorConfig depthDecoratorConfig = (CountDepthDecoratorConfig) decorator.config;
						maxY = depthDecoratorConfig.baseline + depthDecoratorConfig.spread;
						count = depthDecoratorConfig.count;
					}
					else if (decorator.config instanceof RangeDecoratorConfig)
					{
						RangeDecoratorConfig rangeConfig = (RangeDecoratorConfig) decorator.config;
						maxY = rangeConfig.maximum;
						count = rangeConfig.count;
					}
					else
					{
						continue;
					}
					maxY = maxY > 30 ? box.maxY : middle;
					count = MHelper.randRange(1, (int) Math.ceil(count * countCoef), random);
					for (int n = 0; n < count; n++)
					{
						B_POS.set(MHelper.randRange(box.minX, box.maxX, random), MHelper.randRange(box.minY, maxY, random), MHelper.randRange(box.minZ, box.maxZ, random));
						int size = random.nextInt(oreConfig.size * 2);
						for (int i = 0; i < size; i++)
						{
							B_POS.setOffset(Direction.random(random));
							if (world.getBlockState(B_POS) == STONE)
								world.setBlockState(B_POS, oreConfig.state, 0);
						}
					}
				}
			}
		}
	}
	
	protected Set<FoliagePair> getFoliage(Biome biome)
	{
		Set<FoliagePair> pairs = new HashSet<FoliagePair>();
		List<ConfiguredFeature<?,?>> vegetation = biome.getFeaturesForStep(Feature.VEGETAL_DECORATION);
		for (ConfiguredFeature<?,?> feature: vegetation)
		{
			if (feature.feature instanceof DecoratedFeature)
			{
				DecoratedFeatureConfig dConfig = (DecoratedFeatureConfig) feature.config;
				if (dConfig.feature.config instanceof RandomFeatureConfig)
				{
					RandomFeatureConfig rfConfig = (RandomFeatureConfig) dConfig.feature.config;
					for (RandomFeatureEntry<?> rFeature: rfConfig.features)
					{
						if (rFeature.feature.config instanceof BranchedTreeFeatureConfig)
						{
							BranchedTreeFeatureConfig config = (BranchedTreeFeatureConfig) rFeature.feature.config;
							pairs.add(new FoliagePair(config.trunkProvider, config.leavesProvider));
						}
					}
				}
			}
		}
		return pairs;
	}
	
	protected void makeBush(IWorld world, BlockPos pos, FoliagePair foliage, Random random)
	{
		int r = MHelper.randRange(1, 3, random);
		//int r2 = r * r;
		//int minR = r2 * 2 / 3;
		int x1 = pos.getX() - r;
		int x2 = pos.getX() + r;
		int ry = (int) Math.ceil(r * 0.3F);
		int y1 = pos.getY() - ry;
		int y2 = pos.getY() + ry;
		int z1 = pos.getZ() - r;
		int z2 = pos.getZ() + r;
		for (int y = y1; y <= y2; y++)
		{
			B_POS.setY(y);
			int r2 = r - Math.abs(y - pos.getY());
			r2 *= r2;
			int minR = r2 / 2;
			for (int x = x1; x <= x2; x++)
			{
				B_POS.setX(x);
				int sqrX = x - pos.getX();
				sqrX *= sqrX;
				for (int z = z1; z <= z2; z++)
				{
					B_POS.setZ(z);
					int sqrZ = z - pos.getZ();
					sqrZ *= sqrZ;
					int sum = sqrX + sqrZ;
					if (sum < 1.5 || sum <= MHelper.randRange(minR, r2, random) && world.isAir(B_POS))
						foliage.setLeaves(world, B_POS, random);
				}
			}
		}
		foliage.setTrunk(world, pos, random);
	}
	
	public int getMaxSize()
	{
		return 50;
	}
	
	public int getMinSize()
	{
		return 10;
	}
	
	public float groupDistanceMultiplier()
	{
		return 1;
	}
	
	public float getSpiralPower()
	{
		return 1;
	}
	
	static
	{
		HEIGHTMAP = new ArrayList[16][16];
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				HEIGHTMAP[x][z] = new ArrayList<Integer>();
	}
}
