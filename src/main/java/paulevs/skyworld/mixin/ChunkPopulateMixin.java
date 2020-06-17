package paulevs.skyworld.mixin;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.EntityCategory;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import paulevs.skyworld.IStructure;
import paulevs.skyworld.SkyWorldType;
import paulevs.skyworld.generator.SkyChunkGenerator;

@Mixin(ChunkGenerator.class)
public abstract class ChunkPopulateMixin<C extends ChunkGeneratorConfig>
{
	@Inject(method = "generateFeatures", at = @At("HEAD"), cancellable = true)
    private void customPopulate(ChunkRegion region, CallbackInfo info)
	{
		if (region.getWorld().getGeneratorType() == SkyWorldType.SKY_WORLD)
		{
			@SuppressWarnings("unchecked")
			ChunkGenerator<C> self = (ChunkGenerator<C>) (Object) this;
			if (self instanceof SkyChunkGenerator)
			{
				SkyChunkGenerator sky = (SkyChunkGenerator) self;
				if (sky.hasOcean())
					generate(region, 128);
			}
		}
	}
	
	private void generate(ChunkRegion region, int y)
	{
		@SuppressWarnings("unchecked")
		ChunkGenerator<C> self = (ChunkGenerator<C>) (Object) this;
		int i = region.getCenterChunkX();
		int j = region.getCenterChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		
		Biome biome = region.getBiomeForNoiseGen(i << 2, y / 4, j << 2);
		
		ChunkRandom chunkRandom = new ChunkRandom();
		long m = chunkRandom.setSeed(region.getSeed(), k, l);
		GenerationStep.Feature[] var11 = GenerationStep.Feature.values();
		int var12 = var11.length;

		for (int var13 = 0; var13 < var12; ++var13)
		{
			GenerationStep.Feature feature = var11[var13];

			try
			{
				biome.generateFeatureStep(feature, self, region, m, chunkRandom, blockPos);
			}
			catch (Exception var17)
			{
				CrashReport crashReport = CrashReport.create(var17, "Biome decoration");
				crashReport.addElement("Generation")
				.add("CenterX", (Object) i)
				.add("CenterZ", (Object) j)
				.add("Step", (Object) feature).add("Seed", (Object) m)
				.add("Biome", (Object) Registry.BIOME.getId(biome));
				throw new CrashException(crashReport);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Inject(
		method = "setStructureStarts(Lnet/minecraft/world/biome/source/BiomeAccess;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/Chunk;setStructureStart(Ljava/lang/String;Lnet/minecraft/structure/StructureStart;)V",
			shift = Shift.BEFORE),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void addIsland(BiomeAccess biomeAccess, Chunk chunk, ChunkGenerator chunkGenerator, StructureManager structureManager, CallbackInfo ci, Iterator var5, StructureFeature structureFeature, StructureStart structureStart2)
	{
		if (!(structureStart2.getFeature() instanceof MineshaftFeature))
		{
			IStructure structure = (IStructure) structureStart2;
			ChunkPos chunkPos = chunk.getPos();
	        Biome biome = biomeAccess.getBiome(new BlockPos(chunkPos.getStartX() + 9, 0, chunkPos.getStartZ() + 9));
			structure.addIsland(chunkGenerator, structureManager, chunkPos.x, chunkPos.z, biome);
		}
	}

	@Inject(method = "getEntitySpawnList", at = @At("HEAD"), cancellable = true)
	private void getSpawns(EntityCategory category, BlockPos pos, CallbackInfoReturnable<List<Biome.SpawnEntry>> info)
	{
		@SuppressWarnings("unchecked")
		ChunkGenerator<C> self = (ChunkGenerator<C>) (Object) this;
		info.setReturnValue(self.getBiomeSource().getBiomeForNoiseGen(pos.getX(), pos.getY(), pos.getZ()).getEntitySpawnList(category));
		info.cancel();
	}
}
