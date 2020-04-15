package paulevs.skyworld.mixin;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import paulevs.skyworld.SkyWorldType;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World
{
	protected ClientWorldMixin(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient)
	{
		super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
	}

	@Inject(method = "getSkyDarknessHeight", at = @At("HEAD"), cancellable = true)
	private void skyDarknessHeight(CallbackInfoReturnable<Double> info)
	{
		if (properties.getGeneratorType() == SkyWorldType.SKY_WORLD)
		{
			info.setReturnValue(-128D);
			info.cancel();
		}
	}
}
