package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import paulevs.skyworld.SkyWorldType;
import paulevs.skyworld.generator.SkyChunkGenerator;
import paulevs.skyworld.generator.SkyWorldBiomeSource;
import paulevs.skyworld.generator.SkyWorldChunkGeneratorConfig;

@Mixin(OverworldDimension.class)
public abstract class OverworldDimensionMixin extends Dimension
{
	public OverworldDimensionMixin(World world, DimensionType type, float f)
	{
		super(world, type, f);
	}

	@Inject(method = "createChunkGenerator", at = @At("HEAD"), cancellable = true)
	private void makeChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> info)
	{
		LevelGeneratorType levelGeneratorType = this.world.getLevelProperties().getGeneratorType();
		if (levelGeneratorType == SkyWorldType.SKY_WORLD)
		{
			SkyWorldChunkGeneratorConfig config = new SkyWorldChunkGeneratorConfig();
			
			VanillaLayeredBiomeSourceConfig bSource = new VanillaLayeredBiomeSourceConfig(this.world.getLevelProperties());
			bSource.setGeneratorSettings(config);
			BiomeSource biomeSource = new SkyWorldBiomeSource(bSource);
			
			info.setReturnValue(SkyChunkGenerator.FLOATING_CHUNK_GEN.create(this.world, biomeSource, config));
			info.cancel();
		}
	}
}
