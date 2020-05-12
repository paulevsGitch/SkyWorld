package paulevs.skyworld.structures.generators;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.Target;
import paulevs.skyworld.generator.SkyWorldBiomeSource;
import paulevs.skyworld.generator.SkyWorldChunkGeneratorConfig;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.structures.features.FoliagePair;

public abstract class IslandGenerator
{
	private static final Vec3i[] OFFSETS = new Vec3i[] {
			new Vec3i(-1, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(-1, 0, 1),
			new Vec3i( 0, 0, -1), new Vec3i( 0, 0, 1),
			new Vec3i( 1, 0, -1), new Vec3i( 1, 0, 0), new Vec3i( 1, 0, 1)
	};

	protected static final BlockState STONE = Blocks.STONE.getDefaultState();
	protected static final Mutable B_POS = new Mutable();
	protected static final VolumetricHeightmap HEIGHTMAP = new VolumetricHeightmap();
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
	
	protected void generateFoliage(BlockBox box, IWorld world, Random random, int radius, SkyWorldChunkGeneratorConfig config)
	{
		boolean leafVines = config.hasLeafVines();
		boolean normalVines = config.hasNormalVines();
		boolean bushes = config.hasBushes();
		
		if (!leafVines && !normalVines && !bushes)
			return;
		
		B_POS.set(box.minX + 8, 0, box.minZ + 8);
		Biome biome = world.getBiome(B_POS);
		FoliagePair[] pairs = SkyWorldBiomeSource.getFoliage(biome);
		if (pairs != null && pairs.length > 0)
		{
			int count = radius > 20 ? 4 : (int) Math.ceil(radius / 5F);
			int countBush = MHelper.randRange(count / 2, count, random);
			int countVine = MHelper.randRange(count / 2, count, random);
			int sectionStart = VolumetricHeightmap.getSection(box.minY);
			int sectionEnd = VolumetricHeightmap.getSection(box.maxY);
			for (int section = sectionStart; section <= sectionEnd; section++)
			{
				if (bushes && biome.getRainfall() > 0.5F)
				{
					for (int i = 0; i < countBush; i++)
					{
						B_POS.setX(MHelper.randRange(box.minX, box.maxX, random));
						B_POS.setZ(MHelper.randRange(box.minZ, box.maxZ, random));
						int hx = B_POS.getX() - box.minX;
						int hz = B_POS.getZ() - box.minZ;
						int h = HEIGHTMAP.getRandomHeight(hx, hz, section, random);
						if (h > 0)
						{
							B_POS.setY(h + 1);
							FoliagePair pair = pairs[random.nextInt(pairs.length)];
							makeBush(world, B_POS.toImmutable(), pair, random);
						}
					}
				}
				
				if (leafVines && hasVines(biome))
				{
					for (int i = 0; i < countVine; i++)
					{
						B_POS.setX(MHelper.randRange(box.minX, box.maxX, random));
						B_POS.setZ(MHelper.randRange(box.minZ, box.maxZ, random));
						int hx = B_POS.getX() - box.minX;
						int hz = B_POS.getZ() - box.minZ;
						int h = HEIGHTMAP.getRandomHeight(hx, hz, section, random);
						if (h > 0)
						{
							B_POS.setY(h + 1);
							FoliagePair pair = pairs[random.nextInt(pairs.length)];
							generateVine(world, pair, box, B_POS, random, radius);
						}
					}
				}
			}
			
			if (normalVines && hasVines(biome))
			{
				for (int i = 0; i < countVine * 2; i++)
				{
					B_POS.setX(MHelper.randRange(box.minX, box.maxX, random));
					B_POS.setZ(MHelper.randRange(box.minZ, box.maxZ, random));
					B_POS.setY(box.minY);
					while (world.isAir(B_POS) && B_POS.getY() < box.maxY)
					{
						B_POS.setY(B_POS.getY() + 1);
					}
					if (B_POS.getY() >= box.maxY - 1)
						continue;
					if (!world.isAir(B_POS))
						continue;
					else
					{
						B_POS.setY(B_POS.getY() - 1);
					}
					FoliagePair pair = pairs[random.nextInt(pairs.length)];
					generateVine(world, pair, box, B_POS, random, radius / 2);
				}
				
				for (int i = 0; i < countVine * 3; i++)
				{
					B_POS.setX(MHelper.randRange(box.minX, box.maxX, random));
					B_POS.setZ(MHelper.randRange(box.minZ, box.maxZ, random));
					B_POS.setY(box.minY);
					boolean sides = false;
					while (world.isAir(B_POS) && B_POS.getY() < box.maxY && !sides)
					{
						B_POS.setY(B_POS.getY() + 1);
						sides = hasSideBlocks(world, B_POS);
					}
					if (B_POS.getY() >= box.maxY - 1 || !world.isAir(B_POS) || !sides)
						continue;
					generateVanillaVine(world, B_POS, random, radius * 3 / 2);
				}
			}
		}
	}
	
	private boolean hasVines(Biome biome)
	{
		float t = biome.getTemperature();
		return t >= 0.5F && t <= 1.5F && biome.getRainfall() > 0.5F;
	}
	
	protected void generateVine(IWorld world, FoliagePair pair, BlockBox box, BlockPos pos, Random random, int maxLength)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		B_POS.set(pos);
		int length = MHelper.randRange(maxLength > 5 ? 5 : 0, maxLength, random);
		int length2 = length / 2;
		boolean offseted = false;
		for (int i = 0; i < length; i++)
		{
			if (i < length2)
			{
				Vec3i offset = OFFSETS[random.nextInt(OFFSETS.length)];
				B_POS.setX(x + offset.getX());
				B_POS.setZ(z + offset.getZ());
				if (world.isAir(B_POS))
					pair.setLeavesNoDecay(world, B_POS, random);
				B_POS.setX(x);
				B_POS.setZ(z);
			}
			B_POS.setY(y--);
			if (world.isAir(B_POS))
			{
				if (!offseted && random.nextBoolean())
				{
					Vec3i offset = OFFSETS[random.nextInt(OFFSETS.length)];
					B_POS.setX(x + offset.getX());
					B_POS.setZ(z + offset.getZ());
					if (!world.isAir(B_POS))
					{
						B_POS.setX(x);
						B_POS.setZ(z);
					}
					pair.setLeavesNoDecay(world, B_POS, random);
					B_POS.setX(x);
					B_POS.setZ(z);
					offseted = true;
				}
				else
				{
					offseted = false;
					pair.setLeavesNoDecay(world, B_POS, random);
				}
				continue;
			}
			for (Vec3i dir: OFFSETS)
			{
				B_POS.setX(x + dir.getX());
				B_POS.setZ(z + dir.getZ());
				if (!box.contains(B_POS))
					continue;
				if (world.isAir(B_POS))
				{
					pair.setLeavesNoDecay(world, B_POS, random);
					x = pos.getX();
					z = pos.getZ();
					break;
				}
			}
		}
	}
	
