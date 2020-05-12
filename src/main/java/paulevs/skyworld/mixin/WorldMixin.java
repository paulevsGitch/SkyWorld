package paulevs.skyworld.mixin;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import paulevs.skyworld.SkyBiomeAccessType;
import paulevs.skyworld.SkyWorldType;

@Mixin(World.class)
public class WorldMixin
{
	private static final BiomeAccess[] ACCES = new BiomeAccess[1];
	
	@Inject(method = "<init>*", at = @At("RETURN"))
	private void onInit(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient, CallbackInfo info)
	{
		ACCES[0] = new BiomeAccess((World) (Object) this, isClient ? levelProperties.getSeed() : LevelProperties.sha256Hash(levelProperties.getSeed()),  SkyBiomeAccessType.INSTANCE);
	}
	
	public BiomeAccess getBiomeAccess()
	{
		@SuppressWarnings("resource")
		World self = (World) (Object) this;
		return self.getGeneratorType() == SkyWorldType.SKY_WORLD ? ACCES[0] : self.getBiomeAccess();
	}
}
