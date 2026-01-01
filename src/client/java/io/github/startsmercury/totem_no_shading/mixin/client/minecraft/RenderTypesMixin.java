package io.github.startsmercury.totem_no_shading.mixin.client.minecraft;

import io.github.startsmercury.totem_no_shading.impl.client.TotemNoShadingImpl;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderTypes.class)
public abstract class RenderTypesMixin {
    static {
        TotemNoShadingImpl.ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(identifier -> {
            final var setup = RenderSetup.builder(TotemNoShadingImpl.RENDERPIPELINE_ITEM_ENTITY_TRANSLUCENT_CULL)
                .withTexture("Sampler0", identifier)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("item_entity_translucent_cull", setup);
        });
    }
}
