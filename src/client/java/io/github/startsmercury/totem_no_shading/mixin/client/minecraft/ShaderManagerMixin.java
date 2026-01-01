package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.ShaderType;
import io.github.startsmercury.totem_no_shading.impl.client.NoShadingGlslPreprocessor;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FileUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ShaderManager.class)
public abstract class ShaderManagerMixin {
    @Inject(
        method = """
            loadShader (                                         \
                Lnet/minecraft/resources/Identifier;             \
                Lnet/minecraft/server/packs/resources/Resource;  \
                Lcom/mojang/blaze3d/shaders/ShaderType;          \
                Ljava/util/Map;                                  \
                Lcom/google/common/collect/ImmutableMap$Builder; \
            ) V                                                  \
        """,
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, remap = false, target = """
            Lcom/google/common/collect/ImmutableMap$Builder;   \
            put (                                              \
                Ljava/lang/Object;Ljava/lang/Object;           \
            ) Lcom/google/common/collect/ImmutableMap$Builder; \
        """)
    )
    private static void loadCustomShader(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) Identifier resourceLocation,
        final @Local(ordinal = 0, argsOnly = true) ShaderType shaderType,
        final @Local(ordinal = 0, argsOnly = true) Map<Identifier, Resource> map,
        final @Local(ordinal = 0, argsOnly = true) ImmutableMap.Builder<
            ShaderManager.ShaderSourceKey,
            String
        > builder,
        final @Local(ordinal = 1) Identifier resourceLocation2,
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
                shaderType
            ),
            String.join("", glslPreprocessor.process(string))
        );
    }
}
