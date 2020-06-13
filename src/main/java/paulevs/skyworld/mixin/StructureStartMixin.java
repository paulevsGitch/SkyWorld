package paulevs.skyworld.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.IStructure;
import paulevs.skyworld.generator.SkyWorldBiomeSource;
import paulevs.skyworld.structures.features.IslandFeature;
import paulevs.skyworld.structures.generators.Generators;
import paulevs.skyworld.structures.piece.IslandPiece;

@Mixin(StructureStart.class)
public class StructureStartMixin implements IStructure
{
	@Shadow
	@Final
	protected List<StructurePiece> children;
	
	@Shadow
	protected BlockBox boundingBox;
	
	@Shadow
	@Final
	protected ChunkRandom random;
	
	@Shadow
	protected void setBoundingBoxFromChildren() {}
	
	@Shadow
	public StructureFeature<?> getFeature() { return null; }
	
	
	public void addIsland(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome)
	{
		if (chunkGenerator.getBiomeSource() instanceof SkyWorldBiomeSource && !(getFeature() instanceof IslandFeature))
		{
			BlockPos blockPos = new BlockPos(
				(this.boundingBox.maxX + this.boundingBox.minX) * 0.5F,
				chunkGenerator.getHeightInGround(x, x, Type.WORLD_SURFACE) + 8,
				(this.boundingBox.maxZ + this.boundingBox.minZ) * 0.5F
			);
			int radius = (int) (Math.max((this.boundingBox.maxX - this.boundingBox.minX), (this.boundingBox.maxZ - this.boundingBox.minZ)) * 0.75);
			this.children.add(0, new IslandPiece(blockPos, radius, random, Generators.FEATURE_ISLAND));
			
	        this.setBoundingBoxFromChildren();
			System.out.println(getFeature());
		}
	}
}
