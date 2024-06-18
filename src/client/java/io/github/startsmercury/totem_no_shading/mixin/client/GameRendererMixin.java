package io.github.startsmercury.totem_no_shading.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import io.github.startsmercury.totem_no_shading.impl.client.ResourceProviderWrapper;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
        method = "reloadShaders(Lnet/minecraft/server/packs/resources/ResourceProvider;)V",
        at = @At(
            value = "INVOKE",
            slice = "after_rendertype_entity_translucent_cull",
            shift = At.Shift.AFTER,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 0
        ),
        slice = @Slice(
            id = "after_rendertype_entity_translucent_cull",
            from = @At(value = "CONSTANT", args = "stringValue=rendertype_entity_translucent_cull")
        )
    )
    private void withReloadShader(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) ResourceProvider resourceProvider,
        final @Local(ordinal = 1) List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list2
    ) throws IOException {
        list2.add(Pair.of(
            new ShaderInstance(
                new ResourceProviderWrapper(resourceProvider),
                "rendertype_entity_translucent_cull",
                DefaultVertexFormat.NEW_ENTITY
            ),
            shaderInstance -> TotemNoShadingImpl.rendertypeEntityTranslucentCullShader = shaderInstance
        ));
    }
}
