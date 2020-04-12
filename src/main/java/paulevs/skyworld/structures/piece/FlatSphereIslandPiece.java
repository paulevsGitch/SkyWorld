package paulevs.skyworld.structures.piece;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import paulevs.skyworld.noise.OpenSimplexNoise;
import paulevs.skyworld.structures.StructureTypes;

public class FlatSphereIslandPiece extends CustomPiece
{
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private OpenSimplexNoise noise = new OpenSimplexNoise(0);
	private Mutable bPos = new Mutable();
	private BlockPos center;
	private int radius;
	private int radius2;
	
	public FlatSphereIslandPiece(BlockPos center, int radius, Random random)
	{
		super(StructureTypes.FLAT_SPHERE_ISLAND, random.nextInt());
		this.center = center;
		this.radius = radius;
		this.radius2 = radius * radius;
		makeBoundingBox();
	}
	
	public FlatSphereIslandPiece(StructureManager manager, CompoundTag tag)
	{
		super(StructureTypes.FLAT_SPHERE_ISLAND, tag);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos)
	{
		for (int x = box.minX; x <= box.maxX; x++)
		{
			bPos.setX(x);
			for (int y = box.maxY; y >= box.minY; y--)
			{
				bPos.setY(y);
				for (int z = box.minZ; z <= box.maxZ; z++)
				{
					bPos.setZ(z);
					float d = sphereSDF(bPos, center, radius);
					if (d < 0)
					{
						d += noise(bPos, 0.07) * 15 + noise(bPos, 0.12) * 7 + gradient(bPos, center) * 2;
						if (d < 0)
						{
							world.setBlockState(bPos, world.isAir(bPos.up()) ? GRASS : STONE, 0);
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void toNbt(CompoundTag tag)
	{
		tag.putInt("radius", radius);
		tag.put("center", NbtHelper.fromBlockPos(center));
	}
	
	@Override
	protected void fromNbt(CompoundTag tag)
	{
		radius = tag.getInt("radius");
		center = NbtHelper.toBlockPos(tag.getCompound("center"));
	}

	@Override
	protected void makeBoundingBox()
	{
		int x1 = center.getX() - radius;
		int y1 = center.getY() - radius;
		int z1 = center.getZ() - radius;
		int x2 = center.getX() + radius;
		int y2 = center.getY() + radius;
		int z2 = center.getZ() + radius;
		this.boundingBox = new BlockBox(x1, y1, z1, x2, y2, z2);
	}
	
	protected float sphereSDF(BlockPos pos, BlockPos center, float radius)
	{
		return (float) Math.sqrt(pos.getSquaredDistance(center)) - radius;
	}
	
	protected float gradient(BlockPos pos, BlockPos center)
	{
		if (pos.getY() <= center.getY())
			return 0;
		else
		{
			return pos.getY() - center.getY();
		}
	}
	
	protected float noise(BlockPos pos, double scale)
	{
		return (float) noise.eval(pos.getX() * scale, pos.getY() * scale, pos.getZ() * scale) * 0.5F + 0.5F;
	}
}
