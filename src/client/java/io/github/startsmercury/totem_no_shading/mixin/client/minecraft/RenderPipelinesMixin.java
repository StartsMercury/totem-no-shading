package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin {
    @Shadow
    private static RenderPipeline register(RenderPipeline renderPipeline) {
        throw new AssertionError();
    }

    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = """
                Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder; \
                build (                                              \
                ) Lcom/mojang/blaze3d/pipeline/RenderPipeline;       \
            """,
            ordinal = 0
        ),
        slice = @Slice(from = @At(
            value = "CONSTANT",
            args = "stringValue=pipeline/item_translucent"
        )),
	remap = false
    )
    private static RenderPipeline createCustom(
        final RenderPipeline.Builder builder,
        final Operation<RenderPipeline> original
    ) {
        final var pipeline = original.call(builder);

        final var customVertexShader = pipeline
            .getVertexShader()
            .withPath(path -> path + TotemNoShadingImpl.CUSTOM_SHADER_SUFFIX);
        TotemNoShadingImpl.RENDERPIPELINE_ITEM_TRANSLUCENT = builder
            .withVertexShader(customVertexShader)
            .build();

        return pipeline;
    }

    @Inject(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = """
                Lnet/minecraft/client/renderer/RenderPipelines;  \
                register (                                       \
                    Lcom/mojang/blaze3d/pipeline/RenderPipeline; \
                ) Lcom/mojang/blaze3d/pipeline/RenderPipeline;   \
            """,
            ordinal = 0
        ),
        slice = @Slice(from = @At(
            value = "CONSTANT",
            args = "stringValue=pipeline/item_translucent"
        ))
    )
    private static void registerCustom(final CallbackInfo callback) {
        TotemNoShadingImpl.RENDERPIPELINE_ITEM_TRANSLUCENT =
            register(TotemNoShadingImpl.RENDERPIPELINE_ITEM_TRANSLUCENT);
    }
}
