package paulevs.skyworld.structures.features;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class FoliagePair
{
	private static int globalID = 0;
	private final int hashCode;
	private final BlockStateProvider leaves;
	private final BlockStateProvider trunk;
	
	public FoliagePair(BlockStateProvider trunk, BlockStateProvider leaves)
	{
		this.trunk = trunk;
		this.leaves = leaves;
		this.hashCode = globalID++;
	}
	
	public void setLeaves(IWorld world, BlockPos pos, Random random)
	{
		world.setBlockState(pos, leaves.getBlockState(random, pos), 0);
	}
	
	public void setTrunk(IWorld world, BlockPos pos, Random random)
	{
		world.setBlockState(pos, trunk.getBlockState(random, pos), 0);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FoliagePair)
		{
			FoliagePair pair = (FoliagePair) obj;
			return pair.leaves.equals(leaves) && pair.trunk.equals(trunk);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return hashCode;
	}
}