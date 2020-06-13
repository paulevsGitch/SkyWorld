package paulevs.skyworld.structures.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Generators
{
	private static final List<String> GENERATOR_LIST;
	private static final Map<String, Class<? extends IslandGenerator>> GENERATORS;
	public static final IslandGenerator FEATURE_ISLAND = new IslandFeatureGenerator().setName("feature");
	
	public static IslandGenerator getGenerator(String name)
	{
		if (name.equals("feature"))
			return FEATURE_ISLAND;
		
		Class<? extends IslandGenerator> gen = GENERATORS.get(name);
		try
		{
			return gen != null ? gen.getConstructor().newInstance().setName(name) : null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	static
	{
		GENERATOR_LIST = new ArrayList<String>();
		GENERATORS = new HashMap<String, Class<? extends IslandGenerator>>();
		register("flat_sphere", IslandFlatSphereGenerator.class);
		register("cone", IslandConeGenerator.class);
		register("sphere", IslandSphereGenerator.class);
		register("double_cone", IslandDoubleConeGenerator.class);
		register("tall_sphere", IslandTallSphereGenerator.class);
	}
	
	private static void register(String name, Class<? extends IslandGenerator> generator)
	{
		GENERATORS.put(name, generator);
		GENERATOR_LIST.add(name);
	}

	public static IslandGenerator getGenerator(Random random)
	{
		return getGenerator(GENERATOR_LIST.get(random.nextInt(GENERATOR_LIST.size())));
	}
	public static IslandGenerator cloneGenerator(IslandGenerator generator)
	{
		return getGenerator(generator.getName());
	}
	
	public static Set<String> getGenerators()
	{
		return GENERATORS.keySet();
	}
}
