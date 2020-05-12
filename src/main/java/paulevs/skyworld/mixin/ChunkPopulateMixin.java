package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import paulevs.skyworld.SkyWorldType;
import paulevs.skyworld.generator.SkyWorldBiomeSource;

@Mixin(ChunkGenerator.class)
public abstract class ChunkPopulateMixin<C extends ChunkGeneratorConfig>
{
	@Inject(method = "generateFeatures", at = @At("HEAD"), cancellable = true)
    private void customPopulate(ChunkRegion region, CallbackInfo info)
	{
		if (region.getWorld().getGeneratorType() == SkyWorldType.SKY_WORLD)
		{
			generate(region, 8);
			generate(region, 64);
			info.cancel();
		}
	}
	
	private void generate(ChunkRegion region, int y)
	{
		Biome biome = region.getBiomeForNoiseGen(2, y / 4, 2);
		
		if (y < 32 && SkyWorldBiomeSource.isSurfaceBIome(biome))
			return;
		
		@SuppressWarnings("unchecked")
		ChunkGenerator<C> self = (ChunkGenerator<C>) (Object) this;
		int i = region.getCenterChunkX();
		int j = region.getCenterChunkZ();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		
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
}
