package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import paulevs.skyworld.SkyWorldType;

@Mixin(Dimension.class)
public abstract class DimensionRenderMixin
{
	@Shadow
	@Final
	protected World world;
	
	@Inject(method = "getHorizonShadingRatio", at = @At("HEAD"), cancellable = true)
	private void shadingRatio(CallbackInfoReturnable<Double> info)
	{
		if (this.world.getLevelProperties().getGeneratorType() == SkyWorldType.SKY_WORLD)
		{
			info.setReturnValue(0.5);
			info.cancel();
		}
	}
	
	@Inject(method = "getCloudHeight", at = @At("HEAD"), cancellable = true)
	private void cloudHeight(CallbackInfoReturnable<Float> info)
	{
		if (this.world.getLevelProperties().getGeneratorType() == SkyWorldType.SKY_WORLD)
		{
			info.setReturnValue(32F);
			info.cancel();
		}
	}
	
	@Inject(method = "hasGround", at = @At("HEAD"), cancellable = true)
	private void ground(CallbackInfoReturnable<Boolean> info)
	{
		info.setReturnValue(this.world.getLevelProperties().getGeneratorType() != SkyWorldType.SKY_WORLD);
		info.cancel();
	}
}
