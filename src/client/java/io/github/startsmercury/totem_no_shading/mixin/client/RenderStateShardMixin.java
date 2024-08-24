package io.github.startsmercury.totem_no_shading.mixin.client;

import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderStateShard.class)
public class RenderStateShardMixin {
    static {
        TotemNoShadingImpl.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER =
            new RenderStateShard.ShaderStateShard(
                TotemNoShadingImpl.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL
            );
    }
}
