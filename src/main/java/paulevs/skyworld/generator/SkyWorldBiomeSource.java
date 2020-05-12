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
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.structures.features.FoliagePair;
import paulevs.skyworld.structures.features.StructureFeatures;

public class SkyWorldBiomeSource extends BiomeSource
{
	private final BiomeLayerSampler biomeSampler;
	private final BiomeLayerSampler oceanSampler;
	private static final Set<Biome> BIOMES;
	private static final Map<Biome, FoliagePair[]> FOLIAGE;
	private final boolean hasOceans;
	
	public SkyWorldBiomeSource(VanillaLayeredBiomeSourceConfig config, boolean hasOceans)
	{
		super(BIOMES);
		this.hasOceans = hasOceans;
		this.biomeSampler = SkyBiomeLayer.build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings());
		this.oceanSampler = hasOceans ? SkyOceanLayer.build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings()) : null;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ)
	{
		return (hasOceans && biomeY < 4) ? this.oceanSampler.sample(biomeX, biomeZ) : this.biomeSampler.sample(biomeX, biomeZ);
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
		
		StructureFeature<DefaultFeatureConfig> feature = StructureFeatures.SKY_ISLAND;
		for (Biome biome: BIOMES)
		{
			if (!biome.hasStructureFeature(feature))
			{
				biome.addStructureFeature(feature.configure(FeatureConfig.DEFAULT));
				biome.getFeaturesForStep(GenerationStep.Feature.RAW_GENERATION).clear();
				biome.addFeature(GenerationStep.Feature.RAW_GENERATION, feature.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT)));
			}
		}
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
	
	public static boolean isSurfaceBIome(Biome biome)
	{
		return BIOMES.contains(biome);
	}
}