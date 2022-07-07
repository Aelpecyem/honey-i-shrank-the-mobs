package io.github.aelpecyem.histm.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
	@Unique private float actualShadow;
	protected LivingEntityRendererMixin(Context context) {
		super(context);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(Context context, M entityModel, float f, CallbackInfo ci) {
		this.actualShadow = f;
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
					   CallbackInfo ci) {
		if (livingEntity.deathTime > 0) {
			float progress = Math.max(0, 1 - (livingEntity.deathTime + g - 1.0f) / 20.0f * 1.6f);
			this.shadowRadius = actualShadow * progress;
			matrixStack.scale(progress, progress, progress);
		} else {
			this.shadowRadius = actualShadow;
		}
	}
	@Inject(method = "setupTransforms", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V", ordinal = 1), cancellable = true)
	private void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
		ci.cancel();
	}
}
