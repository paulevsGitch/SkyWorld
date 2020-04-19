package paulevs.skyworld.structures.piece;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.structures.StructureTypes;
import paulevs.skyworld.structures.generators.Generators;
import paulevs.skyworld.structures.generators.IslandGenerator;

public class IslandPiece extends CustomPiece
{
	private static final Set<Heightmap.Type> HMAP = Collections.singleton(Heightmap.Type.WORLD_SURFACE_WG);
	private IslandGenerator generator;
	private BlockPos center;
	private int radius;
	
	public IslandPiece(BlockPos center, int radius, Random random, IslandGenerator generator)
	{
		super(StructureTypes.SKY_ISLAND, random.nextInt());
		this.center = new BlockPos(center);
		this.radius = radius;
		this.generator = generator;
		this.generator.initValues(this.center, radius);
		makeBoundingBox();
	}
	
	public IslandPiece(StructureManager manager, CompoundTag tag)
	{
		super(StructureTypes.SKY_ISLAND, tag);
	}
	
	@Override
	protected void toNbt(CompoundTag tag)
	{
		tag.putInt("radius", radius);
		tag.put("center", NbtHelper.fromBlockPos(center));
		tag.putString("generator", this.generator.getName());
	}
	
	@Override
	protected void fromNbt(CompoundTag tag)
	{
		this.radius = tag.getInt("radius");
		this.center = NbtHelper.toBlockPos(tag.getCompound("center"));
		this.generator = Generators.getGenerator(tag.getString("generator"));
		generator.initValues(center, radius);
		makeBoundingBox();
	}

	@Override
	protected void makeBoundingBox()
	{
		this.boundingBox = new BlockBox();
		generator.setBoundingBox(boundingBox, center, radius);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos)
	{
		this.generator.generate(world, generator, random, MHelper.intersection(boundingBox, box), pos, center, radius);
		Heightmap.populateHeightmaps(world.getChunk(pos.x, pos.z), HMAP);
		return true;
	}
}
