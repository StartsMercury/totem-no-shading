package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CoreShaders.class)
public abstract class CoreShadersMixin {
    @Shadow
    private static ShaderProgram register(String string, VertexFormat vertexFormat) {
        throw new AssertionError();
    }

    @Inject(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = """
                Lnet/minecraft/client/renderer/CoreShaders;     \
                register (                                      \
                    Ljava/lang/String;                          \
                    Lcom/mojang/blaze3d/vertex/VertexFormat;    \
                ) Lnet/minecraft/client/renderer/ShaderProgram; \
            """,
            ordinal = 0
        ),
        slice = @Slice(from = @At(
            value = "CONSTANT",
            args = "stringValue=rendertype_item_entity_translucent_cull"
        ))
    )
    private static void registerCustom(final CallbackInfo callback) {
        TotemNoShadingImpl.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL = register(
            "rendertype_item_entity_translucent_cull_no_shading",
            DefaultVertexFormat.NEW_ENTITY
        );
    }
}
