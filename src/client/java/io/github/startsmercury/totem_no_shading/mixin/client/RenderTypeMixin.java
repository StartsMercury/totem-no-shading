package io.github.startsmercury.totem_no_shading.mixin.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.TriState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderType.class)
public class RenderTypeMixin {
    static {
        TotemNoShadingImpl.ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(
            resourceLocation -> {
                RenderType.CompositeState compositeState = RenderType.CompositeState
                    .builder()
                    .setShaderState(
                        TotemNoShadingImpl.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER
                    )
                    .setTextureState(new RenderStateShard.TextureStateShard(
                        resourceLocation,
                        TriState.FALSE,
                        false
                    ))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(true);
                return RenderType.create(
                    "item_entity_translucent_cull",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    true,
                    compositeState
                );
            }
        );
    }
}
