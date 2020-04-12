package paulevs.skyworld.structures.piece;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import paulevs.skyworld.math.MHelper;
import paulevs.skyworld.math.SDF;
import paulevs.skyworld.structures.StructureTypes;

public class ConeIslandPiece extends CustomPiece
{
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState GRASS = Blocks.GRASS_BLOCK.getDefaultState();
	private BlockPos center;
	private int radius;
	private BlockPos cone1Pos;
	private BlockPos cone2Pos;
	private int h1;
	private int h2;
	private float r2;
	private float r3;
	private float blend;
	private float noisePower;
	private float noiseScale;
	
	public ConeIslandPiece(BlockPos center, int radius, Random random)
	{
		super(StructureTypes.FLAT_SPHERE_ISLAND, random.nextInt());
		this.center = center;
		this.radius = radius;
		initValues();
		makeBoundingBox();
	}
	
	public ConeIslandPiece(StructureManager manager, CompoundTag tag)
	{
		super(StructureTypes.FLAT_SPHERE_ISLAND, tag);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos)
	{
		int minY = box.minY;//Math.max(box.minY, center.getY() - h1 - h2);
		int maxY = box.maxY;//Math.min(box.maxY, center.getY());
		for (int y = maxY; y >= minY; y--)
		{
			B_POS.setY(y);
			for (int x = box.minX; x <= box.maxX; x++)
			{
				B_POS.setX(x);
				for (int z = box.minZ; z <= box.maxZ; z++)
				{
					B_POS.setZ(z);
					float d = SDF.smoothUnion(SDF.coneSDF(B_POS, cone1Pos, h1, r2, radius), SDF.coneSDF(B_POS, cone2Pos, h2, r3, r2), blend);
					if (d < 0)
					{
						d += MHelper.noise(B_POS, noiseScale) * noisePower;
						if (d < 0)
						{
							world.setBlockState(B_POS, world.isAir(B_POS.up()) ? GRASS : STONE, 0);
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
		this.radius = tag.getInt("radius");
		this.center = NbtHelper.toBlockPos(tag.getCompound("center"));
		initValues();
	}

	@Override
	protected void makeBoundingBox()
	{
		int x1 = center.getX() - radius;
		int y1 = cone2Pos.getY() - radius;
		int z1 = center.getZ() - radius;
		int x2 = center.getX() + radius;
		int y2 = center.getY();
		int z2 = center.getZ() + radius;
		this.boundingBox = new BlockBox(x1, y1, z1, x2, y2, z2);
	}
	
	protected void initValues()
	{
		this.h1 = (int) Math.ceil(radius * 0.125F);
		this.h2 = radius - h1;
		this.cone1Pos = center.down(h1);
		this.cone2Pos = center.down(h1 + h2);
		this.r2 = radius * 0.5F;
		this.r3 = radius * 0.05F;
		this.blend = radius * 0.5F;
		this.noisePower = this.radius * 0.15F;// * 5.1F;
		this.noiseScale = 0.5F / (float) Math.log(this.radius);
	}
}
