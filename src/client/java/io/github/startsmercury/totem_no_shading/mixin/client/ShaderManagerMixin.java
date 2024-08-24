package io.github.startsmercury.totem_no_shading.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.shaders.CompiledShader;
import io.github.startsmercury.totem_no_shading.impl.client.NoShadingGlslPreprocessor;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ShaderManager.class)
public abstract class ShaderManagerMixin {
    @ModifyExpressionValue(
        method = "compileProgram",
        at = @At(value = "INVOKE", target = """
            Lnet/minecraft/client/renderer/ShaderProgram;configId()\
            Lnet/minecraft/resources/ResourceLocation;\
        """)
    )
    private ResourceLocation changeProgramName(
        final ResourceLocation configId,
        final @Share("noShading") LocalBooleanRef noShading
    ) {
        if (configId.getPath().endsWith(TotemNoShadingImpl.CUSTOM_SHADER_SUFFIX)) {
            noShading.set(true);
            return configId.withPath(path -> path.substring(
                0,
                path.length() - TotemNoShadingImpl.CUSTOM_SHADER_SUFFIX.length()
            ));
        } else {
            return configId;
        }
    }

    @ModifyExpressionValue(
        method = "compileProgram",
        at = @At(value = "INVOKE", target = """
            Lnet/minecraft/client/renderer/ShaderProgramConfig;vertex()\
            Lnet/minecraft/resources/ResourceLocation;\
        """)
    )
    private ResourceLocation changeVertexProgramName(
        final ResourceLocation configId,
        final @Share("noShading") LocalBooleanRef noShading
    ) {
        if (noShading.get()) {
            return configId.withPath(
                path -> path + TotemNoShadingImpl.CUSTOM_SHADER_SUFFIX
            );
        } else {
            return configId;
        }
    }

    @Inject(
        method = "loadShader",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, remap = false, target = """
            Lcom/google/common/collect/ImmutableMap$Builder;put(\
                Ljava/lang/Object;Ljava/lang/Object;\
            )Lcom/google/common/collect/ImmutableMap$Builder;\
        """)
    )
    private static void loadCustomShader(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) ResourceLocation resourceLocation,
        final @Local(ordinal = 0, argsOnly = true) CompiledShader.Type type,
        final @Local(ordinal = 0, argsOnly = true) Map<ResourceLocation, Resource> map,
        final @Local(ordinal = 0, argsOnly = true) ImmutableMap.Builder<
            ShaderManager.ShaderSourceKey,
            String
        > builder,
        final @Local(ordinal = 1) ResourceLocation resourceLocation2,
        final @Local(ordinal = 0) String string
    ) {
        if (!TotemNoShadingImpl.TARGET_VSH_SHADER.equals(resourceLocation)) {
            return;
        }

        final var glslPreprocessor = new NoShadingGlslPreprocessor(
            resourceLocation.withPath(FileUtil::getFullResourcePath),
            map
        );

        builder.put(
            new ShaderManager.ShaderSourceKey(
                resourceLocation2.withPath(
                    path -> path + TotemNoShadingImpl.CUSTOM_SHADER_SUFFIX
                ),
                type
            ),
            String.join("", glslPreprocessor.process(string))
        );
    }
}
