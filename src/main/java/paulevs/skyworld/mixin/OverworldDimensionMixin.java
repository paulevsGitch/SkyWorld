package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import paulevs.skyworld.SkyChunkGenerator;
import paulevs.skyworld.SkyWorldType;

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
			BiomeSource biomeSource = new FixedBiomeSource(new FixedBiomeSourceConfig(null).setBiome(Biomes.BAMBOO_JUNGLE));
			
			FloatingIslandsChunkGeneratorConfig config = new FloatingIslandsChunkGeneratorConfig();
			config.withCenter(new BlockPos(0, 64, 0));
			config.setDefaultBlock(Blocks.DIAMOND_BLOCK.getDefaultState());
			config.setDefaultFluid(Blocks.ACACIA_PLANKS.getDefaultState());
			
			info.setReturnValue(SkyChunkGenerator.FLOATING_CHUNK_GEN.create(this.world, biomeSource, config));
			info.cancel();
		}
	}
}
