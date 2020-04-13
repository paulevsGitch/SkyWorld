package paulevs.skyworld.structures.generators;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
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
import paulevs.skyworld.math.MHelper;

public abstract class IslandGenerator
{
	protected static final BlockState STONE = Blocks.STONE.getDefaultState();
	protected static final Mutable B_POS = new Mutable();
	
	public abstract void initValues(BlockPos center, int radius);
	
	public abstract void setBoundingBox(BlockBox box, BlockPos center, int radius);
	
	public abstract void generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos, BlockPos center, int radius);
	
	protected boolean isAir(IWorld world, BlockPos pos)
	{
		return !world.getBlockState(pos).isFullCube(world, pos);
	}
	
	public abstract String getName();
	
	protected void generateOres(BlockBox box, IWorld world, Random random)
	{
		int oreRange = box.maxY - box.minY;
		int middle = box.minY + oreRange / 3;
		B_POS.set(box.minX + 8, 0, box.minZ + 8);
		List<ConfiguredFeature<?,?>> ores = world.getBiome(B_POS).getFeaturesForStep(Feature.UNDERGROUND_ORES);
		for (ConfiguredFeature<?,?> feature: ores)
		{
			DecoratedFeatureConfig conf = (DecoratedFeatureConfig) feature.config;
			ConfiguredDecorator<? extends DecoratorConfig> decorator = (ConfiguredDecorator<? extends DecoratorConfig>) conf.decorator;
			int minY = 0;
			int maxY = 0;
			int count = 0;
			if (decorator.config instanceof CountDepthDecoratorConfig)
			{
				CountDepthDecoratorConfig depthDecoratorConfig = (CountDepthDecoratorConfig) decorator.config;
				minY = depthDecoratorConfig.baseline - depthDecoratorConfig.spread;
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
			OreFeatureConfig oreConfig = (OreFeatureConfig) conf.feature.config;
			if (oreConfig.target == Target.NATURAL_STONE)
			{
				minY = minY * oreRange / 255 + box.minY;
				maxY = maxY * oreRange / 255 + box.minY;
				if (maxY < middle)
					maxY = middle;
				for (int n = 0; n < count; n++)
				{
					B_POS.set(MHelper.randRange(box.minX, box.maxX, random), MHelper.randRange(minY, maxY, random), MHelper.randRange(box.minZ, box.maxZ, random));
					int size = random.nextInt(oreConfig.size);
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
