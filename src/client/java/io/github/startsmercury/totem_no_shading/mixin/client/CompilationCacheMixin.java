package io.github.startsmercury.totem_no_shading.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.ShaderManager$CompilationCache")
public class CompilationCacheMixin {
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
}
