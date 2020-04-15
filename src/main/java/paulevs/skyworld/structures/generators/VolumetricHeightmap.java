package paulevs.skyworld.structures.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.MathHelper;

public class VolumetricHeightmap
{
	private final Section[] sections = new Section[16];
	
	public VolumetricHeightmap()
	{
		for (int i = 0; i < 16; i++)
			sections[i] = new Section();
	}
	
	public void clear()
	{
		for (Section s: sections)
			s.clear();
	}
	
	public void addHeight(int x, int y, int z)
	{
		if (y >= 0 && y < 256)
			sections[y >> 4].addHeight(x, z, y);
	}
	
	public int getRandomHeight(int x, int z, int section, Random random)
	{
		return sections[section].getRandomHeight(x, z, random);
	}
	
	public static int getSection(int y)
	{
		return MathHelper.clamp(y >> 4, 0, 15);
	}
	
	private class Section
	{
		final List<Integer>[][] heights;
		
		@SuppressWarnings("unchecked")
		Section()
		{
			heights = new ArrayList[16][16];
			for (int x = 0; x < 16; x++)
				for (int z = 0; z < 16; z++)
					heights[x][z] = new ArrayList<Integer>();
		}
		
		void clear()
		{
			for (int x = 0; x < 16; x++)
				for (int z = 0; z < 16; z++)
					heights[x][z].clear();
		}
		
		void addHeight(int x, int z, int height)
		{
			heights[x][z].add(height);
		}
		
		int getRandomHeight(int x, int z, Random random)
		{
			if (heights[x][z].size() > 0)
			{
				return heights[x][z].get(random.nextInt(heights[x][z].size()));
			}
			else
			{
				return -1;
			}
		}
	}
}
