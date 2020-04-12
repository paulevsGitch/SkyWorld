package paulevs.skyworld.structures.features;

import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.structures.piece.ConeIslandPiece;

public class ConeIslandFeature extends IslandFeature
{
	public ConeIslandFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory)
	{
		super(configFactory);
		this.setDistance(4);
		this.setSeparation(1);
		this.setSalt(getName().hashCode() & 65535);
	}
	
	@Override
	public StructureStartFactory getStructureStartFactory()
	{
		return ConeIslandStart::new;
	}

	@Override
	public String getName()
	{
		return "Cone Island";
	}

	@Override
	public int getRadius()
	{
		return 3;
	}
	
	public static class ConeIslandStart extends StructureStart
	{
		public ConeIslandStart(StructureFeature<?> feature, int chunkX, int chunkZ, BlockBox box, int references, long l)
		{
			super(feature, chunkX, chunkZ, box, references, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome)
		{
			int px = (x << 4);
			int pz = (z << 4);
			this.children.add(new ConeIslandPiece(new BlockPos(px + random.nextInt(16), 64 + random.nextInt(64), pz + random.nextInt(16)), random.nextInt(30) + 5, random));
			this.setBoundingBoxFromChildren();
		}
	}
}
