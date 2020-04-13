package paulevs.skyworld.structures.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Generators
{
	private static final List<Class<? extends IslandGenerator>> GENERATOR_LIST;
	private static final Map<String, Class<? extends IslandGenerator>> GENERATORS;
	
	public static IslandGenerator getGenerator(String name)
	{
		Class<? extends IslandGenerator> gen = GENERATORS.get(name);
		try
		{
			return gen != null ? gen.getConstructor().newInstance() : null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	static
	{
		GENERATOR_LIST = new ArrayList<Class<? extends IslandGenerator>>();
		GENERATORS = new HashMap<String, Class<? extends IslandGenerator>>();
		register("flat_sphere", IslandFlatSphereGenerator.class);
		register("cone", IslandConeGenerator.class);
	}
	
	private static void register(String name, Class<? extends IslandGenerator> generator)
	{
		GENERATORS.put(name, generator);
		GENERATOR_LIST.add(generator);
	}

	public static IslandGenerator getGenerator(Random random)
	{
		Class<? extends IslandGenerator> gen =  GENERATOR_LIST.get(random.nextInt(GENERATOR_LIST.size()));
		try
		{
			return gen != null ? gen.getConstructor().newInstance() : null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
