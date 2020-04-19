package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.VillageFeature;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import paulevs.skyworld.generator.SkyWorldBiomeSource;
import paulevs.skyworld.structures.generators.Generators;
import paulevs.skyworld.structures.piece.IslandPiece;

@Mixin(VillageFeature.Start.class)
public abstract class VillageFeatureMixin extends VillageStructureStart
{
	/*@Inject(method = "initialize", at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/structure/VillageGenerator;addPieces(Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/util/math/BlockPos;Ljava/util/List;Ljava/util/Random;Lnet/minecraft/world/gen/feature/VillageFeatureConfig;)V",
					shift = Shift.BEFORE),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void addIsland(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome, CallbackInfo info)
	{
		System.out.println("Village!");
	}*/

	public VillageFeatureMixin(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l)
	{
		super(structureFeature, i, j, blockBox, k, l);
		// TODO Auto-generated constructor stub
	}

	@Inject(method = "initialize", at = @At("HEAD"), cancellable = true)
	private void addIsland(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome, CallbackInfo info)
	{
		if (chunkGenerator.getBiomeSource() instanceof SkyWorldBiomeSource)
		{
			BlockPos blockPos = new BlockPos(x << 4, 64, z << 4); 
			this.children.add(new IslandPiece(blockPos, 128, random, Generators.getGenerator("cone")));
			
			VillageFeatureConfig villageFeatureConfig = (VillageFeatureConfig) chunkGenerator.getStructureConfig(biome, Feature.VILLAGE);
	        VillageGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, villageFeatureConfig);
	        this.setBoundingBoxFromChildren();
		}
	}
}
