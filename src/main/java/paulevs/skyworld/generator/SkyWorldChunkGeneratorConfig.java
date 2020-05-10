package paulevs.skyworld.generator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class SkyWorldChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
	protected int islandDistance = 6;
	
	@Override
	public int getBiomeSize()
	{
		return 3;
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

	public static SkyWorldChunkGeneratorConfig fromDynamic(Dynamic<?> dynamic)
	{
		SkyWorldChunkGeneratorConfig config = new SkyWorldChunkGeneratorConfig();
		config.islandDistance = dynamic.get("distance").asInt(6);
		return config;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Environment(EnvType.CLIENT)
	public Dynamic<?> toDynamic(NbtOps dynamicOps)
	{
		return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("distance"), dynamicOps.createInt(6))));
	}

	public void setIslandDistance(int dist)
	{
		islandDistance = dist;
	}
}
