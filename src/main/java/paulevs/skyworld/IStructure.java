package paulevs.skyworld;

import net.minecraft.structure.StructureManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public interface IStructure
{
	public void addIsland(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome);
}