	protected void generateVanillaVine(IWorld world, BlockPos pos, Random random, int maxLength)
	{
		boolean north = !isAir(world, pos.north());
		boolean south = !isAir(world, pos.south());
		boolean east = !isAir(world, pos.east());
		boolean west = !isAir(world, pos.west());
		//if (north || south || east || west)
		{
			BlockState vine = Blocks.VINE.getDefaultState()
					.with(VineBlock.NORTH, north)
					.with(VineBlock.SOUTH, south)
					.with(VineBlock.EAST, east)
					.with(VineBlock.WEST, west);
			int length = MHelper.randRange(maxLength > 5 ? 5 : 0, maxLength, random);
			B_POS.set(pos);
			int y = pos.getY();
			for (int i = 0; i < length; i++)
			{
				B_POS.setY(y - i);
				if (world.isAir(B_POS))
					world.setBlockState(B_POS, vine, 0);
			}
		}
	}
	
	protected boolean hasSideBlocks(IWorld world, BlockPos pos)
	{
		return !isAir(world, pos.north()) || !isAir(world, pos.south()) || !isAir(world, pos.east()) || !isAir(world, pos.west());
	}
	
	protected void generateOres(BlockBox box, IWorld world, Random random, int radius)
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
					if (radius < 20 && maxY < 30)
						continue;
					maxY = maxY > 30 ? box.maxY : middle;
					count = MHelper.randRange(1, (int) Math.ceil(count * countCoef), random);
					if (radius < 20 && count > 3)
						count = 3;
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
	
	protected void makeBush(IWorld world, BlockPos pos, FoliagePair foliage, Random random)
	{
		int r = MHelper.randRange(1, 3, random);
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
			int minR = r2 / 3;
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
					if ((sum < 1.5 || sum <= MHelper.randRange(minR, r2, random)) && world.isAir(B_POS))
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
}
