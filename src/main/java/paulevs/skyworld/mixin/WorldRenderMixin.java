package paulevs.skyworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin
{
	@Shadow
	private ClientWorld world;
	
	@Shadow
	private int ticks;
	
	@Shadow
	private CloudRenderMode lastCloudsRenderMode;
	
	@Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;FDDD)V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(Lnet/minecraft/client/render/BufferBuilder;DDDLnet/minecraft/util/math/Vec3d;)V",
			shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void clouds(MatrixStack matrices, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo info,
			float f, float g, double d, double e, double h, double i, double j, float k, float l, float m, Vec3d vec3d, BufferBuilder bufferBuilder)//, double h, double i, double j, Vec3d vec3d)
	{
		//System.out.println(h + " " + h * 0.00390625F);
		renderCloudsBuf(bufferBuilder, h, i - 16, j, vec3d, 5, 7);
		renderCloudsBuf(bufferBuilder, h, i + 16, j, vec3d, -3, -2);
	}
	
	private void renderCloudsBuf(BufferBuilder builder, double x, double y, double z, Vec3d color, int offsetX, int offsetY)
	{
		float k = (float) MathHelper.floor(x + offsetX) * 0.00390625F;
		float l = (float) MathHelper.floor(z + offsetY) * 0.00390625F;
		float m = (float) color.x;
		float n = (float) color.y;
		float o = (float) color.z;
		float p = m * 0.9F;
		float q = n * 0.9F;
		float r = o * 0.9F;
		float s = m * 0.7F;
		float t = n * 0.7F;
		float u = o * 0.7F;
		float v = m * 0.8F;
		float w = n * 0.8F;
		float aa = o * 0.8F;
		float ab = (float) Math.floor(y / 4.0D) * 4.0F;
		if (this.lastCloudsRenderMode == CloudRenderMode.FANCY)
		{
			for (int ac = -3; ac <= 4; ++ac)
			{
				for (int ad = -3; ad <= 4; ++ad)
				{
					float ae = (float) (ac * 8);
					float af = (float) (ad * 8);
					if (ab > -5.0F)
					{
						builder.vertex((double) (ae + 0.0F), (double) (ab + 0.0F), (double) (af + 8.0F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
						builder.vertex((double) (ae + 8.0F), (double) (ab + 0.0F), (double) (af + 8.0F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
						builder.vertex((double) (ae + 8.0F), (double) (ab + 0.0F), (double) (af + 0.0F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
						builder.vertex((double) (ae + 0.0F), (double) (ab + 0.0F), (double) (af + 0.0F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
					}

					if (ab <= 5.0F)
					{
						builder.vertex((double) (ae + 0.0F), (double) (ab + 4.0F - 9.765625E-4F), (double) (af + 8.0F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
						builder.vertex((double) (ae + 8.0F), (double) (ab + 4.0F - 9.765625E-4F), (double) (af + 8.0F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
								.color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
						builder.vertex((double) (ae + 8.0F), (double) (ab + 4.0F - 9.765625E-4F), (double) (af + 0.0F))
								.texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
						builder.vertex((double) (ae + 0.0F), (double) (ab + 4.0F - 9.765625E-4F), (double) (af + 0.0F))
								.texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
								.color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
					}

					int aj;
					if (ac > -1)
					{
						for (aj = 0; aj < 8; ++aj)
						{
							builder.vertex((double) (ae + (float) aj + 0.0F), (double) (ab + 0.0F),
									(double) (af + 8.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
							builder.vertex((double) (ae + (float) aj + 0.0F), (double) (ab + 4.0F),
									(double) (af + 8.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
							builder.vertex((double) (ae + (float) aj + 0.0F), (double) (ab + 4.0F),
									(double) (af + 0.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
							builder.vertex((double) (ae + (float) aj + 0.0F), (double) (ab + 0.0F),
									(double) (af + 0.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
						}
					}

					if (ac <= 1)
					{
						for (aj = 0; aj < 8; ++aj)
						{
							builder.vertex((double) (ae + (float) aj + 1.0F - 9.765625E-4F), (double) (ab + 0.0F),
									(double) (af + 8.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
							builder.vertex((double) (ae + (float) aj + 1.0F - 9.765625E-4F), (double) (ab + 4.0F),
									(double) (af + 8.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
							builder.vertex((double) (ae + (float) aj + 1.0F - 9.765625E-4F), (double) (ab + 4.0F),
									(double) (af + 0.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
							builder.vertex((double) (ae + (float) aj + 1.0F - 9.765625E-4F), (double) (ab + 0.0F),
									(double) (af + 0.0F))
									.texture((ae + (float) aj + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l)
									.color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
						}
					}

					if (ad > -1)
					{
						for (aj = 0; aj < 8; ++aj)
						{
							builder.vertex((double) (ae + 0.0F), (double) (ab + 4.0F),
									(double) (af + (float) aj + 0.0F))
									.texture((ae + 0.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
							builder.vertex((double) (ae + 8.0F), (double) (ab + 4.0F),
									(double) (af + (float) aj + 0.0F))
									.texture((ae + 8.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
							builder.vertex((double) (ae + 8.0F), (double) (ab + 0.0F),
									(double) (af + (float) aj + 0.0F))
									.texture((ae + 8.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
							builder.vertex((double) (ae + 0.0F), (double) (ab + 0.0F),
									(double) (af + (float) aj + 0.0F))
									.texture((ae + 0.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
						}
					}

					if (ad <= 1)
					{
						for (aj = 0; aj < 8; ++aj)
						{
							builder.vertex((double) (ae + 0.0F), (double) (ab + 4.0F),
									(double) (af + (float) aj + 1.0F - 9.765625E-4F))
									.texture((ae + 0.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
							builder.vertex((double) (ae + 8.0F), (double) (ab + 4.0F),
									(double) (af + (float) aj + 1.0F - 9.765625E-4F))
									.texture((ae + 8.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
							builder.vertex((double) (ae + 8.0F), (double) (ab + 0.0F),
									(double) (af + (float) aj + 1.0F - 9.765625E-4F))
									.texture((ae + 8.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
							builder.vertex((double) (ae + 0.0F), (double) (ab + 0.0F),
									(double) (af + (float) aj + 1.0F - 9.765625E-4F))
									.texture((ae + 0.0F) * 0.00390625F + k, (af + (float) aj + 0.5F) * 0.00390625F + l)
									.color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
						}
					}
				}
			}
		}
		else
		{
			for (int am = -32; am < 32; am += 32)
			{
				for (int an = -32; an < 32; an += 32)
				{
					builder.vertex((double) (am + 0), (double) ab, (double) (an + 32))
							.texture((float) (am + 0) * 0.00390625F + k, (float) (an + 32) * 0.00390625F + l)
							.color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
					builder.vertex((double) (am + 32), (double) ab, (double) (an + 32))
							.texture((float) (am + 32) * 0.00390625F + k, (float) (an + 32) * 0.00390625F + l)
							.color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
					builder.vertex((double) (am + 32), (double) ab, (double) (an + 0))
							.texture((float) (am + 32) * 0.00390625F + k, (float) (an + 0) * 0.00390625F + l)
							.color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
					builder.vertex((double) (am + 0), (double) ab, (double) (an + 0))
							.texture((float) (am + 0) * 0.00390625F + k, (float) (an + 0) * 0.00390625F + l)
							.color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
				}
			}
		}
	}
}