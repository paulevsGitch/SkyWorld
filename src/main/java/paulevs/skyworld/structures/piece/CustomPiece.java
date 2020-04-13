package paulevs.skyworld.structures.piece;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IWorld;

public abstract class CustomPiece extends StructurePiece
{
	protected static final Mutable B_POS = new Mutable();
	
	protected CustomPiece(StructurePieceType type, int i)
	{
		super(type, i);
	}

	protected CustomPiece(StructurePieceType type, CompoundTag tag)
	{
		super(type, tag);
		fromNbt(tag);
		makeBoundingBox();
	}
	
	protected abstract void fromNbt(CompoundTag tag);
	
	protected abstract void makeBoundingBox();
	
	protected boolean isAir(IWorld world, BlockPos pos)
	{
		return !world.getBlockState(pos).isFullCube(world, pos);
	}
}