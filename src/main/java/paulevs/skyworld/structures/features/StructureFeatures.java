package paulevs.skyworld.structures.features;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.SkyWorld;

public class StructureFeatures
{
	private static final List<StructureFeature<?>> FEATURES = new ArrayList<StructureFeature<?>>();
	public static final StructureFeature<DefaultFeatureConfig> SKY_ISLAND = register("sky_island", new IslandFeature(DefaultFeatureConfig::deserialize));
	
	public static void register() {}
	
	private static StructureFeature<DefaultFeatureConfig> register(String id, StructureFeature<DefaultFeatureConfig> feature)
	{
		FEATURES.add(feature);
		Feature.STRUCTURES.put(id, feature);
		Registry.BIOME.forEach((biome) -> {
			biome.getFeaturesForStep(GenerationStep.Feature.RAW_GENERATION).add(0, feature.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT)));
			biome.addStructureFeature(feature.configure(FeatureConfig.DEFAULT));
		});
		return Registry.register(Registry.STRUCTURE_FEATURE, SkyWorld.getID(id), feature);
	}
	
	public static boolean hasFeature(StructureFeature<?> feature)
	{
		return FEATURES.contains(feature);
	}
}
