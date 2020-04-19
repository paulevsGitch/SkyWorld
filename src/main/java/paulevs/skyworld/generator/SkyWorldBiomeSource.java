package paulevs.skyworld.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import paulevs.skyworld.structures.features.FoliagePair;;

public class SkyWorldBiomeSource extends BiomeSource
{
	private final BiomeLayerSampler biomeSampler;
	private static final Set<Biome> BIOMES;
	private static final Map<Biome, FoliagePair[]> FOLIAGE;
	
	public SkyWorldBiomeSource(VanillaLayeredBiomeSourceConfig config)
	{
		super(BIOMES);
		this.biomeSampler = SkyBiomeLayer.build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings());
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		return this.biomeSampler.sample(biomeX, biomeZ);
	}
	
	static
	{
		FOLIAGE = new HashMap<Biome, FoliagePair[]>();
		Set<Biome> biomes = new HashSet<Biome>();
		for (Biome biome: Registry.BIOME)
			if (!biome.hasParent() && isValidCategory(biome.getCategory()) && biome.getDepth() > -0.3)
			{
				biomes.add(biome);
				Set<FoliagePair> biomeFlora = new HashSet<FoliagePair>();
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
									biomeFlora.add(new FoliagePair(config.trunkProvider, config.leavesProvider));
								}
							}
						}
					}
				}
				FOLIAGE.put(biome, biomeFlora.toArray(new FoliagePair[] {}));
			}
		BIOMES = ImmutableSet.copyOf(biomes);
	}
	
	private static boolean isValidCategory(Category category)
	{
		return 	category != Category.NONE &&
				category != Category.BEACH &&
				category != Category.OCEAN &&
				category != Category.NETHER &&
				category != Category.THEEND;
	}
	
	public static FoliagePair[] getFoliage(Biome biome)
	{
		return FOLIAGE.get(biome);
	}
}