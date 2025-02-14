package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Function;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin {
    @Shadow
    private static RenderType.CompositeRenderType create(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, RenderPipeline renderPipeline, RenderType.CompositeState state) {
        throw new AssertionError();
    }

    @Final
    @Shadow
    private static Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL;

    static {
        TotemNoShadingImpl.ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(resourceLocation -> {
            var renderType = ITEM_ENTITY_TRANSLUCENT_CULL.apply(resourceLocation);
            return create(
                ((RenderStateShardAccessor) renderType).getName()
                    + TotemNoShadingImpl.CUSTOM_SHADER_SUFFIX,
                renderType.bufferSize(),
                renderType.affectsCrumbling(),
                renderType.sortOnUpload(),
                TotemNoShadingImpl.RENDERPIPELINE_ITEM_ENTITY_TRANSLUCENT_CULL,
                ((RenderType$CompositeRenderTypeAccessor) renderType).getState()
            );
        });
    }
}
