package paulevs.skyworld.generator;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class SkyWorldChunkGeneratorConfig extends OverworldChunkGeneratorConfig
{
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
}
