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
        TotemNoShadingImpl.ITEM_TRANSLUCENT = Util.memoize(texture -> {
            final var setup = RenderSetup.builder(TotemNoShadingImpl.RENDERPIPELINE_ITEM_TRANSLUCENT)
                .withTexture("Sampler0", texture)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .useLightmap()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("item_translucent", setup);
        });
    }
}
