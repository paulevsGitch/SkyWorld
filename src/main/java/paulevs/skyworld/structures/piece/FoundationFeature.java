package paulevs.skyworld.structures.piece;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import paulevs.skyworld.structures.StructureTypes;

public class FoundationFeature extends CustomPiece
{
	public FoundationFeature(BlockBox box, Random random)
	{
		super(StructureTypes.FOUNDATION, random.nextInt());
		this.boundingBox = new BlockBox(box.minX, box.minY - 6, box.minZ, box.maxX, box.minY - 2, box.maxZ);
	}
	
	public FoundationFeature(StructureManager manager, CompoundTag tag)
	{
		super(StructureTypes.FOUNDATION, tag);
	}

	@Override
	protected void fromNbt(CompoundTag tag) {}

	@Override
	protected void makeBoundingBox() {}

	@Override
	protected void toNbt(CompoundTag tag) {}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos)
	{
		int x1 = Math.max(boundingBox.minX, box.minX);
		int x2 = Math.min(boundingBox.maxX, box.maxX);
		int z1 = Math.max(boundingBox.minZ, box.minZ);
		int z2 = Math.min(boundingBox.maxZ, box.maxZ);
		BlockState filler = world.getBiome(pos.getCenterBlockPos()).getSurfaceConfig().getUnderMaterial();
		for (int x = x1; x <= x2; x++)
		{
			B_POS.setX(x);
			for (int z = z1; z <= z2; z++)
			{
				B_POS.setZ(z);
				for (int y = boundingBox.maxY; y >= boundingBox.minY; y--)
				{
					B_POS.setY(y);
					if (world.getBlockState(B_POS).getBlock() == Blocks.STONE)
						break;
					if (!world.getBlockState(B_POS).isFullCube(world, B_POS))
						world.setBlockState(B_POS, filler, 0);
				}
			}
		}
		return true;
	}
}
