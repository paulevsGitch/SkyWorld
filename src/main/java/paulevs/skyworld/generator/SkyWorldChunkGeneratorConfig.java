package paulevs.skyworld.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.datafixers.Dynamic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import paulevs.skyworld.structures.generators.Generators;

public class SkyWorldChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
	protected int islandDistance = 6;
	protected int biomeSize = 3;
	protected boolean hasLeafVines = true;
	protected boolean hasNormalVines = true;
	protected boolean hasBushes = true;
	private boolean hasOcean = false;
	private Map<String, Boolean> types;
	private List<String> availableTypes;
	
	@Override
	public int getBiomeSize()
	{
		return biomeSize;
	}

	@Override
	public int getRiverSize()
	{
		return -1;
	}
	
	public int getIslandDistance()
	{
		return islandDistance;
	}
	
	public boolean hasLeafVines()
	{
		return hasLeafVines;
	}
	
	public boolean hasNormalVines()
	{
		return hasNormalVines;
	}
	
	public boolean hasBushes()
	{
		return hasBushes;
	}
	
	public boolean hasOcean()
	{
		return hasOcean;
	}

	public static SkyWorldChunkGeneratorConfig fromDynamic(Dynamic<?> dynamic)
	{
		SkyWorldChunkGeneratorConfig config = new SkyWorldChunkGeneratorConfig();
		
		config.islandDistance = dynamic.get("distance").asInt(6);
		config.biomeSize = dynamic.get("biomesize").asInt(6);
		config.hasLeafVines = dynamic.get("hasleafvines").asBoolean(true);
		config.hasNormalVines = dynamic.get("hasnormalvines").asBoolean(true);
		config.hasBushes = dynamic.get("hasbushes").asBoolean(true);
		config.hasOcean = dynamic.get("hasocean").asBoolean(false);
		
		config.types = new HashMap<String, Boolean>();
		config.availableTypes = new ArrayList<String>();
		for (String type: Generators.getGenerators())
		{
			String key = "island_" + type;
			boolean value = dynamic.get(key).asBoolean(true);
			config.types.put(type, dynamic.get(key).asBoolean(true));
			if (value)
				config.availableTypes.add(type);
		}
		
		return config;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Environment(EnvType.CLIENT)
	public Dynamic<?> toDynamic(NbtOps dynamicOps)
	{
		Map<Tag, Tag> map = new HashMap<Tag, Tag>();
		map.put(dynamicOps.createString("distance"), dynamicOps.createInt(6));
		map.put(dynamicOps.createString("biomesize"), dynamicOps.createInt(3));
		map.put(dynamicOps.createString("hasleafvines"), dynamicOps.createBoolean(true));
		map.put(dynamicOps.createString("hasnormalvines"), dynamicOps.createBoolean(true));
		map.put(dynamicOps.createString("hasbushes"), dynamicOps.createBoolean(true));
		map.put(dynamicOps.createString("hasocean"), dynamicOps.createBoolean(false));
		
		for (String type: Generators.getGenerators())
		{
			String key = "island_" + type;
			map.put(dynamicOps.createString(key), dynamicOps.createBoolean(true));
		}
		
		return new Dynamic(dynamicOps, dynamicOps.createMap(map));
	}
	
	public List<String> getAvailableTypes()
	{
		return availableTypes;
	}
}
