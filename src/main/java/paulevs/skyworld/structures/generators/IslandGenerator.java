package paulevs.skyworld.structures.generators;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class IslandGenerator
{
	protected static final BlockState STONE = Blocks.STONE.getDefaultState();
	protected static final Mutable B_POS = new Mutable();
	
	public abstract void initValues(BlockPos center, int radius);
	
	public abstract void setBoundingBox(BlockBox box, BlockPos center, int radius);
	
	public abstract void generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos, BlockPos center, int radius);
	
	protected boolean isAir(IWorld world, BlockPos pos)
	{
		return !world.getBlockState(pos).isFullCube(world, pos);
	}
	
	public abstract String getName();
}
